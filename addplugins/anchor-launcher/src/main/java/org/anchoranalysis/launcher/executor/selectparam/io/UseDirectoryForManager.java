/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.io;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.PrettyPathConverter;

/**
 * Uses the path directory as a manager
 *
 * @author Owen Feehan
 */
class UseDirectoryForManager implements SelectParam<Optional<Path>> {

    private boolean input = true;
    private Path directory;

    /**
     * Constructor
     *
     * @param input iff TRUE, then we are replacing the input-manager, otherwise the output-manager
     */
    public UseDirectoryForManager(Path directory, boolean input) {
        super();
        this.input = input;
        this.directory = directory;
        if (!directory.toFile().isDirectory()) {
            throw new CommandLineException(
                    String.format(
                            "The path %s to UseDirectoryForManager must be a directory",
                            directory));
        }
    }

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {

        if (input) {
            eea.setInputDirectory(Optional.of(directory));
        } else {
            eea.setOutputDirectory(Optional.of(directory));
        }

        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return PrettyPathConverter.prettyPath(directory);
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
