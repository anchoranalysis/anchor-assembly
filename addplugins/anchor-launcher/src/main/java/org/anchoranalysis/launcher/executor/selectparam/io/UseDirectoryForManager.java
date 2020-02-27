package org.anchoranalysis.launcher.executor.selectparam.io;

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
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.PrettyPathConverter;


/**
 * Uses the path directory as a manager
 * 
 * @author Owen Feehan
 *
 */
class UseDirectoryForManager extends SelectParam<Path> {

	private boolean input = true;
	private Path directory;
	
	/**
	 * Constructor
	 *  
	 * @param input iff TRUE, then we are replacing the input-manager, otherwise the output-manager
	 */
	public UseDirectoryForManager(Path directory, boolean input) {
		super();
		this.input = input;
		this.directory = directory;
		assert( directory.toFile().isDirectory() );
	}



	@Override
	public Path select( ExperimentExecutionArguments eea ) {
		
		if (input) {
			eea.setInputDirectory(directory);
		} else {
			eea.setOutputDirectory(directory);
		}
		
		return null;
	}

	@Override
	public String describe() throws ExperimentExecutionException {
		return PrettyPathConverter.prettyPath(directory);
	}

	@Override
	public boolean isDefault() {
		return false;
	}
}
