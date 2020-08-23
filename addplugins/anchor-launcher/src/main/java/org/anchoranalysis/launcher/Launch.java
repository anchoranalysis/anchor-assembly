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

package org.anchoranalysis.launcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.parser.ParseArgsAndRunExperiment;

/**
 * A command-line interface used for launching experiments
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Launch {

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
     * @param parserConfig a parser for this command-line application
     */
    public static void runCommandLineApp(
            String[] args, LauncherConfig parserConfig, Logger logger) {
        DirtyInitializer.dirtyInitialization();
        new ParseArgsAndRunExperiment(logger).parseAndRun(args, parserConfig);
    }
}
