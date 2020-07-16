/* (C)2020 */
package org.anchoranalysis.browser.launcher;

import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.experiment.log.MessageLoggerList;
import org.anchoranalysis.experiment.log.reporter.TextFileMessageLogger;
import org.anchoranalysis.launcher.Launch;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LaunchInteractiveBrowser {

    /**
     * Entry point for command-line application
     *
     * @param args command line application
     */
    public static void main(String[] args) {
        Logger logger = createLogErrorReporter();
        Launch.runCommandLineApp(args, new LauncherConfigBrowser(), logger);
    }

    private static Logger createLogErrorReporter() {
        ConsoleMessageLogger consoleLogger = new ConsoleMessageLogger();

        MessageLoggerList list =
                new MessageLoggerList(
                        Stream.of(
                                consoleLogger,
                                new TextFileMessageLogger(
                                        "anchorGUI.log", new ErrorReporterIntoLog(consoleLogger))));
        return new Logger(list);
    }
}
