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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.core.log.MessageLogger;
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
    /** the experiment to run */
    private final SelectParam<Path> experiment;

    /** the directory where configuration files are stored */
    private final Path configDirectory;

    /** the directory from which the experiment is executed */
    private final Path executionDirectory;
    // END REQUIRED ARGUMENTS

    @Getter @Setter private SelectParam<Optional<Path>> input = SelectParamFactory.useDefault();

    @Getter @Setter private SelectParam<Optional<Path>> output = SelectParamFactory.useDefault();

    @Getter @Setter private SelectParam<Optional<Path>> task = SelectParamFactory.useDefault();

    /**
     * If present, a string is printed in the description if the default-experiment is used,
     * otherwise ignored.
     */
    @Setter private Optional<String> defaultBehaviourString = Optional.empty();

    /**
     * Executes an experiment after finding a single experiment XML file, and reading the experiment
     * from this file
     *
     * @throws ExperimentExecutionException if the execution ends early
     */
    public void executeExperiment(
            ExecutionArguments executionArguments, boolean alwaysShowExperimentArguments, MessageLogger logger)
            throws ExperimentExecutionException {

        ExperimentExecutorAfter delegate = new ExperimentExecutorAfter(executionDirectory);

        if (defaultBehaviourString.isPresent() && areAllDefault()) {
            // Special behaviour if everything has defaults
            logger.logFormatted(
                    "%s.%nLearn how to select inputs, outputs and tasks with 'anchor -%s'.%n",
                    defaultBehaviourString.get(), // NOSONAR
                    CommandLineOptions.SHORT_OPTION_HELP);
        }

        Experiment experimentLoaded = loadExperimentFromPath(executionArguments);

        if (alwaysShowExperimentArguments || experimentLoaded.useDetailedLogging()) {
            logger.log(describe());
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
     * Constructs a summary string to describe how the experiment is being executed
     *
     * @throws ExperimentExecutionException
     */
    private String describe() throws ExperimentExecutionException {
        return String.format("%s%s%n", describeExperiment(), describeInputOutput());
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

    private void addToListIfNonDefault(
            SelectParam<Optional<Path>> selectParam, String textDscr, List<String> list)
            throws ExperimentExecutionException {
        if (!selectParam.isDefault()) {
            list.add(String.format("%s %s", textDscr, selectParam.describe()));
        }
    }

    private String describeInputOutput() throws ExperimentExecutionException {

        // Components
        List<String> list = new ArrayList<>();
        addToListIfNonDefault(input, "input", list);
        addToListIfNonDefault(output, "output", list);
        addToListIfNonDefault(task, "task", list);

        return collapseIntoOneLine(list);
    }

    private static String collapseIntoOneLine(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(i == 0 ? " with " : " and ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    private Experiment loadExperimentFromPath(ExecutionArguments execArgs)
            throws ExperimentExecutionException {
        return BeanReader.readExperimentFromXML(experiment.select(execArgs));
    }
}
