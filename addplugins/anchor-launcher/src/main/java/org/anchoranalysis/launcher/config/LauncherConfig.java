package org.anchoranalysis.launcher.config;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.nio.file.Path;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.executor.ExperimentExecutorFactory;
import org.anchoranalysis.launcher.resources.Resources;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Specifies a configuration of the launcher for a particular application.
 *
 * <p>As the launcher class is used for starting both the command-line tool and the Anchor GUI, this
 * provides the necessary application-specific configuration for each.
 *
 * @author Owen Feehan
 */
public abstract class LauncherConfig {

    /** Config for resources sued by the launcher */
    public abstract Resources resources();

    /** Config for displaying help message */
    public abstract HelpConfig help();

    /**
     * if true, then some extra newlines are inserted before error messages
     *
     * <p>This useful for the GUI client in Windows due to WinRun4j running as a Windows app, and
     * not as a shell app. This changes how output is displayed;
     */
    public abstract boolean newlinesBeforeError();

    public abstract ExecutionArguments createArguments(CommandLine line)
            throws ExperimentExecutionException;

    public abstract void addAdditionalOptions(Options options);

    public ExperimentExecutor createExperimentExecutor(CommandLine line)
            throws ExperimentExecutionException {

        Path pathCurrentJARDir = PathCurrentJarHelper.pathCurrentJAR(classInCurrentJar());

        Path pathDefaultExperiment = pathDefaultExperiment(pathCurrentJARDir);

        // Assumes config-dir is always the directory of defaultExperiment.xml
        return ExperimentExecutorFactory.create(
                line, pathDefaultExperiment, pathDefaultExperiment.getParent(), pathCurrentJARDir);
    }

    public abstract void customizeExperimentExecutor(ExperimentExecutor executor, CommandLine line)
            throws ExperimentExecutionException;

    /**
     * path to a property file that defines a relative-path to the default experiment in bean XML
     */
    protected abstract String pathRelativeProperties();

    /** a class which we use to determine the base location for pathRelativeProperties */
    protected abstract Class<?> classInCurrentJar();

    /**
     * Path to default-experiment
     *
     * @return a path to the defaultExperiment
     * @throws ExperimentExecutionException
     */
    private Path pathDefaultExperiment(Path pathCurrentJARDir) throws ExperimentExecutionException {
        return PathDeriver.pathDefaultExperiment(pathCurrentJARDir, pathRelativeProperties());
    }
}
