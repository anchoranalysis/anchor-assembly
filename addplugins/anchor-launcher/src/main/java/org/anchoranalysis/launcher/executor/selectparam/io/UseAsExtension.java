/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.io;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Uses the path directory as a manager
 *
 * @author Owen Feehan
 */
class UseAsExtension implements SelectParam<Optional<Path>> {

    private String[] extensions;

    /**
     * Constructor
     *
     * @param wildcardStr string containing a wildcard
     */
    public UseAsExtension(String[] extensions) {
        super();
        this.extensions = extensions;
    }

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {

        // Remove the period from the left side
        List<String> extWithoutPeriod = removeLeadingPeriod(extensions);

        eea.setInputFilterExtensions(Optional.of(new HashSet<>(extWithoutPeriod)));

        return Optional.empty();
    }

    private static List<String> removeLeadingPeriod(String[] exts) {
        return Arrays.stream(exts).map(s -> s.substring(1)).collect(Collectors.toList());
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.join(", ", extensions);
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
