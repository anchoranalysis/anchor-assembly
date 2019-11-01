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

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamManagerFactory;
import org.anchoranalysis.launcher.parser.CommandLineParserExperiment;

/**
 * Determines where the files passed the ExperimentExecutor are loaded from
 *  - where the input-manager or output-manager is found
 *  - different other execution arguments
 *  
 * @author Owen Feehan
 *
 */
public class ExperimentExecutionTemplate {

	private static String DEFAULT_BEHAVIOUR_STRING = "Searching for inputs as per default experiment";
			
	private SelectParam<Path> experiment;
	
	private SelectParam<Path> input = SelectParamManagerFactory.useDefault();

	private SelectParam<Path> output = SelectParamManagerFactory.useDefault();
	
	ExperimentExecutionTemplate( SelectParam<Path> experiment) {
		super();
		this.experiment = experiment;
	}
	
	/** Constructs a summary string to describe how the experiment is being executed 
	 * @throws ExperimentExecutionException */
	public String describe() throws ExperimentExecutionException {
		return String.format(
			"%s%s",
			describeExperiment(),
			describeInputOutput()
		);
	}
	
	private String describeExperiment() throws ExperimentExecutionException{
		
		// Special behaviour if everything has defaults
		if (areAllDefault()) {
			return String.format(
				"%s.%nLearn how to select inputs, outputs and tasks with 'anchor -%s'.",
				DEFAULT_BEHAVIOUR_STRING,
				CommandLineParserExperiment.OPTION_HELP
			);
		} else if (experiment.isDefault()) {
			return DEFAULT_BEHAVIOUR_STRING;
		} else {
			return String.format("Executing %s", experiment.describe() );
		}
		
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

	public Experiment loadExperimentFromPath( ExperimentExecutionArguments execArgs ) throws ExperimentExecutionException {
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
}
