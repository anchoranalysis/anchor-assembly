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

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.FilePathResolver;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.apache.commons.cli.CommandLine;

public class ExperimentExecutorFactory {

	/**
	 * Creates an experiment-executor from a command line that EITHER:
	 *       uses a default-experiment
	 *    OR accepts a path passed as the first command-line argument
	 *    
	 * @param line the command-line arguments
	 * @param pathRelativeProperties a property that defines a relative-path to the default experiment in bean XML
	 * @param executingClass a class which we use to determine the base location for pathRelativeProperties
	 * @return
	 * @throws ExperimentExecutionException
	 */
	public static ExperimentExecutor create( CommandLine line, String pathRelativeProperties, Class<?> executingClass ) throws ExperimentExecutionException {
		return new ExperimentExecutor(
			SelectParamFactory.experimentSelectParam(
				line,
				pathRelativeProperties,
				new FilePathResolver(executingClass)
			)
		);
	}
}
