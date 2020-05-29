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

/**
 * Executes an experiment in different ways - AFTER an experiment object exists.
 * 
 * We do not print any error messages to the console, but throw any errors in the form of ExperimentExecutionException which
 * can be translated elsewhere into nice error messages
 * 
 * @author Owen Feehan
 *
 */
class ExperimentExecutorObj {
	
	private static Optional<Set<String>> defaultExtensions = Optional.empty();
	
	public ExperimentExecutorObj( Path pathExecutionDirectory ) throws ExperimentExecutionException {
		initializeIfNecessary(pathExecutionDirectory, true, true );
		// Only accessible through static methods
	}
	
	/**
	 * Initialises our factories if not already done
	 * 
	 * @param gui is the gui allowed on certain factories
	 * @param pathExecutionDirectory a path to a directory from which the JAR is launched (typically the bin/ directory)
	 * @param includeRootPaths if TRUE, a root bank is sought among the configurations and loaded
	 * @throws ExperimentExecutionException 
	 */
	static void initializeIfNecessary( Path pathExecutionDirectory, boolean includeDefaultInstances, boolean includeRootPaths ) throws ExperimentExecutionException {
		if (!RegisterBeanFactories.isCalledRegisterAllPackage()) {
			
			// We first register all bean-factories without any default instances, so we can load
			//  the default-instances from beans in a config-file
			AnchorDefaultBeanFactory defaultFactory = RegisterBeanFactories.registerAllPackageBeanFactories();
			RegisterBeanFactoriesImage.registerBeanFactories();
			RegisterBeanFactoriesIO.registerBeanFactories();
			
			if (includeDefaultInstances) {
				// After loading the defaults, we add them to the factory
				defaultFactory.getDefaultInstances().addFrom(
					HelperLoadAdditionalConfig.loadDefaultInstances(pathExecutionDirectory)
				);
			}
			
			if (includeRootPaths) {
				HelperLoadAdditionalConfig.loadRootPaths(pathExecutionDirectory);
			}
			
			defaultExtensions = HelperLoadAdditionalConfig.loadDefaultExtensions(pathExecutionDirectory);
		}
	}
	
	/**
	 * Executes an experiment, possibly replacing the input and output manager 
	 * 
	 * @param path a path to the file-system (can be a path to a file, or to a dolder)
	 * @param ea experiment-arguments
	 * @param pathInput if defined, the path to an input-manager to replace the input-manager specified in the experiment. If empty(), ignored.
	 * @param pathOutput if defined, the path to an output-manager to replace the output-manager specified in the experiment. If empty(), ignored.
	 * @param pathTask if defined, the path to a task to replace the task specified in the experiment. If empty(), ignored.
	 * @throws ExperimentExecutionException if the execution ends early
	 */
	public void executeExperiment(
		Experiment experiment,
		ExperimentExecutionArguments ea,
		Optional<Path> pathInput,
		Optional<Path> pathOutput,
		Optional<Path> pathTask
	) throws ExperimentExecutionException {		
				
		if (!ea.hasInputFilterExtensions() &&  defaultExtensions.isPresent()) {
			// If no input-filter extensions have been specified and defaults are available, they are inserted in
			ea.setInputFilterExtensions(defaultExtensions.get());
		}
		
		OptionalUtilities.ifPresent(
			pathInput,
			path-> replaceInputManager(experiment, ea, path)
		);
		
		OptionalUtilities.ifPresent(
			pathOutput,
			path-> replaceOutputManager(experiment, ea, path)
		);
		
		OptionalUtilities.ifPresent(
			pathTask,
			path -> replaceTask(experiment, ea, path)
		);
		
		executeExperiment( experiment, ea );		
	}
	
	/**
	 * Replaces the input-manager of an experiment with an input-manager declared at pathInput
	 * 
	 * @param experiment experiment whose input-manager will be replaced
	 * @param ea experiment-arguments
	 * @param pathInput a path to a BeanXML file defining the replacement input-manager
	 * @throws ExperimentExecutionException
	 */
	private void replaceInputManager( Experiment experiment, ExperimentExecutionArguments ea, Path pathInput ) throws ExperimentExecutionException {
		
		// As path could be a folder, we make sure we get a file
		InputManager<InputFromManager> inputManager = ExperimentReader.readInputManagerFromXML(	pathInput );
		
		try {
			if (experiment instanceof IReplaceInputManager) {
				IReplaceInputManager experimentCasted = (IReplaceInputManager) experiment;
				experimentCasted.replaceInputManager(inputManager);
			} else {
				throw new ExperimentExecutionException(
					String.format(
						"To override the input of an experiment, it must implement %s.%nThe current experiment does not: %s",
						IReplaceInputManager.class.getName(),
						experiment.getClass().getName()
					)
				);
			}
			
		} catch (OperationFailedException e) {
			throw new ExperimentExecutionException(
				String.format(
					"Cannot override the input of an experiment %s with input-manager type %s",
					experiment.getClass().getName(),
					inputManager.getClass().getName()
				),
				e
			);
		}
	}
	
	
	
	/**
	 * Replaces the output-manager of an experiment with an output-manager declared at pathOutput
	 * 
	 * @param experiment experiment whose input-manager will be replaced
	 * @param ea experiment-arguments
	 * @param pathOutput a path to a BeanXML file defining the replacement output-manager
	 * @throws ExperimentExecutionException
	 */
	private void replaceOutputManager( Experiment experiment, ExperimentExecutionArguments ea, Path pathOutput ) throws ExperimentExecutionException {
		
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
						experiment.getClass().getName()
					)
				);
			}
			
		} catch (OperationFailedException e) {
			throw new ExperimentExecutionException(
				String.format(
					"Cannot override the output of an experiment %s with input-manager type %s",
					experiment.getClass().getName(),
					outputManager.getClass().getName()
				),
				e
			);
		}
	}
	
	
	
	/**
	 * Replaces the task of an experiment with an task declared at pathTask
	 * 
	 * @param experiment experiment whose input-task will be replaced
	 * @param ea experiment-arguments
	 * @param pathTask a path to a BeanXML file defining the replacement task
	 * @throws ExperimentExecutionException
	 */
	@SuppressWarnings("unchecked")
	private void replaceTask( Experiment experiment, ExperimentExecutionArguments ea, Path pathTask ) throws ExperimentExecutionException {
		
		// As path could be a folder, we make sure we get a file
		Task<InputFromManager,Object> task = ExperimentReader.readTaskFromXML(pathTask);
		
		try {
			if (experiment instanceof IReplaceTask) {
				IReplaceTask<InputFromManager,Object> experimentCasted = (IReplaceTask<InputFromManager,Object>) experiment;
				experimentCasted.replaceTask(task);
			} else {
				throw new ExperimentExecutionException(
					String.format(
						"To override the task of an experiment, it must implement %s.%nThe current experiment does not: %s",
						IReplaceTask.class.getName(),
						experiment.getClass().getName()
					)
				);
			}
			
		} catch (OperationFailedException e) {
			throw new ExperimentExecutionException(
				String.format(
					"Cannot override the input of an experiment %s with task type %s",
					experiment.getClass().getName(),
					task.getClass().getName()
				),
				e
			);
		}
	}
	
	
	/**
	 * Executes an experiment
	 * 
	 * @param experimentsPath a path to a XML file describing an Experiment, or else to a path to a folder containing Experiment files
	 * @param ea additional arguments that describe the Experiment
	 * @throws ExperimentExecutionException if the experiment cannot be executed 
	 */
	private void executeExperiment( Experiment experiment, ExperimentExecutionArguments ea ) throws ExperimentExecutionException {
			
		try {
			experiment.doExperiment(ea);
		
		} catch (ExperimentExecutionException e) {
			throw new ExperimentExecutionException("Experiment execution ended with failure", e);
		}
	}
}
