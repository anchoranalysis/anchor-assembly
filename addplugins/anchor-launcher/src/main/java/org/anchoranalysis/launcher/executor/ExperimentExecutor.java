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
