/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.experiment;

import java.nio.file.Path;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.PrettyPathConverter;

/**
 * An experiment passed as a custom-path
 *
 * @author Owen Feehan
 */
class UseExperimentPassedAsPath implements SelectParam<Path> {

    private Path path;

    public UseExperimentPassedAsPath(Path path) {
        super();
        this.path = path;
    }

    @Override
    public Path select(ExperimentExecutionArguments eea) throws ExperimentExecutionException {

        if (path.toFile().isDirectory()) {
            throw new ExperimentExecutionException(
                    "Please select a path to experiment FILE not a folder");
        }

        return path;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.format("experiment %s", PrettyPathConverter.prettyPath(path));
    }
}
