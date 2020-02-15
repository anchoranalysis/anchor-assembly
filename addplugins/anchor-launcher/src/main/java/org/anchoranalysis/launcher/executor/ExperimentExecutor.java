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

import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamManagerFactory;
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
	
	private SelectParam<Path> input = SelectParamManagerFactory.useDefault();

	private SelectParam<Path> output = SelectParamManagerFactory.useDefault();
	
	// If non-null, a string is printed in the description if the default-experiment is used. If non-null this is ignored.
	private String defaultBehaviourString;
	
	ExperimentExecutor( SelectParam<Path> experiment) {
		super();
		this.experiment = experiment;
	}

	/**
	 * Executes an experiment after finding a single experiment XML file, and reading the experiment from this file 
	 * 
	 * @throws ExperimentExecutionException if the execution ends early
	 */
	public void executeExperiment( Path pathExecutionDirectory, ExperimentExecutionArguments execArgs, LogReporter logger ) throws ExperimentExecutionException {
		
		ExperimentExecutorObj delegate = new ExperimentExecutorObj(execArgs.isGUIEnabled(), pathExecutionDirectory);
		
		logger.log( describe() );
		
		logger.log("");
		
		delegate.executeExperiment(
			loadExperimentFromPath(execArgs),
			execArgs,
			getInput().select( execArgs ),
			getOutput().select( execArgs )
		);
		
	}
	
	/** Constructs a summary string to describe how the experiment is being executed 
	 * @throws ExperimentExecutionException */
	private String describe() throws ExperimentExecutionException {
		return String.format(
			"%s%s",
			describeExperiment(),
			describeInputOutput()
		);
	}
	
	private String describeExperiment() throws ExperimentExecutionException{
		
		if (defaultBehaviourString!=null) {
			// Special behaviour if everything has defaults
			if (areAllDefault()) {
				return String.format(
					"%s.%nLearn how to select inputs, outputs and tasks with 'anchor -%s'.",
					defaultBehaviourString,
					ParseArgsAndRunExperiment.OPTION_HELP
				);
			} else if (experiment.isDefault()) {
				return defaultBehaviourString;
			}
		}
		
		return String.format("Executing %s", experiment.describe() );
	}
	
	private boolean areAllDefault() {
		return experiment.isDefault() && input.isDefault() && output.isDefault();
	}
	
	private String describeInputOutput() throws ExperimentExecutionException {
		
		if (input.isDefault() && output.isDefault() ) {
			// We show nothing
			return "";
		}
		
		if (!input.isDefault() && !output.isDefault()) {
			// BOTH are overridden
			return String.format(" with input %s and output %s", input.describe(), output.describe() );
		}
		
		if (!input.isDefault() && output.isDefault()) {
			// input only
			return String.format(" with input %s", input.describe() );
		}
		
		if (input.isDefault() && !output.isDefault()) {
			// output only
			return String.format(" with output %s", output.describe() );
		}
		
		assert(false);
		return "should never happen";
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
}
