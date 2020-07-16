/* (C)2020 */
package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.apache.commons.cli.CommandLine;

public class ExperimentExecutorFactory {

    private ExperimentExecutorFactory() {}

    /**
     * Creates an experiment-executor from a command line that EITHER: uses a default-experiment OR
     * accepts a path passed as the first command-line argument
     *
     * @param line the command-line arguments
     * @param defaultExperiment path to the default-experiment
     * @param configDir path to the configuration directory
     * @param executionDir path from which experiment is executed (i.e. the bin/ directory
     *     typically)
     * @return
     * @throws ExperimentExecutionException
     */
    public static ExperimentExecutor create(
            CommandLine line, Path defaultExperiment, Path configDir, Path executionDir)
            throws ExperimentExecutionException {
        return new ExperimentExecutor(
                SelectParamFactory.experimentSelectParam(line, defaultExperiment),
                configDir,
                executionDir);
    }
}
