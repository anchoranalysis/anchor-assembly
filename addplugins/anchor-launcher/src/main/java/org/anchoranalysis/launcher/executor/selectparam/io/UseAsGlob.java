/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.io.glob.GlobExtractor;
import org.anchoranalysis.io.glob.GlobExtractor.GlobWithDirectory;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Uses the path directory as a manager
 *
 * @author Owen Feehan
 */
class UseAsGlob implements SelectParam<Optional<Path>> {

    private String wildcardStr;

    /**
     * Constructor
     *
     * @param wildcardStr string containing a wildcard
     */
    public UseAsGlob(String wildcardStr) {
        super();
        this.wildcardStr = wildcardStr;
    }

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {

        // Isolate a directory component, from the rest of the glob
        // to allow matches like sdsds/sdsds/*.jpg
        GlobWithDirectory gwd = GlobExtractor.extract(wildcardStr);

        eea.setInputDirectory(gwd.getDirectory().map(Paths::get));
        eea.setInputFilterGlob(Optional.of(gwd.getGlob()));

        // An empty set, means no filter check is applied
        eea.setInputFilterExtensions(Optional.of(new HashSet<String>()));

        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return wildcardStr;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
