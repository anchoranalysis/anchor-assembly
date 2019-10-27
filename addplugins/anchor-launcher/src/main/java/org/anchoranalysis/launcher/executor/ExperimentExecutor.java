package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;

import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;


/**
 * Executes an experiment in different ways - creating an experiment by loading from the file-system, or otherwise using defaults.
 * 
 * We do not print any error messages to the console, but throw any errors in the form of ExperimentExecutionException which
 * can be translated elsewhere into nice error messages
 * 
 * @author Owen Feehan
 *
 */
public class ExperimentExecutor {

	private ExperimentExecutorObj delegate;
	
	public ExperimentExecutor( boolean gui, Path pathExecutionDirectory ) throws ExperimentExecutionException {
		delegate = new ExperimentExecutorObj(gui, pathExecutionDirectory);
	}

	/**
	 * Executes an experiment after finding a single experiment XML file, and reading the experiment from this file 
	 * 
	 * @param template determines how to execution the experiment (where inputs, outputs etc. come from)
	 * @throws ExperimentExecutionException if the execution ends early
	 */
	public void executeExperiment( ExperimentExecutionTemplate template, ExperimentExecutionArguments execArgs, LogReporter logger ) throws ExperimentExecutionException {
		
		logger.log( template.describe() );
		
		logger.log("");
		
		delegate.executeExperiment(
			template.loadExperimentFromPath(execArgs),
			execArgs,
			template.getInput().select( execArgs ),
			template.getOutput().select( execArgs )
		);
		
	}
}
