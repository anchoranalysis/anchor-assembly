/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/**
 * Uses whatever default-manager exists
 *
 * @author Owen Feehan
 */
public class UseDefaultManager implements SelectParam<Optional<Path>> {

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {
        return Optional.empty();
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return "default manager on current working directory";
    }
}
