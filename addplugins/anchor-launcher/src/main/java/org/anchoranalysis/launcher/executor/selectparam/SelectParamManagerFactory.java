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
import java.nio.file.Paths;
import java.util.function.Function;

import org.apache.commons.cli.CommandLine;

/**
 * Creates an appropriate SelectParam based upon the options passed to the command-line
 * 
 * @author Owen Feehan
 *
 */
public class SelectParamManagerFactory {
	
	/**
	 * Default option
	 * 
	 * @return
	 */
	public static SelectParam<Path> useDefault() {
		return new UseDefaultManager();
	}
	
	/**
	 * Can point to either a path to beanXML
	 * 
	 * @param line command-line to consider if certain options have been selected or not
	 * @param optionName which option we consider
	 * @return an appropriate SelectParam object
	 */
	public static SelectParam<Path> pathOrDefault( CommandLine line, String optionName ) {
		return ifOption(line, optionName, path -> new CustomManagerFromPath(path) );
	}
	
	/**
	 * Can point to either a path to beanXML or to a directory (in which case a manager is derived)
	 * 
	 * @param line command-line to consider if certain options have been selected or not
	 * @param optionName which option we consider
	 * @param iff TRUE, this is an input-manager, otherwise it's an output manager
	 * @return an appropriate SelectParam object
	 */
	public static SelectParam<Path> pathOrDirectoryOrDefault(
		CommandLine line,
		String optionName,
		boolean input
	) {
		return ifOption(line, optionName, path -> pathOrDirectory(path, input) );
	}
	
	private static SelectParam<Path> ifOption(CommandLine line, String optionName, Function<Path,SelectParam<Path>> func ) {
		if (line.hasOption(optionName)) {
        	
        	Path optionValuePath = Paths.get(
        		line.getOptionValue(optionName)
        	);
        	return func.apply(optionValuePath);
        	
        } else {
        	return new UseDefaultManager();
        }
	}
	
	/** If a path is a directory, execIfDir is called, and NULL is returned.  If a path is a non-directory, is is returned directly */
	private static SelectParam<Path> pathOrDirectory( Path path, boolean input ) {
				
		// If it's a relative path, we convert to absolute
		if (!path.isAbsolute()) {
			path = path.toAbsolutePath();
		}
		
		if (path.toFile().isDirectory()) {
    		return new UseDirectoryAsManager(path, input);
    	} else {
    		return new CustomManagerFromPath(path);	
    	}
	}
}
