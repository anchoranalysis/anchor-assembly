/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.path;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Loads a custom-manager from a path
 *
 * @author Owen Feehan
 */
public class CustomManagerFromPath implements SelectParam<Optional<Path>> {

    private Path path;

    public CustomManagerFromPath(Path path) {
        super();
        this.path = path;
    }

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {
        return Optional.of(path);
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.format("from %s", PrettyPathConverter.prettyPath(path));
    }
}
