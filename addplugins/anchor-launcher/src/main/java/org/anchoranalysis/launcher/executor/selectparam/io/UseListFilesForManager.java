/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.io;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.PrettyPathConverter;

/**
 * Uses a list of paths to specific files as a manager
 *
 * @author Owen Feehan
 */
class UseListFilesForManager implements SelectParam<Optional<Path>> {

    private List<Path> paths;

    /**
     * Constructor
     *
     * @param input iff TRUE, then we are replacing the input-manager, otherwise the output-manager
     */
    public UseListFilesForManager(List<Path> paths) {
        super();
        this.paths = paths;
        checkNoDirectories(paths);
    }

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {
        eea.setInputPaths(paths);
        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.join(", ", FunctionalList.mapToList(paths, PrettyPathConverter::prettyPath));
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    private void checkNoDirectories(List<Path> paths) {
        for (Path p : paths) {
            assert (!p.toFile().isDirectory());
        }
    }
}
