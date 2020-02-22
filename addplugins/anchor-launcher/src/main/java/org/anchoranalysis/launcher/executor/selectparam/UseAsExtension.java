package org.anchoranalysis.launcher.executor.selectparam;

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
import java.util.Arrays;
import java.util.HashSet;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;


/**
 * Uses the path directory as a manager
 * 
 * @author Owen Feehan
 *
 */
class UseAsExtension extends SelectParam<Path> {

	private String extension;
	
	/**
	 * Constructor
	 *  
	 * @param wildcardStr string containing a wildcard
	 */
	public UseAsExtension(String extension) {
		super();
		this.extension = extension;
	}

	@Override
	public Path select( ExperimentExecutionArguments eea ) {
		
		// Remove the period from the left side
		String extWithoutPeriod = extension.substring(1);
		eea.setInputFilterExtensions(
			new HashSet<>( Arrays.asList(extWithoutPeriod) )
		);
		
		return null;
	}

	@Override
	public String describe() throws ExperimentExecutionException {
		return extension;
	}

	@Override
	public boolean isDefault() {
		return false;
	}
}
