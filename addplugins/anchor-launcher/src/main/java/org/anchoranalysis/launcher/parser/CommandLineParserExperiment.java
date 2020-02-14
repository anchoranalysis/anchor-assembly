package org.anchoranalysis.launcher.parser;

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


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A general parser for command-line applications that have the following attributes
 *  1. a help option, that prints help information
 *  2. a version option, that prints version information
 *  3. a logError option, that records certain errors (parsing errors) in a log-file with more detail
 *  3. take an argument of a single path that represents an experiment BeanXML file (or path to a folder containing such)
 * 
 * @author Owen Feehan
 *
 */
public class CommandLineParserExperiment {
	
	// START: Options
	public static final String OPTION_HELP = "h";
	private static final String OPTION_VERSION = "v";
	private static final String OPTION_LOG_ERROR = "l";
	// END: Options
	
	/**
	 * For reporting messages on what goes wrong
	 */
	private LogErrorReporter logger;

	/**
	 * 
	 * @param logger a logger where error messages outputted to
	 * @param newlinesBeforeError if TRUE, then some extra newlines are inserted before error messages
	 */
	public CommandLineParserExperiment( LogErrorReporter logger ) {
		this.logger = logger;
	}
	
	/**
	 * Parses the arguments to a command-line experiment and does some action, usually processExperiment()
	 *   but sometimes other actions (depending on what options are selected)
	 *   
	 * @param args arguments from command-line
	 */
	public void parse( String args[], CommandLineParserConfig parserConfig ) {
		
		Options options = createOptions(parserConfig);
		
		// create the parser
	    CommandLineParser parser = new DefaultParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        
	        if (maybePrintHelp(line, options, args, parserConfig)) {
	        	return;
	        }
	        
	        if (maybePrintVersion(line, parserConfig)) {
	        	return;
	        }
	      	        
	        // We process the remaining arguments
	        if (parserConfig.requiresFirstArgument() && line.getArgs().length==0) {
	        	ErrorPrinter.printMissingExperimentArgument();
	        	return;
	        }
	        
	        if (line.getArgs().length>1) {
	        	ErrorPrinter.printTooManyArguments();
	        	return;
	        }
	        
	        processExperimentShowErrors(line, parserConfig );
			
	    } catch( ParseException e ) {
	        // oSomething went wrong
	    	logger.getLogReporter().logFormatted( "Parsing of command-line arguments failed.  Reason: %s%n", e.getMessage() );
	    } catch (IOException e) {
	    	logger.getErrorReporter().recordError(CommandLineParserExperiment.class, e);
	    	logger.getLogReporter().logFormatted( "An I/O error occurred.  Reason: %s%n", e.getMessage() );
		}
	}
	
	/**
	 * Maybe prints a help message to the screen
	 * 
	 * @return true if it prints the message, false otherwise
	 * @throws IOException 
	 */
	private boolean maybePrintHelp( CommandLine line, Options options, String args[], CommandLineParserConfig parserConfig ) throws IOException {
		boolean isArgumentRequiredAndMissing = parserConfig.requiresFirstArgument() && args.length==0;
		if (line.hasOption(OPTION_HELP) || isArgumentRequiredAndMissing) {
        	printHelp( options, parserConfig );
		    return true;
        }
		return false;
	}
	
	private boolean maybePrintVersion( CommandLine line, CommandLineParserConfig parserConfig ) throws IOException {
		if (line.hasOption(OPTION_VERSION)) {
        	VersionPrinter.printVersion(
        		parserConfig.classLoaderResources(),
        		parserConfig.resourceVersionFooter(),
        		parserConfig.resourceMavenProperties()
        	);
        	return true;
        } 
        return false;
	}
		
	/**
	 * Calls processExperiment() but displays any error messages in a user-friendly way on System.err
	 * 
	 * @param line
	 */
	private void processExperimentShowErrors( CommandLine line, CommandLineParserConfig parserConfig ) {
		
		try {
			processExperiment(line, logger, parserConfig );
			
		} catch (ExperimentExecutionException e) {
	    	
			if (parserConfig.newlinesBeforeError()) {
				logger.getLogReporter().logFormatted("%n");
			}
			
	    	// Let's print a simple (non-word wrapped message) to the console
			logger.getLogReporter().log( e.friendlyMessageHierarchy() );
	    	
	    	// Unless it's enabled, we record a more detailed error log to the file-system
	    	if (line.hasOption(OPTION_LOG_ERROR)) {
	    		Path errorLogPath = Paths.get(line.getOptionValue(OPTION_LOG_ERROR) );
	    		logger.getLogReporter().logFormatted("Logging error in \"%s\"%n", errorLogPath.toAbsolutePath() );
	    		ErrorPrinter.printErrorLog(e, errorLogPath);
	    	}
	    }
	}
	
	
	/**
	 * Some operation is executed on an an experiment after considering the help/version options
	 * 
	 * @param line remaining-command line arguments after options are removed
	 * @param logger TODO
	 * @throws ExperimentExecutionException if processing ends early
	 */
	protected void processExperiment( CommandLine line, LogErrorReporter logger, CommandLineParserConfig parserConfig ) throws ExperimentExecutionException {
		
        ExperimentExecutionArguments ea = parserConfig.createArguments(line);
        
        ExperimentExecutor executor = new ExperimentExecutor(
        	ea.isGUIEnabled(),
        	parserConfig.configDir()
        );
		
		executor.executeExperiment(
        	parserConfig.createExperimentTemplate(line),
        	ea,
        	logger.getLogReporter()
        );
	}
	
	
	/**
	 * Prints help message to guide usage to std-output
	 * 
	 * @param options possible user-options
	 * @throws IOException if the help display messages cannot be read
	 */
	protected void printHelp( Options options, CommandLineParserConfig parserConfig ) throws IOException {
		
    	// automatically generate the help statement
	    HelpFormatter formatter = new HelpFormatter();
	    
	    // Avoid a leading / on the resource path as it uses a ClassLoader to load resources, which is different behaviour to getClass().getResourceAsStream()
	    String headerDisplayMessage = ResourceReader.readStringFromResource(parserConfig.resourceUsageHeader(), parserConfig.classLoaderResources() );
	    String footerDisplayMessage = ResourceReader.readStringFromResource(parserConfig.resourceUsageFooter(), parserConfig.classLoaderResources() );

	    String firstLine = String.format( "%s [options] [%s]", parserConfig.commandNameInHelp(), parserConfig.firstArgumentInHelp() );
	    formatter.printHelp(
	    	firstLine,
	    	headerDisplayMessage,
	    	options,
	    	footerDisplayMessage
	    );
	}
	
	/**
	 * Create options for the command-line client, returning default options always available for this class
	 * @return the options that can be used
	 */
	private Options createOptions( CommandLineParserConfig parserConfig ) {

		Options options = new Options();

		options.addOption(OPTION_HELP, false, "print this message and exit");
		
		options.addOption(OPTION_VERSION, false, "print version information and exit");
		
		// This logs the errors in greater detail
		options.addOption(OPTION_LOG_ERROR, true, "log BeanXML parsing errors to file");
		
		parserConfig.addAdditionalOptions(options);
		
		return options;
	}
}
