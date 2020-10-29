/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.exception.BeanXmlException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.plugin.io.input.path.RootPathMap;

// Loads additional configuration (for executing an experiment) from the filesystem
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperLoadAdditionalConfig {

    /** Name of anchor subdirectory in user directory */
    private static final String ANCHOR_USER_SUBDIR = ".anchor/";

    /** Name of anchor config subdirectory in home directory */
    private static final String ANCHOR_HOME_CONFIG = "config/";

    /** Filename (relative to anchor root) for default instances config file */
    private static final String DEFAULT_INSTANCES_FILENAME = "defaultBeans.xml";

    /** Filename (relative to anchor root) for default extensions */
    private static final String DEFAULT_EXTENSIONS_FILENAME = "defaultInputExtensions.xml";

    /** Filename (relative to anchor root) for root path map */
    private static final String ROOT_PATH_MAP_FILENAME = "rootPaths.xml";

    /**
     * Name of the environment variable that indicates the ANCHOR home directory (i.e. directory in
     * which bin/ exists, from which Anchor is executed)
     */
    private static final String ANCHOR_HOME_ENV_VAR_NAME = "ANCHOR_HOME";

    public static BeanInstanceMap loadDefaultInstances(Path pathExecutionDirectory)
            throws ExperimentExecutionException {

        Path pathHome =
                getAnchorHome(pathExecutionDirectory)
                        .resolve(ANCHOR_HOME_CONFIG)
                        .resolve(DEFAULT_INSTANCES_FILENAME)
                        .normalize();
        Path pathUser = getAnchorUserDir().resolve(DEFAULT_INSTANCES_FILENAME).normalize();

        if (!pathHome.toFile().exists() && !pathUser.toFile().exists()) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot find a config file for defaultBean instances, looking at:%n%s%n%s",
                            pathHome, pathUser));
        }

        BeanInstanceMap map = new BeanInstanceMap();
        addDefaultInstancesFromDir(pathHome, map);
        addDefaultInstancesFromDir(pathUser, map);
        return map;
    }

    /**
     * Loads a set of default file extensions from the config/ directory
     *
     * @param pathExecutionDirectory the directory in which anchor is executed (i.e. the bin/)
     * @return the set of extensions (they should be without any period, and in lower-case) or null
     *     if the file doesn't exist
     * @throws ExperimentExecutionException
     */
    public static Optional<Set<String>> loadDefaultExtensions(Path pathExecutionDirectory)
            throws ExperimentExecutionException {

        Path path =
                getAnchorHome(pathExecutionDirectory)
                        .resolve(ANCHOR_HOME_CONFIG)
                        .resolve(DEFAULT_EXTENSIONS_FILENAME)
                        .normalize();

        if (path.toFile().exists()) {
            try {
                StringSet setBean = BeanXmlLoader.loadBean(path, "bean");
                return Optional.of(setBean.set());

            } catch (BeanXmlException e) {
                throw new ExperimentExecutionException(
                        String.format("An error occurred loading bean XML from %s", path), e);
            }
        }

        return Optional.empty();
    }

    private static void addDefaultInstancesFromDir(Path path, BeanInstanceMap addToMap)
            throws ExperimentExecutionException {
        if (path.toFile().exists()) {
            try {
                List<NamedBean<?>> listDefaults = BeanXmlLoader.loadBean(path, "bean");
                addToMap.addFrom(listDefaults);

            } catch (BeanXmlException | BeanMisconfiguredException e) {
                throw new ExperimentExecutionException(
                        String.format("An error occurred loading bean XML from %s", path), e);
            }
        }
    }

    public static RootPathMap loadRootPaths(Path pathExecutionDirectory)
            throws ExperimentExecutionException {

        // First we look in the Anchor Home directory
        // Then we look in the Anchor User directory

        Path pathHome =
                getAnchorHome(pathExecutionDirectory)
                        .resolve(ANCHOR_HOME_CONFIG)
                        .resolve(ROOT_PATH_MAP_FILENAME)
                        .normalize();
        Path pathUser = getAnchorUserDir().resolve(ROOT_PATH_MAP_FILENAME).normalize();

        addRootPathsFromDir(pathHome, RootPathMap.instance());
        addRootPathsFromDir(pathUser, RootPathMap.instance());

        return RootPathMap.instance();
    }

    private static void addRootPathsFromDir(Path path, RootPathMap addToMap)
            throws ExperimentExecutionException {

        if (path.toFile().exists()) {
            try {
                addToMap.addFromXmlFile(path);
            } catch (OperationFailedException e) {
                throw new ExperimentExecutionException(
                        String.format(
                                "An error occurred adding a root-path from the XML file %s", path),
                        e);
            }
        }
    }

    /**
     * If ANCHOR_HOME environment variable is set, we use this as the definition of anchor_home,
     * otherwise we guess from where the JAR was executed from
     *
     * @param pathExecutionDirectory the home directory suggested by where the execution path
     * @return
     */
    private static Path getAnchorHome(Path pathExecutionDirectory) {
        String home = System.getenv(ANCHOR_HOME_ENV_VAR_NAME);
        if (home != null) {
            return Paths.get(home);
        } else {
            // Fall back to the execution directory
            return pathExecutionDirectory.resolve("..");
        }
    }

    private static Path getAnchorUserDir() {
        Path currentUsersHomeDir = Paths.get(System.getProperty("user.home"));
        return currentUsersHomeDir.resolve(ANCHOR_USER_SUBDIR);
    }
}
