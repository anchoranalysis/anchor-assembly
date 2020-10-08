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

package org.anchoranalysis.launcher.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.config.ResourcesConfig;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * A general parser for command-line applications with certain attributes.
 *
 * <p>Namely:
 *
 * <ol>
 *   <li>a help option, that prints help information
 *   <li>a version option, that prints version information
 *   <li>a logError option, that records certain errors (parsing errors) in a log-file with more
 *       detail
 *   <li>and take an argument of a single path that represents an experiment BeanXML file (or path
 *       to a folder containing experiment BeanXML)
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
     * Parses the arguments to a command-line experiment and runs an experiment
     *
     * @param args arguments from command-line
     * @param parserConfig a configuration for the command-line exector
     */
    public void parseAndRun(String[] args, LauncherConfig parserConfig) {

        Options options = createOptions(parserConfig);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (maybePrintHelp(line, options, parserConfig.resources(), parserConfig.help())) {
                return;
            }

            if (maybePrintVersion(line, parserConfig.resources())) {
                return;
            }

            if (line.getArgs().length > 1) {
                ErrorPrinter.printTooManyArguments();
                return;
            }

            processExperimentShowErrors(line, parserConfig);

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
     * Maybe prints a help message to the screen
     *
     * @return true if it prints the message, false otherwise
     * @throws IOException
     */
    private boolean maybePrintHelp(
            CommandLine line,
            Options options,
            ResourcesConfig resourcesConfig,
            HelpConfig helpConfig)
            throws IOException {
        if (line.hasOption(CommandLineOptions.SHORT_OPTION_HELP)) {
            printHelp(
                    options,
                    resourcesConfig,
                    helpConfig.getCommandName(),
                    helpConfig.getFirstArgument());
            return true;
        }
        return false;
    }

    private boolean maybePrintVersion(CommandLine line, ResourcesConfig resources)
            throws IOException {
        if (line.hasOption(CommandLineOptions.SHORT_OPTION_VERSION)) {
            VersionPrinter.printVersion(
                    resources.getClassLoader(),
                    resources.getVersionFooter(),
                    resources.getMavenProperties());
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
    private void processExperimentShowErrors(CommandLine line, LauncherConfig parserConfig) {

        try {
            processExperiment(line, logger, parserConfig);

        } catch (ExperimentExecutionException e) {

            if (parserConfig.newlinesBeforeError()) {
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
    private void processExperiment(CommandLine line, Logger logger, LauncherConfig parserConfig)
            throws ExperimentExecutionException {
        parserConfig
                .createExperimentExecutor(line)
                .executeExperiment(
                        parserConfig.createArguments(line),
                        line.hasOption(CommandLineOptions.SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS),
                        logger.messageLogger());
    }

    /**
     * Prints help message to guide usage to std-output
     *
     * @param options possible user-options
     * @throws IOException if the help display messages cannot be read
     */
    private static void printHelp(
            Options options,
            ResourcesConfig resources,
            String commandNameInHelp,
            String firstArgumentInHelp)
            throws IOException {

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();

        // Avoid a leading / on the resource path as it uses a ClassLoader to load resources, which
        // is different behaviour to getClass().getResourceAsStream()
        String headerDisplayMessage =
                ResourceReader.readStringFromResource(
                        resources.getUsageHeader(), resources.getClassLoader());
        String footerDisplayMessage =
                ResourceReader.readStringFromResource(
                        resources.getUsageFooter(), resources.getClassLoader());

        String firstLine =
                String.format("%s [options] [%s]", commandNameInHelp, firstArgumentInHelp);
        formatter.printHelp(firstLine, headerDisplayMessage, options, footerDisplayMessage);
    }

    /**
     * Create options for the command-line client, returning default options always available for
     * this class
     *
     * @return the options that can be used
     */
    private Options createOptions(LauncherConfig parserConfig) {

        Options options = new Options();
        CommandLineOptions.addBasicOptions(options);
        parserConfig.addAdditionalOptions(options);

        return options;
    }
}
