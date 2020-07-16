/* (C)2020 */
package org.anchoranalysis.launcher;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.parser.ParseArgsAndRunExperiment;

/**
 * A command-line interface used for launching experiments
 *
 * @author Owen Feehan
 */
public class Launch {

    private Launch() {
        // Class should only be accessed through static methods
    }

    /**
     * Entry point for command-line application
     *
     * @param args command line application
     */
    public static void main(String[] args) {
        Logger logger = new Logger(new ConsoleMessageLogger());
        runCommandLineApp(args, new LauncherConfigCommandLine(), logger);
    }

    /**
     * Runs a command-line app, by parsing arguments
     *
     * @param args args from command-line application
     * @param parser a parser for this command-line application
     */
    public static void runCommandLineApp(
            String[] args, LauncherConfig parserConfig, Logger logger) {
        DirtyInitializer.dirtyInitialization();
        new ParseArgsAndRunExperiment(logger).parseAndRun(args, parserConfig);
    }
}
