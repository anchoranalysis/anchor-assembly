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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.anchoranalysis.launcher.CommandLineException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FilenameUtils;

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
		return ifOption(line, optionName, args -> pathOrTaskName(args, configDir) );
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
		return ifOption(line, optionName, args -> pathOrDirectoryOrGlobOrExtension(args) );
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
	
	private static SelectParam<Path> ifOption(CommandLine line, String optionName, Function<String[],SelectParam<Path>> func ) {
		if (line.hasOption(optionName)) {
        	return func.apply( line.getOptionValues(optionName) );
        	
        } else {
        	return new UseDefaultManager();
        }
	}
	
	/** If the argument is a path to a directory, then this directory is set as the default. Otherwise the argument is treated like a path to BeanXML 
	 * @throws CommandLineException  */
	private static SelectParam<Path> pathOrDirectory( String[] arg, boolean input ) {
    	
		if (arg.length>1) {
			throw new CommandLineException("More than one argument was passed to -o. Only one is allowed!");
		}
		
    	Path path = pathFromArg(arg[0]);
		
		if (path.toFile().isDirectory()) {
    		return new UseDirectoryAsManager(path, input);
    	} else {
    		return new CustomManagerFromPath(path);	
    	}
	}
	
	
	private static SelectParam<Path> pathOrDirectoryOrGlobOrExtension( String[] args ) {

		// If it contains a wildcard, assume its a glob
		if (anyHas(args, s->s.contains("*"))) {
			if (args.length==1) {
				return new UseAsGlob(args[0]);
			} else {
				throw new CommandLineException("Only a single wildcard argument is permitted to -i");
			}
		}
		
		// If it begins with a period, and no slashes, then assume it's a file extension
		if (anyHas(args, SelectParamManagerFactory::isFileExtension)) {
			if (allHave(args, SelectParamManagerFactory::isFileExtension)) {
				return new UseAsExtension(args);	
			} else {
				throw new CommandLineException("If a file-extension (e.g. .png) is specified, all other arguments to -i must also be file-extensions");
			}
			
		}
				
		if (anyHas(args, SelectParamManagerFactory::hasXmlExtension)) {
			if (args.length==1) {
				return new CustomManagerFromPath( pathFromArg(args[0]) );
			} else {
				throw new CommandLineException("Only a single BeanXML argument is permitted after -i (i.e. with a path with a .xml extension)");
			}
			
		}
				
		List<Path> paths = pathFromArgs(args);
		
		if (anyHas(paths, p->p.toFile().isDirectory())) {
			if (args.length==1) {
				return new UseDirectoryAsManager(paths.get(0), true);
			} else {
				throw new CommandLineException("Only a single argument is permitted after -i if it's a directory");
			}
			
		}
		
		return new UseListFilesAsManager(paths);
		//throw new CommandLineException("At least one argument passed to -i was not recognised");
	}
	
	private static boolean hasXmlExtension( String path ) {
		return FilenameUtils.getExtension(path).equalsIgnoreCase("xml");
	}
	
	private static <T> boolean anyHas( T[] args, Predicate<T> pred ) {
		for( T arg : args ) {
			if (pred.test(arg)) {
				return true;
			}
		}
		return false;
	}
	
	private static <T> boolean anyHas( List<T> args, Predicate<T> pred ) {
		for( T arg : args ) {
			if (pred.test(arg)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean allHave( String[] args, Predicate<String> pred ) {
		for( String arg : args ) {
			if (!pred.test(arg)) {
				return false;
			}
		}
		return true;
	}
	
	/** If the argument a name (no extension, no root, no special-chars apart from forward-slashes), then construct an automatic path to the tasks
	 *  in the configuration directory. Otherwise treat as path to BeanXML */
	private static SelectParam<Path> pathOrTaskName( String[] args, Path configDir ) {
    	
		if (args.length!=1) {
			throw new CommandLineException("One and only one argument is permitted after -t");
		}
		
		String taskArg = args[0];
		
		if (isTaskName(taskArg)) {
			return new CustomManagerFromPath( constructPathForTaskName(taskArg, configDir) );
		} else {
			return new CustomManagerFromPath( pathFromArg(taskArg) );
		}
	}
	
	private static Path constructPathForTaskName( String arg, Path configDir ) {
		return configDir.resolve("tasks").resolve(arg + ".xml");
	}
	
	// Check if it contains only a restricted set of characters... alphaNumeric, hyphen, underscore, forward-slash
	private static boolean isTaskName( String arg ) {
		return arg.matches("^[a-zA-Z0-9_\\-\\/]+$");
	}
	
	private static List<Path> pathFromArgs( String[] args ) {
		return Arrays.stream(args).map( SelectParamManagerFactory::pathFromArg ).collect( Collectors.toList() );
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
