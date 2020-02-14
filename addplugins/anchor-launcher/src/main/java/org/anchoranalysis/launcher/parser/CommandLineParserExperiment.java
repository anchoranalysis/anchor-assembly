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

import org.anchoranalysis.core.file.PathUtilities;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplate;
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
public abstract class CommandLineParserExperiment {
	
	// START: Options
	public static final String OPTION_HELP = "h";
	private static final String OPTION_VERSION = "v";
	private static final String OPTION_LOG_ERROR = "l";
	// END: Options
	
	/**
	 * if TRUE, then some extra newlines are inserted before error messages
	 *  
	 *  This useful for the GUI client in Windows due to WinRun4j running as a Windows app, and not as
	 *    a shell app. This changes how output is displayed;
	 */
	private boolean newlinesBeforeError;
	
	/**
	 * For reporting messages on what goes wrong
	 */
	private LogErrorReporter logger;

	/**
	 * 
	 * @param logger a logger where error messages outputted to
	 * @param newlinesBeforeError if TRUE, then some extra newlines are inserted before error messages
	 */
	protected CommandLineParserExperiment( LogErrorReporter logger, boolean newlinesBeforeError ) {
		this.newlinesBeforeError = newlinesBeforeError;
		this.logger = logger;
	}
	
	/**
	 * Parses the arguments to a command-line experiment and does some action, usually processExperiment()
	 *   but sometimes other actions (depending on what options are selected)
	 *   
	 * @param args arguments from command-line
	 */
	public void parse( String args[] ) {
		
		Options options = createOptions();
		
		// create the parser
	    CommandLineParser parser = new DefaultParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        
	        if (maybePrintHelp(line, options, args )) {
	        	return;
	        }
	        
	        if (maybePrintVersion(line)) {
	        	return;
	        }
	      	        
	        // We process the remaining arguments
	        if (requiresFirstArgument() && line.getArgs().length==0) {
	        	ErrorPrinter.printMissingExperimentArgument();
	        	return;
	        }
	        
	        if (line.getArgs().length>1) {
	        	ErrorPrinter.printTooManyArguments();
	        	return;
	        }
	        
	        processExperimentShowErrors(line);
			
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
	private boolean maybePrintHelp( CommandLine line, Options options, String args[] ) throws IOException {
		boolean isArgumentRequiredAndMissing = requiresFirstArgument() && args.length==0;
		if (line.hasOption(OPTION_HELP) || isArgumentRequiredAndMissing) {
        	printHelp( options );
		    return true;
        }
		return false;
	}
	
	private boolean maybePrintVersion( CommandLine line ) throws IOException {
		if (line.hasOption(OPTION_VERSION)) {
        	VersionPrinter.printVersion(
        		classLoaderResources(),
        		resourceVersionFooter(),
        		resourceMavenProperties()
        	);
        	return true;
        } 
        return false;
	}
	
	/**
	 * Create options for the command-line client, returning default options always available for this class
	 * @return the options that can be used
	 */
	protected Options createOptions() {

		Options options = new Options();

		options.addOption(OPTION_HELP, false, "print this message and exit");
		
		options.addOption(OPTION_VERSION, false, "print version information and exit");
		
		// This logs the errors in greater detail
		options.addOption(OPTION_LOG_ERROR, true, "log BeanXML parsing errors to file");
		
		return options;
	}
	
	
	/**
	 * Calls processExperiment() but displays any error messages in a user-friendly way on System.err
	 * 
	 * @param line
	 */
	private void processExperimentShowErrors( CommandLine line ) {
		
		try {
			processExperiment(line, logger);
			
		} catch (ExperimentExecutionException e) {
	    	
			if (newlinesBeforeError) {
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
	protected void processExperiment( CommandLine line, LogErrorReporter logger ) throws ExperimentExecutionException {
		
        ExperimentExecutionArguments ea = createArguments(line);
        
        ExperimentExecutor executor = new ExperimentExecutor(
        	ea.isGUIEnabled(),
        	configDir()
        );
		
		executor.executeExperiment(
        	createExperimentTemplate(line),
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
	protected void printHelp( Options options ) throws IOException {
		
    	// automatically generate the help statement
	    HelpFormatter formatter = new HelpFormatter();
	    
	    // Avoid a leading / on the resource path as it uses a ClassLoader to load resources, which is different behaviour to getClass().getResourceAsStream()
	    String headerDisplayMessage = ResourceReader.readStringFromResource(resourceUsageHeader(), classLoaderResources() );
	    String footerDisplayMessage = ResourceReader.readStringFromResource(resourceUsageFooter(), classLoaderResources() );

	    String firstLine = String.format( "%s [options] [%s]", commandNameInHelp(), firstArgumentInHelp() );
	    formatter.printHelp(
	    	firstLine,
	    	headerDisplayMessage,
	    	options,
	    	footerDisplayMessage
	    );
	}
	
	/**
	 * What class-loader to use for loading resources
	 * @return a class-loader
	 */
	protected abstract ClassLoader classLoaderResources();
	
	/**
	 * What the application command is described as in the help message e.g.&nbsp;anchor or anchorGUI
	 * @return a word describing the application command (for the help message)
	 */
	protected abstract String commandNameInHelp();
	
	/**
	 * What the application argument is described as in the help message e.g.&nbsp;experimentFile.xml
	 * @return a word describing the application arguments (for the help message)
	 */
	protected abstract String firstArgumentInHelp();
	
	/**
	 * What resource to use for the version-footer
	 * @return resource-path
	 */
	protected abstract String resourceVersionFooter();

	/**
	 * What resource to use for the usage-footer
	 * @return resource-path
	 */
	protected abstract String resourceUsageHeader();
	
	/**
	 * What resource to use for the usage-footer
	 * @return resource-path
	 */
	protected abstract String resourceUsageFooter();
	
	
	/**
	 * What resource to use for the maven properties
	 * @return resource-path
	 */
	protected abstract String resourceMavenProperties();
	
	/**
	 * If true this command-line application always requires a first-argument (unless -help or -version is selected)
	 * @return true if a first-argument is always required, false otherwise
	 */
	protected abstract boolean requiresFirstArgument();
	
	/**
	 * Directory where the configuration files are stored
	 * 
	 * @return a path to the directory
	 */
	protected abstract Path configDir();
	
	protected abstract ExperimentExecutionArguments createArguments( CommandLine line );
	
	protected abstract ExperimentExecutionTemplate createExperimentTemplate( CommandLine line ) throws ExperimentExecutionException;
}
