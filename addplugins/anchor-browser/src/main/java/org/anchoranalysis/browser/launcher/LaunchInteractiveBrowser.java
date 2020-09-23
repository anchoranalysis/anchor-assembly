/*-
 * #%L
 * anchor-browser
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

package org.anchoranalysis.browser.launcher;

import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.experiment.log.MessageLoggerList;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.experiment.log.reporter.TextFileMessageLogger;
import org.anchoranalysis.launcher.Launch;

/**
 * Command-line application for launching the anchor GUI application, otherwise known as interactive-browser.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LaunchInteractiveBrowser {

    private static final String LOG_FILE_PATH = "anchorGUI.log";
    
    /**
     * Entry point for command-line application
     *
     * @param args command line application
     */
    public static void main(String[] args) {
        Logger logger = createLogger();
        Launch.runCommandLineApp(args, new LauncherConfigBrowser(), logger);
    }

    private static Logger createLogger() {
        StatefulMessageLogger consoleLogger = new ConsoleMessageLogger();

        StatefulMessageLogger textLogger = new TextFileMessageLogger(
                LOG_FILE_PATH, new ErrorReporterIntoLog(consoleLogger));
        
        MessageLoggerList list =
                new MessageLoggerList(
                        Stream.of(consoleLogger,textLogger));
        return new Logger(list);
    }
}
