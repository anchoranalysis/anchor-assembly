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
import java.util.function.Function;

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.experiment.ExperimentFactory;
import org.anchoranalysis.launcher.executor.selectparam.io.InputFactory;
import org.anchoranalysis.launcher.executor.selectparam.io.OutputFactory;
import org.anchoranalysis.launcher.executor.selectparam.task.TaskFactory;
import org.apache.commons.cli.CommandLine;

/**
 * Creates an appropriate SelectParam based upon the options passed to the command-line
 * 
 * @author Owen Feehan
 *
 */
public class SelectParamFactory {
	
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
		return ifOption(line, optionName, args -> TaskFactory.pathOrTaskName(args, configDir) );
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
		return ifOption(line, optionName, args -> InputFactory.pathOrDirectoryOrGlobOrExtension(args) );
	}
	
	
	/**
	 * Can point to either:
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
		return ifOption(line, optionName, arg -> OutputFactory.pathOrDirectory(arg, false) );
	}
	
	
	/**
	 * Can point to either:
	 * <ol>
	 * <li>a path ending in .xml -> assumed to BeanXML for an experiment</li> 
	 * <li>nothing -> then default experiment is used</li>
	 * </ol>
	 * 
	 * @param line command-line to consider if certain options have been selected or not
	 * @param defaultExperiment path to the default experiment
	 * @return an appropriate SelectParam object
	 * @throws ExperimentExecutionException
	 */
	public static SelectParam<Path> experimentSelectParam( CommandLine line, Path defaultExperiment	) throws ExperimentExecutionException {
		return ExperimentFactory.defaultExperimentOrCustom(line, defaultExperiment);
	}
	
	private static SelectParam<Path> ifOption(CommandLine line, String optionName, Function<String[],SelectParam<Path>> func ) {
		if (line.hasOption(optionName)) {
        	return func.apply( line.getOptionValues(optionName) );
        	
        } else {
        	return new UseDefaultManager();
        }
	}
}
