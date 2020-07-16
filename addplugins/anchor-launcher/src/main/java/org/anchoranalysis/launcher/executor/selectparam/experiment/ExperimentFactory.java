/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.experiment;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.apache.commons.cli.CommandLine;

/**
 * SelectParam<Path> factory for experiments
 *
 * @author Owen Feehan
 */
public class ExperimentFactory {

    private ExperimentFactory() {}

    /**
     * Chooses either a path to the default-experiment or a path to a custom experiment.
     *
     * @param line
     * @param defaultExperiment path to the default experiment
     * @return
     * @throws ExperimentExecutionException
     */
    public static SelectParam<Path> defaultExperimentOrCustom(
            CommandLine line, Path defaultExperiment) throws ExperimentExecutionException {

        // It should only be possible to have 0 or 1 args, due to prior check
        if (line.getArgs().length == 1) {
            return new UseExperimentPassedAsPath(extractPath(line));

        } else {
            // We check to see if a defaultExperimentPath is passed and use this instead
            //
            // This is a useful workaround to allow a helper application (e.g. WinRunJ) to always
            // pass a defaultExperimentPath into the application
            //  on the command-line, rather than through a properties file or some other method
            // (which might not have the correct
            //  path relative to the working directory.
            //
            // The default path is simply ignored if the user specifies their own explicit path
            return new UseDefaultExperiment(defaultExperiment);
        }
    }

    private static Path extractPath(CommandLine line) throws ExperimentExecutionException {
        String str = line.getArgs()[0];

        if (str.contains("*")) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Error: Cannot accept a wildcard in path to experiment BeanXML: %s",
                            str));
        }
        try {
            return Paths.get(str);
        } catch (InvalidPathException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Error: The argument \"%s\" should be a path to experiment BeanXML, but is invalid.",
                            str));
        }
    }
}
