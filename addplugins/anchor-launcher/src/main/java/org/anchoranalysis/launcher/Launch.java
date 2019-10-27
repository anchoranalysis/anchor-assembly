package org.anchoranalysis.launcher;

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.log.ConsoleLogReporter;
import org.anchoranalysis.launcher.parser.CommandLineParserExperiment;

/*
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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



/**
 * A command-line interface used for launching experiments
 * 
 * @author Owen Feehan
 *
 */
public class Launch {
		
	private Launch() {
		// Class should only be accessed through static methods
	}
	
	/**
	 * Entry point for command-line application
	 * 
	 * @param args command line application
	 * @throws Exception whatever exceptions occur
	 */
	public static void main(String[] args) throws Exception {
		LogErrorReporter logger = new LogErrorReporter( new ConsoleLogReporter() );
		runCommandLineApp( args, new CommandLineParserExperimentLauncher( logger ) );
	}
	

	/**
	 * Runs a command-line app, by parsing arguments
	 * 
	 * @param args args from command-line application
	 * @param parser a parser for this command-line application
	 */
	public static void runCommandLineApp( String[] args, CommandLineParserExperiment parser ) {
		DirtyInitializer.dirtyInitialization();
		parser.parse(args);
	}
	
}
