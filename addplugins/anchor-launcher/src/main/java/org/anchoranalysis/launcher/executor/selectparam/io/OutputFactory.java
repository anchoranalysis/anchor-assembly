/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.io;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.CustomManagerFromPath;
import org.anchoranalysis.launcher.executor.selectparam.path.PathConverter;

/**
 * SelectParam<Path> factory for outputs
 *
 * @author Owen Feehan
 */
public class OutputFactory {

    private OutputFactory() {}

    /**
     * If the argument is a path to a directory, then this directory is set as the default.
     * Otherwise the argument is treated like a path to BeanXML
     *
     * @throws CommandLineException
     */
    public static SelectParam<Optional<Path>> pathOrDirectory(String[] arg, boolean input) {

        if (arg.length > 1) {
            throw new CommandLineException(
                    "More than one argument was passed to -o. Only one is allowed!");
        }

        Path path = PathConverter.pathFromArg(arg[0]);

        if (path.toFile().isDirectory()) {
            return new UseDirectoryForManager(path, input);
        } else {
            return new CustomManagerFromPath(path);
        }
    }
}
