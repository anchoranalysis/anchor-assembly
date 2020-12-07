/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.anchoranalysis.launcher.run;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.resources.Resources;
import org.anchoranalysis.launcher.run.tasks.PredefinedTasks;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Parses command-line arguments and runs an experiment.
 *
 * <p>The parser includes:
 *
 * <ol>
 *   <li>a help option, that prints help information
 *   <li>a version option, that prints version information
 *   <li>a logError option, that records certain errors (parsing errors) in a log-file with more
 *       detail
 *   <li>and take an argument of a single path that represents an experiment BeanXML file (or path
 *       to a directory containing experiment BeanXML)
 * </ol>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class ParseArgumentsAndRunExperiment {

    // START REQUIRED ARGUMENTS
    /** For reporting messages on what goes wrong */
    private final Logger logger;
    // END REQUIRED ARGUMENTS

    /**
     * Parses the arguments to a command-line experiment and runs an experiment.
     *
     * @param arguments arguments from command-line
     * @param config a configuration for the command-line executor.
     */
    public void parseAndRun(String[] arguments, LauncherConfig config) {

        Options options = createOptions(config);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, arguments);

            // Resources
            Resources resources = config.resources();
            
            if (maybePrintHelp(line, options, resources, config.help())) {
                return;
            }

            if (maybePrintVersion(line, resources, System.out)) {  // NOSONAR
                return;
            }

            if (line.getArgs().length > 1) {
                ErrorPrinter.printTooManyArguments();
                return;
            }
            
            processExperimentShowErrors(line, config, resources);

        } catch (ParseException e) {
            // Something went wrong
            logger.messageLogger()
                    .logFormatted(
                            "Parsing of command-line arguments failed.  Reason: %s%n",
                            e.getMessage());
        } catch (IOException e) {
            logger.errorReporter().recordError(ParseArgumentsAndRunExperiment.class, e);
            logger.messageLogger()
                    .logFormatted("An I/O error occurred.  Reason: %s%n", e.getMessage());
        } catch (AnchorFriendlyRuntimeException e) {
            logger.messageLogger().logFormatted(e.friendlyMessageHierarchy());
        }
    }

    /**
     * Prints a help message to the screen  if the command-line option is selected. 
     *
     * @return true if it prints the message, false otherwise
     */
    private boolean maybePrintHelp(
            CommandLine line,
            Options options,
            Resources resources,
            HelpConfig helpConfig)
    {
        if (line.hasOption(CommandLineOptions.SHORT_OPTION_HELP)) {
            printHelp(
                    options,
                    resources,
                    helpConfig.getCommandName(),
                    helpConfig.getFirstArgument());
            return true;
        }
        return false;
    }

    private boolean maybePrintVersion(CommandLine line, Resources resources, PrintStream printTo)
            throws IOException {
        if (line.hasOption(CommandLineOptions.SHORT_OPTION_VERSION)) {
            printVersion(resources, printTo);
            return true;
        }
        return false;
    }
    
    /**
     * Calls processExperiment() but displays any error messages in a user-friendly way on
     * System.err
     *
     * @param line
     */
    private void processExperimentShowErrors(CommandLine line, LauncherConfig config, Resources resources) {

        try {
            processExperiment(line, logger, config, resources, System.out);    // NOSONAR

        } catch (ExperimentExecutionException e) {

            if (config.newlinesBeforeError()) {
                logger.messageLogger().logFormatted("%n");
            }

            // Let's print a simple (non-word wrapped message) to the console
            logger.messageLogger().log(e.friendlyMessageHierarchy());

            // Unless it's enabled, we record a more detailed error log to the filesystem
            if (line.hasOption(CommandLineOptions.SHORT_OPTION_LOG_ERROR)) {
                Path errorLogPath =
                        Paths.get(line.getOptionValue(CommandLineOptions.SHORT_OPTION_LOG_ERROR));
                logger.messageLogger()
                        .logFormatted("Logging error in \"%s\"%n", errorLogPath.toAbsolutePath());
                ErrorPrinter.printErrorLog(e, errorLogPath);
            }
        }
    }

    /**
     * Some operation is executed on an an experiment after considering the help/version options
     *
     * @param line remaining-command line arguments after options are removed
     * @param logger logger
     * @throws ExperimentExecutionException if processing ends early
     */
    private void processExperiment(CommandLine line, Logger logger, LauncherConfig config, Resources resources, PrintStream printTo)
            throws ExperimentExecutionException {

        ExperimentExecutor executor = config.createExperimentExecutor(line); 
        
        if (maybeShowTasks(line, executor.taskDirectory(), resources, printTo)) {
            // Exit early if we've shown the available tasks.
            return;
        }
        
        executor.executeExperiment(
                        config.createArguments(line),
                        line.hasOption(CommandLineOptions.SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS),
                        logger.messageLogger());
    }

    /**
     * Prints help message to guide usage to standard-output.
     *
     * @param options possible user-options
     */
    private static void printHelp(
            Options options,
            Resources resources,
            String commandNameInHelp,
            String firstArgumentInHelp) {

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();

        String firstLine =
                String.format("%s [options] [%s]", commandNameInHelp, firstArgumentInHelp);
        formatter.printHelp(firstLine, resources.usageHeader(), options, resources.usageFooter());
    }

    /**
     * Create options for the command-line client, returning default options always available for
     * this class
     *
     * @return the options that can be used
     */
    private Options createOptions(LauncherConfig config) {

        Options options = new Options();
        CommandLineOptions.addBasicOptions(options);
        config.addAdditionalOptions(options);

        return options;
    }
    
    
    /**
     * Prints the available pre-defined tasks if the command-line option is selected.
     * 
     * The predefined tasks come from the .xml files found in the config/tasks/ directory.
     * 
     * @param resources resources
     * @param printTo where to print messages to
     * @return true if it prints the message, false otherwise
     */
    private boolean maybeShowTasks(CommandLine line, Path tasksDirectory, Resources resources, PrintStream printTo) {
        if (line.hasOption(CommandLineOptions.SHORT_OPTION_SHOW_TASKS)) {
            PredefinedTasks.printTasksToConsole(tasksDirectory, resources, printTo);
            return true;
        }
        return false;
    }
    


    /**
     * Describes which version of anchor is being used, and what version number
     *
     * @param resources resources
     * @param printTo where to print messages to
     * @throws IOException if it's not possible to determine the version number
     */
    private static void printVersion(
            Resources resources, PrintStream printTo)
            throws IOException {
        printTo.printf(
                "anchor version %s by Owen Feehan (ETH Zurich, University of Zurich, 2016)%n",
                resources.versionFromMavenProperties());
        printTo.println();
        printTo.print(resources.versionFooter());
    }
}
