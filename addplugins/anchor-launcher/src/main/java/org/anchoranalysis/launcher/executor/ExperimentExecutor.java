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

import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.anchoranalysis.launcher.parser.ParseArgsAndRunExperiment;

/**
 * Determines where the files passed the ExperimentExecutor are loaded from
 *  - where the input-manager or output-manager is found
 *  - different other execution arguments
 *  
 * @author Owen Feehan
 *
 */
public class ExperimentExecutor {
			
	private SelectParam<Path> experiment;
	
	private SelectParam<Path> input = SelectParamFactory.useDefault();

	private SelectParam<Path> output = SelectParamFactory.useDefault();
	
	private SelectParam<Path> task = SelectParamFactory.useDefault();
	
	private Path configDir;
	
	private Path executionDir;
	
	// If non-null, a string is printed in the description if the default-experiment is used. If non-null this is ignored.
	private String defaultBehaviourString;
	
	/**
	 * Constructor
	 * 
	 * @param experiment the experiment to run
	 * @param configDir the directory where configuration files are stored
	 * @param executionDir the directory from which the experiment is executed
	 */
	ExperimentExecutor( SelectParam<Path> experiment, Path configDir, Path executionDir) {
		super();
		this.experiment = experiment;
		this.configDir = configDir;
		this.executionDir = executionDir;
	}

	/**
	 * Executes an experiment after finding a single experiment XML file, and reading the experiment from this file 
	 * 
	 * @throws ExperimentExecutionException if the execution ends early
	 */
	public void executeExperiment( ExperimentExecutionArguments execArgs, boolean alwaysShowExperimentArgs, LogReporter logger ) throws ExperimentExecutionException {
		
		ExperimentExecutorObj delegate = new ExperimentExecutorObj(executionDir);
				
		if (defaultBehaviourString!=null) {
			// Special behaviour if everything has defaults
			if (areAllDefault()) {
				logger.logFormatted(
					"%s.%nLearn how to select inputs, outputs and tasks with 'anchor -%s'.%n",
					defaultBehaviourString,
					ParseArgsAndRunExperiment.OPTION_HELP
				);
			}
		}
		
		Experiment experiment = loadExperimentFromPath(execArgs); 
		
		if (alwaysShowExperimentArgs || experiment.useDetailedLogging()) {
			logger.log( describe() );
		}
		
		setupModelDirectory(configDir, execArgs);

		delegate.executeExperiment(
			experiment,
			execArgs,
			getInput().select( execArgs ),
			getOutput().select( execArgs ),
			getTask().select( execArgs )
		);
		
	}
	
	private void setupModelDirectory( Path pathExecutionDirectory, ExperimentExecutionArguments execArgs ) {
		// Set model directory, assuming that the directory is called from bin/
		execArgs.setModelDirectory(
			pathExecutionDirectory.getParent().resolve("models").normalize().toAbsolutePath()
		);
	}
	
	/** Constructs a summary string to describe how the experiment is being executed 
	 * @throws ExperimentExecutionException */
	private String describe() throws ExperimentExecutionException {
		return String.format(
			"%s%s%n",
			describeExperiment(),
			describeInputOutput()
		);
	}
	
	private String describeExperiment() throws ExperimentExecutionException{
		return String.format("Executing %s", experiment.describe() );
	}
	
	private boolean areAllDefault() {
		return experiment.isDefault() && input.isDefault() && output.isDefault() && task.isDefault();
	}
	
	private void addToListIfNonDefault(SelectParam<Path> selectParam, String textDscr, List<String> list) throws ExperimentExecutionException {
		if (!selectParam.isDefault()) {
			list.add( String.format("%s %s", textDscr, selectParam.describe()) );
		}
	}
	
	private String describeInputOutput() throws ExperimentExecutionException {
		
		// Components
		List<String> list = new ArrayList<>();
		addToListIfNonDefault( input, "input", list );
		addToListIfNonDefault( output, "output", list );
		addToListIfNonDefault( task, "task", list );
				
		return collapseIntoOneLine(list);
	}
	
	private static String collapseIntoOneLine( List<String> list ) {
		StringBuilder sb = new StringBuilder();
		for( int i=0; i<list.size(); i++) {
			String item = list.get(i);
			
			if (i==0) {
				sb.append(" with ");
			} else {
				sb.append(" and ");
			}
			sb.append( item );
		}
		return sb.toString();
	}
	
	private Experiment loadExperimentFromPath( ExperimentExecutionArguments execArgs ) throws ExperimentExecutionException {
		return ExperimentReader.readExperimentFromXML( experiment.select(execArgs), execArgs);
	}
	
	public void setInput(SelectParam<Path> input) {
		this.input = input;
	}

	public void setOutput(SelectParam<Path> output) {
		this.output = output;
	}

	public SelectParam<Path> getInput() {
		return input;
	}

	public SelectParam<Path> getOutput() {
		return output;
	}

	public void setDefaultBehaviourString(String defaultBehaviourString) {
		this.defaultBehaviourString = defaultBehaviourString;
	}

	public String getDefaultBehaviourString() {
		return defaultBehaviourString;
	}

	public SelectParam<Path> getTask() {
		return task;
	}

	public void setTask(SelectParam<Path> task) {
		this.task = task;
	}

	public Path getConfigDir() {
		return configDir;
	}
}
