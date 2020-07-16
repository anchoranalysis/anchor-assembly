package org.anchoranalysis.launcher.executor;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.anchoranalysis.launcher.parser.ParseArgsAndRunExperiment;

/**
 * Determines where the files passed the ExperimentExecutor are loaded from.
 *
 * <p><div> This can be:
 *
 * <ul>
 *   <li>where the input-manager or output-manager is found
 *   <li>different other execution arguments
 * </ul>
 *
 * </div>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ExperimentExecutor {

    // START REQUIRED ARGUMENTS
    /** the experiment to run */
    private final SelectParam<Path> experiment;

    /** the directory where configuration files are stored */
    @Getter private final Path configDir;

    /** the directory from which the experiment is executed */
    private final Path executionDir;
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
            ExperimentExecutionArguments execArgs,
            boolean alwaysShowExperimentArgs,
            MessageLogger logger)
            throws ExperimentExecutionException {

        ExperimentExecutorAfter delegate = new ExperimentExecutorAfter(executionDir);

        if (defaultBehaviourString.isPresent() && areAllDefault()) {
            // Special behaviour if everything has defaults
            logger.logFormatted(
                    "%s.%nLearn how to select inputs, outputs and tasks with 'anchor -%s'.%n",
                    defaultBehaviourString.get(), ParseArgsAndRunExperiment.OPTION_HELP);
        }

        Experiment experimentLoaded = loadExperimentFromPath(execArgs);

        if (alwaysShowExperimentArgs || experimentLoaded.useDetailedLogging()) {
            logger.log(describe());
        }

        setupModelDirectory(configDir, execArgs);

        delegate.executeExperiment(
                experimentLoaded,
                execArgs,
                getInput().select(execArgs),
                getOutput().select(execArgs),
                getTask().select(execArgs));
    }

    private void setupModelDirectory(
            Path pathExecutionDirectory, ExperimentExecutionArguments execArgs) {
        // Set model directory, assuming that the directory is called from bin/
        execArgs.setModelDirectory(
                pathExecutionDirectory.getParent().resolve("models").normalize().toAbsolutePath());
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

    private Experiment loadExperimentFromPath(ExperimentExecutionArguments execArgs)
            throws ExperimentExecutionException {
        return ExperimentReader.readExperimentFromXML(experiment.select(execArgs));
    }
}
