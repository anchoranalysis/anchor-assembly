package org.anchoranalysis.launcher.run;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import org.anchoranalysis.core.functional.checked.CheckedRunnable;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.resources.Resources;
import org.anchoranalysis.launcher.run.tasks.PredefinedTasks;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import lombok.AllArgsConstructor;

/**
 * Prints messages if certain command-line options are selected.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
class MessagePrinter {
    
    /** Where to print messages to. */
    private static final PrintStream PRINT_TO = System.out; // NOSONAR

    /** Resources messages can be loaded from. */
    private final Resources resources;
    
    /**
     * Prints a help message to the screen  if the command-line option is selected. 
     *
     * @return true if it prints the message, false otherwise
     */
    public boolean maybePrintHelp(
            CommandLine line,
            Options options,
            HelpConfig helpConfig) {
        return runIfOption(line, CommandLineOptions.SHORT_OPTION_HELP, () ->
            printHelp(
                    options,
                    helpConfig.getCommandName(),
                    helpConfig.getFirstArgument())
        );
    }

    public boolean maybePrintVersion(CommandLine line)
            throws IOException {
        return runIfOption(line, CommandLineOptions.SHORT_OPTION_VERSION, this::printVersion);
    }
    
    /**
     * Prints the available pre-defined tasks if the command-line option is selected.
     * 
     * The predefined tasks come from the .xml files found in the config/tasks/ directory.
     * 
     * @return true if it prints the message, false otherwise
     */
    public boolean maybeShowTasks(CommandLine line, Path tasksDirectory) {
        return runIfOption(line, CommandLineOptions.SHORT_OPTION_SHOW_TASKS, ()->
            PredefinedTasks.printTasksToConsole(tasksDirectory, resources, PRINT_TO)
        );
    }
        
    /**
     * Describes which version of anchor is being used, and what version number
     *
     * @throws IOException if it's not possible to determine the version number
     */
    private void printVersion() throws IOException {
        PRINT_TO.printf(
                "anchor version %s by Owen Feehan (ETH Zurich, University of Zurich, 2016)%n",
                resources.versionFromMavenProperties());
        PRINT_TO.println();
        PRINT_TO.print(resources.versionFooter());
    }
    
    /**
     * Prints help message to guide usage to standard-output.
     *
     * @param options possible user-options
     */
    private void printHelp(
            Options options,
            String commandNameInHelp,
            String firstArgumentInHelp) {

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();

        String firstLine =
                String.format("%s [options] [%s]", commandNameInHelp, firstArgumentInHelp);
        formatter.printHelp(firstLine, resources.usageHeader(), options, resources.usageFooter());
    }
    
    /**
     * Runs some code if a particular command-line option is activated.
     * 
     * @param <E> an exception that may be thrown by {@code runnable}
     * @param line the command-line options present
     * @param option the option to check if it has been run
     * @param runnable code to run if {@code option} is activated in {@code line}
     * @return true if the code was run, false otherwise
     * @throws E if thrown by {@code runnable}
     */
    private static <E extends Exception> boolean runIfOption(CommandLine line, String option, CheckedRunnable<E> runnable) throws E {
        if (line.hasOption(option)) {
            runnable.run();
            return true;
        } else {
            return false;
        }
    }
}
