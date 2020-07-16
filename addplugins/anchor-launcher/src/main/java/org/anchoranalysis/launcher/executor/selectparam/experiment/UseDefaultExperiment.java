/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.experiment;

import java.nio.file.Path;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.PrettyPathConverter;

class UseDefaultExperiment implements SelectParam<Path> {

    private Path defaultExperiment;

    public UseDefaultExperiment(Path defaultExperiment) {
        super();
        this.defaultExperiment = defaultExperiment;
    }

    @Override
    public Path select(ExperimentExecutionArguments eea) throws ExperimentExecutionException {
        return defaultExperiment;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.format(
                "default experiment (%s)", PrettyPathConverter.prettyPath(defaultExperiment));
    }
}
