package org.anchoranalysis.launcher.config;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class PathDeriver {

    /** A property that indicates a relative path to a default properties file */
    private static final String PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT =
            "default.config.path.relative";

    /**
     * Path to default-experiment
     *
     * @return a path to the defaultExperiment
     * @throws ExperimentExecutionException
     */
    public static Path pathDefaultExperiment(Path pathCurrentJARDir, String pathRelativeProperties)
            throws ExperimentExecutionException {

        String relativePathDefaultExperiment =
                relativePathDefaultExperiment(
                        propertyPath(pathCurrentJARDir, pathRelativeProperties));

        return pathCurrentJARDir.resolve(relativePathDefaultExperiment);
    }

    private static String relativePathDefaultExperiment(Path propertyPath)
            throws ExperimentExecutionException {

        Properties props = new Properties();

        try {
            props.load(new FileInputStream(propertyPath.toFile()));
        } catch (IOException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "An error occurred loading properties from the properties file at %s)",
                            propertyPath),
                    e);
        }

        if (!props.containsKey(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT)) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Properties file is missing key: %s",
                            PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT));
        }

        return props.getProperty(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT);
    }

    private static Path propertyPath(Path currentJARDir, String pathRelativeProperties)
            throws ExperimentExecutionException {

        Path pathProperties = currentJARDir.resolve(pathRelativeProperties);

        if (!pathProperties.toFile().exists()) {
            throw new ExperimentExecutionException(
                    String.format("Cannot find properties file at: %s", pathProperties));
        }

        return pathProperties;
    }
}
