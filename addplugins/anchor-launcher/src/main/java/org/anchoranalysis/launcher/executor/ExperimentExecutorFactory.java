package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;

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

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.apache.commons.cli.CommandLine;

public class ExperimentExecutorFactory {

	/**
	 * Creates an experiment-executor from a command line that EITHER:
	 *       uses a default-experiment
	 *    OR accepts a path passed as the first command-line argument
	 *    
	 * @param line the command-line arguments
	 * @param defaultExperiment path to the default-experiment
	 * @param configDir path to the configuration directory
	 * @param executionDir path from which experiment is executed (i.e. the bin/ directory typically)
	 * @return
	 * @throws ExperimentExecutionException
	 */
	public static ExperimentExecutor create( CommandLine line, Path defaultExperiment, Path configDir, Path executionDir ) throws ExperimentExecutionException {
		return new ExperimentExecutor(
			SelectParamFactory.experimentSelectParam(line, defaultExperiment),
			configDir,
			executionDir
		);
	}
}
