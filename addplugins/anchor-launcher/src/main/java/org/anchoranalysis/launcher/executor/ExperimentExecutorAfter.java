package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.factory.AnchorDefaultBeanFactory;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.io.IReplaceInputManager;
import org.anchoranalysis.experiment.io.IReplaceOutputManager;
import org.anchoranalysis.experiment.io.IReplaceTask;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.image.bean.RegisterBeanFactoriesImage;
import org.anchoranalysis.image.io.bean.RegisterBeanFactoriesIO;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bean.OutputManager;

/*
 * #%L
 * anchor-browser
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

/**
 * Executes an experiment in different ways - AFTER an experiment bean exists.
 *
 * <p>We do not print any error messages to the console, but throw any errors in the form of
 * ExperimentExecutionException which can be translated elsewhere into nice error messages
 *
 * @author Owen Feehan
 */
class ExperimentExecutorAfter {

    private static Optional<Set<String>> defaultExtensions = Optional.empty();

    public ExperimentExecutorAfter(Path pathExecutionDirectory)
            throws ExperimentExecutionException {
        initializeIfNecessary(pathExecutionDirectory, true, true);
        // Only accessible through static methods
    }

    /**
     * Initialises our factories if not already done
     *
     * @param gui is the gui allowed on certain factories
     * @param pathExecutionDirectory a path to a directory from which the JAR is launched (typically
     *     the bin/ directory)
     * @param includeRootPaths if TRUE, a root bank is sought among the configurations and loaded
     * @throws ExperimentExecutionException
     */
    static void initializeIfNecessary(
            Path pathExecutionDirectory, boolean includeDefaultInstances, boolean includeRootPaths)
            throws ExperimentExecutionException {
        if (!RegisterBeanFactories.isCalledRegisterAllPackage()) {

            // We first register all bean-factories without any default instances, so we can load
            //  the default-instances from beans in a config-file
            AnchorDefaultBeanFactory defaultFactory =
                    RegisterBeanFactories.registerAllPackageBeanFactories();
            RegisterBeanFactoriesImage.registerBeanFactories();
            RegisterBeanFactoriesIO.registerBeanFactories();

            if (includeDefaultInstances) {
                // After loading the defaults, we add them to the factory
                defaultFactory
                        .getDefaultInstances()
                        .addFrom(
                                HelperLoadAdditionalConfig.loadDefaultInstances(
                                        pathExecutionDirectory));
            }

            if (includeRootPaths) {
                HelperLoadAdditionalConfig.loadRootPaths(pathExecutionDirectory);
            }

            defaultExtensions =
                    HelperLoadAdditionalConfig.loadDefaultExtensions(pathExecutionDirectory);
        }
    }

    /**
     * Executes an experiment, possibly replacing the input and output manager
     *
     * @param path a path to the file-system (can be a path to a file, or to a dolder)
     * @param ea experiment-arguments
     * @param pathInput if defined, the path to an input-manager to replace the input-manager
     *     specified in the experiment. If empty(), ignored.
     * @param pathOutput if defined, the path to an output-manager to replace the output-manager
     *     specified in the experiment. If empty(), ignored.
     * @param pathTask if defined, the path to a task to replace the task specified in the
     *     experiment. If empty(), ignored.
     * @throws ExperimentExecutionException if the execution ends early
     */
    public void executeExperiment(
            Experiment experiment,
            ExperimentExecutionArguments ea,
            Optional<Path> pathInput,
            Optional<Path> pathOutput,
            Optional<Path> pathTask)
            throws ExperimentExecutionException {

        if (!ea.hasInputFilterExtensions() && defaultExtensions.isPresent()) {
            // If no input-filter extensions have been specified and defaults are available, they
            // are inserted in
            ea.setInputFilterExtensions(defaultExtensions);
        }

        OptionalUtilities.ifPresent(pathInput, path -> replaceInputManager(experiment, path));

        OptionalUtilities.ifPresent(pathOutput, path -> replaceOutputManager(experiment, path));

        OptionalUtilities.ifPresent(pathTask, path -> replaceTask(experiment, path));

        executeExperiment(experiment, ea);
    }

    /**
     * Replaces the input-manager of an experiment with an input-manager declared at pathInput
     *
     * @param experiment experiment whose input-manager will be replaced
     * @param pathInput a path to a BeanXML file defining the replacement input-manager
     * @throws ExperimentExecutionException
     */
    private void replaceInputManager(Experiment experiment, Path pathInput)
            throws ExperimentExecutionException {

        // As path could be a folder, we make sure we get a file
        InputManager<InputFromManager> inputManager =
                ExperimentReader.readInputManagerFromXML(pathInput);

        try {
            if (experiment instanceof IReplaceInputManager) {
                IReplaceInputManager experimentCasted = (IReplaceInputManager) experiment;
                experimentCasted.replaceInputManager(inputManager);
            } else {
                throw new ExperimentExecutionException(
                        String.format(
                                "To override the input of an experiment, it must implement %s.%nThe current experiment does not: %s",
                                IReplaceInputManager.class.getName(),
                                experiment.getClass().getName()));
            }

        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot override the input of an experiment %s with input-manager type %s",
                            experiment.getClass().getName(), inputManager.getClass().getName()),
                    e);
        }
    }

    /**
     * Replaces the output-manager of an experiment with an output-manager declared at pathOutput
     *
     * @param experiment experiment whose input-manager will be replaced
     * @param pathOutput a path to a BeanXML file defining the replacement output-manager
     * @throws ExperimentExecutionException
     */
    private void replaceOutputManager(Experiment experiment, Path pathOutput)
            throws ExperimentExecutionException {

        // As path could be a folder, we make sure we get a file
        OutputManager outputManager = ExperimentReader.readOutputManagerFromXML(pathOutput);

        try {
            if (experiment instanceof IReplaceOutputManager) {
                IReplaceOutputManager experimentCasted = (IReplaceOutputManager) experiment;
                experimentCasted.replaceOutputManager(outputManager);
            } else {
                throw new ExperimentExecutionException(
                        String.format(
                                "To override the output of an experiment, it must implement %s.%nThe current experiment does not: %s",
                                IReplaceOutputManager.class.getName(),
                                experiment.getClass().getName()));
            }

        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot override the output of an experiment %s with input-manager type %s",
                            experiment.getClass().getName(), outputManager.getClass().getName()),
                    e);
        }
    }

    /**
     * Replaces the task of an experiment with an task declared at pathTask
     *
     * @param experiment experiment whose input-task will be replaced
     * @param pathTask a path to a BeanXML file defining the replacement task
     * @throws ExperimentExecutionException
     */
    @SuppressWarnings("unchecked")
    private void replaceTask(Experiment experiment, Path pathTask)
            throws ExperimentExecutionException {

        // As path could be a folder, we make sure we get a file
        Task<InputFromManager, Object> task = ExperimentReader.readTaskFromXML(pathTask);

        try {
            if (experiment instanceof IReplaceTask) {
                IReplaceTask<InputFromManager, Object> experimentCasted =
                        (IReplaceTask<InputFromManager, Object>) experiment;
                experimentCasted.replaceTask(task);
            } else {
                throw new ExperimentExecutionException(
                        String.format(
                                "To override the task of an experiment, it must implement %s.%nThe current experiment does not: %s",
                                IReplaceTask.class.getName(), experiment.getClass().getName()));
            }

        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot override the input of an experiment %s with task type %s",
                            experiment.getClass().getName(), task.getClass().getName()),
                    e);
        }
    }

    /**
     * Executes an experiment
     *
     * @param experimentsPath a path to a XML file describing an Experiment, or else to a path to a
     *     folder containing Experiment files
     * @param ea additional arguments that describe the Experiment
     * @throws ExperimentExecutionException if the experiment cannot be executed
     */
    private void executeExperiment(Experiment experiment, ExperimentExecutionArguments ea)
            throws ExperimentExecutionException {

        try {
            experiment.doExperiment(ea);

        } catch (ExperimentExecutionException e) {
            throw new ExperimentExecutionException("Experiment execution ended with failure", e);
        }
    }
}
