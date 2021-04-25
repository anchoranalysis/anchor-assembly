/*-
 * #%L
 * anchor-browser
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

package org.anchoranalysis.browser.launcher;

import java.util.Optional;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.resources.Resources;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * A command-line interface for launching the GUI browser
 *
 * @author Owen Feehan
 */
class LauncherConfigBrowser extends LauncherConfig {

    /** A path relative to the current JAR where a properties file can be found */
    private static final String PATH_RELATIVE_PROPERTIES = "anchorGUI.properties";

    private static final String RESOURCE_VERSION_FOOTER =
            "org/anchoranalysis/browser/launcher/versionFooterDisplayMessage.txt";
    private static final String RESOURCE_USAGE_HEADER =
            "org/anchoranalysis/browser/launcher/usageHeaderDisplayMessage.txt";
    private static final String RESOURCE_USAGE_FOOTER =
            "org/anchoranalysis/browser/launcher/usageFooterDisplayMessage.txt";
    private static final String RESOURCE_MAVEN_PROPERTIES =
            "META-INF/maven/org.anchoranalysis.anchor/anchor-browser/pom.properties";

    @Override
    public ExecutionArguments createArguments(CommandLine line) {
        return new ExecutionArguments();
    }

    @Override
    protected Class<?> classInCurrentJar() {
        return LauncherConfigBrowser.class;
    }

    @Override
    public boolean newlinesBeforeError() {
        return true;
    }

    @Override
    public void addAdditionalOptions(Options options) {
        // Nothing to do here for the browser
    }

    @Override
    public Resources resources() {
        return new Resources(
                getClass().getClassLoader(),
                RESOURCE_VERSION_FOOTER,
                RESOURCE_MAVEN_PROPERTIES,
                RESOURCE_USAGE_HEADER,
                RESOURCE_USAGE_FOOTER,
                Optional.empty());
    }

    @Override
    public HelpConfig help() {
        return new HelpConfig("anchorGUI", "configFile.xml");
    }

    @Override
    protected String pathRelativeProperties() {
        return PATH_RELATIVE_PROPERTIES;
    }

    @Override
    public void customizeExperimentExecutor(ExperimentExecutor template, CommandLine line) {
        // Nothing to do here for the browser
    }
}
