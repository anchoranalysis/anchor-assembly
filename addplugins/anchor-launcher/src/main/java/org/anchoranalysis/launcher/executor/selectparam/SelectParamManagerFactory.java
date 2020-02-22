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
	 * @param configDir path to the configuration-directory of anchor
	 * @return an appropriate SelectParam object
	 */
	public static SelectParam<Path> pathOrTaskNameOrDefault( CommandLine line, String optionName, Path configDir ) {
		return ifOption(line, optionName, arg -> pathOrTaskName(arg, configDir) );
	}
	
	/**
	 * Can point to either:
	 * <ol>
	 * <li>a path ending in .xml -> assumed to BeanXML for an input manager</li>
	 * <li>a directory -> set as an the inputDirectory in the input-context</li>
	 * <li>a string with a wild-card, assumed to be a glob, set into the input-context as a glob</li>
	 * <li>a string with a period, and without any forward or backwards slashes -> set into the input-context as an extension to match</li>
	 * </ol>
	 * 
	 * @param line command-line to consider if certain options have been selected or not
	 * @param optionName which option we consider
	 * @return an appropriate SelectParam object
	 */
	public static SelectParam<Path> inputSelectParam(
		CommandLine line,
		String optionName
	) {
		return ifOption(line, optionName, arg -> pathOrDirectoryOrGlobOrExtension(arg) );
	}
	
	
	/**
	 * Can point to either
	 * <ol>
	 * <li>a path ending in .xml -> assumed to BeanXML for an output manager</li> 
	 * <li>a directory -> set as the outputDirectory in the input-context</li>
	 * </ol>
	 * 
	 * @param line command-line to consider if certain options have been selected or not
	 * @param optionName which option we consider
	 * @return an appropriate SelectParam object
	 */
	public static SelectParam<Path> outputSelectParam(
		CommandLine line,
		String optionName
	) {
		return ifOption(line, optionName, arg -> pathOrDirectory(arg, false) );
	}
	
	private static SelectParam<Path> ifOption(CommandLine line, String optionName, Function<String,SelectParam<Path>> func ) {
		if (line.hasOption(optionName)) {
        	return func.apply( line.getOptionValue(optionName) );
        	
        } else {
        	return new UseDefaultManager();
        }
	}
	
	/** If the argument is a path to a directory, then this directory is set as the default. Otherwise the argument is treated like a path to BeanXML */
	private static SelectParam<Path> pathOrDirectory( String arg, boolean input ) {
    	
    	Path path = pathFromArg(arg);
		
		if (path.toFile().isDirectory()) {
    		return new UseDirectoryAsManager(path, input);
    	} else {
    		return new CustomManagerFromPath(path);	
    	}
	}
	
	
	private static SelectParam<Path> pathOrDirectoryOrGlobOrExtension( String arg ) {

		// If it contains a wildcard, assume its a glob
		if (arg.contains("*")) {
			return new UserAsGlob(arg);
		}
		
		// If it begins with a period, and no slashes, then assume it's a file extension
		if (isFileExtension(arg)) {
			return new UseAsExtension(arg);
		}
				
    	Path path = pathFromArg(arg);
		
		if (path.toFile().isDirectory()) {
    		return new UseDirectoryAsManager(path, true);
    	} else {
    		return new CustomManagerFromPath(path);	
    	}
	}
	
	/** If the argument a name (no extension, no root, no special-chars apart from forward-slashes), then construct an automatic path to the tasks
	 *  in the configuration directory. Otherwise treat as path to BeanXML */
	private static SelectParam<Path> pathOrTaskName( String arg, Path configDir ) {
    	
		if (isTaskName(arg)) {
			return new CustomManagerFromPath( constructPathForTaskName(arg, configDir) );
		} else {
			return new CustomManagerFromPath( pathFromArg(arg) );
		}
	}
	
	private static Path constructPathForTaskName( String arg, Path configDir ) {
		return configDir.resolve("tasks").resolve(arg + ".xml");
	}
	
	// Check if it contains only a restricted set of characters... alphaNumeric, hyphen, underscore, forward-slash
	private static boolean isTaskName( String arg ) {
		return arg.matches("^[a-zA-Z0-9_\\-\\/]+$");
	}
		
	private static Path pathFromArg( String arg ) {
		return Paths.get(arg).toAbsolutePath();
	}
	
	private static boolean isFileExtension( String arg ) {
		if (!arg.startsWith(".")) {
			return false;
		}
		
		if (arg.contains("/") || arg.contains("\\")) {
			return false;
		}
		
		if (arg.equals(".") || arg.equals("..")) {
			return false;
		}
		
		return true;
	}
}
