package org.anchoranalysis.launcher.executor;

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
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.anchoranalysis.launcher.options.CommandLineOptions;

/**
 * Runs a particular experiment after identifying necessary paths and input files.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ExperimentExecutor {

    private static final String TASKS_SUBDIRECTORY_NAME = "tasks";

    // START REQUIRED ARGUMENTS
    /** The experiment to run. */
    private final SelectParam<Path> experiment;

    /** The directory where configuration files are stored. */
    private final Path configDirectory;
    // END REQUIRED ARGUMENTS

    @Getter @Setter private SelectParam<Optional<Path>> input = SelectParamFactory.useDefault();

    @Getter @Setter private SelectParam<Optional<Path>> output = SelectParamFactory.useDefault();

    @Getter @Setter private SelectParam<Optional<Path>> task = SelectParamFactory.useDefault();

    /**
     * Whether to open the output directory in the desktop GUI after execution (if supported on the
     * O/S).
     */
    @Getter @Setter private boolean openInDesktop = false;

    /**
     * If present, a string is printed in the description if the default-experiment is used,
     * otherwise ignored.
     */
    @Setter private Optional<String> defaultBehaviourString = Optional.empty();

    /**
     * Executes an experiment after finding a single experiment XML file, and reading the experiment
     * from this file.
     *
     * @throws ExperimentExecutionException if the execution ends early
     */
    public void executeExperiment(
            ExecutionArguments executionArguments,
            boolean alwaysShowExperimentArguments,
            Logger logger)
            throws ExperimentExecutionException {

        if (openInDesktop) {
            Consumer<Path> desktopOpener =
                    path -> DesktopPathOpener.openPathInDesktop(path, logger.errorReporter());
            executionArguments.input().assignCallUponDirectoryCreation(desktopOpener);
        }

        ExperimentExecutorAfter delegate = new ExperimentExecutorAfter(configDirectory);

        if (defaultBehaviourString.isPresent() && areAllDefault()) {
            // Special behaviour if everything has defaults
            logger.messageLogger()
                    .logFormatted(
                            "%s.%nLearn how to select inputs, outputs and tasks with 'anchor -%s'.%n",
                            defaultBehaviourString.get(), // NOSONAR
                            CommandLineOptions.SHORT_OPTION_HELP);
        }

        Experiment experimentLoaded = loadExperimentFromPath(executionArguments);

        if (alwaysShowExperimentArguments || experimentLoaded.useDetailedLogging()) {
            logger.messageLogger().log(describe());
        }

        setupModelDirectory(configDirectory, executionArguments);

        delegate.executeExperiment(
                experimentLoaded,
                executionArguments,
                getInput().select(executionArguments),
                getOutput().select(executionArguments),
                getTask().select(executionArguments));
    }

    public Path taskDirectory() {
        return configDirectory.resolve(TASKS_SUBDIRECTORY_NAME);
    }

    private void setupModelDirectory(Path pathExecutionDirectory, ExecutionArguments execArgs) {
        // Set model directory, assuming that the directory is called from bin/
        execArgs.input()
                .assignModelDirectory(
                        pathExecutionDirectory
                                .getParent()
                                .resolve("models")
                                .normalize()
                                .toAbsolutePath());
    }

    /**
     * Constructs a summary string to describe how the experiment is being executed.
     *
     * @throws ExperimentExecutionException
     */
    private String describe() throws ExperimentExecutionException {
        return String.format(
                "%s%s%n", describeExperiment(), SelectPathDescriber.describe(input, output, task));
    }

    private String describeExperiment() throws ExperimentExecutionException {
        return String.format("Executing %s", experiment.describe());
    }

    private boolean areAllDefault() {
        return experiment.isDefault()
                && input.isDefault()
                && output.isDefault()
                && task.isDefault();
    }

    private Experiment loadExperimentFromPath(ExecutionArguments execArgs)
            throws ExperimentExecutionException {
        return BeanReader.readExperimentFromXML(experiment.select(execArgs));
    }
}
