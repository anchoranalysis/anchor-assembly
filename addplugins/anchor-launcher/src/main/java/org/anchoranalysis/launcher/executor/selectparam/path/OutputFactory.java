/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.anchoranalysis.launcher.executor.selectparam.path;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.ArgumentConverter;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;

/**
 * {@code SelectParam<Path>} factory for outputs.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputFactory {

    /**
     * If the argument is a path to a directory, then this directory is set as the default.
     *
     * <p>Otherwise the argument is treated like a path to BeanXML.
     *
     * @throws CommandLineException
     */
    public static SelectParam<Optional<Path>> pathOrDirectory(String[] arguments, boolean input) {

        if (arguments.length > 1) {
            throw new CommandLineException(
                    "More than one argument was passed to -o. Only one is allowed!");
        }
        
        String pathArgument = arguments[0];

        try {
            Path path = ArgumentConverter.pathFromArgument(pathArgument);
            File file = path.toFile();
            if (file.isDirectory()) {
                // If the path exists AND is a directory...
                return new UseDirectoryForManager(path, input);
            } else if (file.exists()) {
                // If the path exists BUT isn't a directory
                return new UseAsCustomManager(path);
            } else {
                // If the path doesn't exist, but looks like a directory
                if (looksLikeDirectoryPath(pathArgument)) {
                    // Make the parent directory into which the outputter will create a new subdirectory for this experiment
                    file.mkdirs();
                    return new UseDirectoryForManager(path, input);
                } else {
                    throw new CommandLineException(
                          String.format(
                               "The argument '%s' passed to -o is:%n- neither a path to an existing file (taken as BeanXML for output-manager)%n- nor looks like a directory (used for outputting into a subdirectory)%n", pathArgument));
                }
            }
        } catch (InvalidPathArgumentException e) {
            throw e.toCommandLineException();
        }
    }
    
    private static boolean looksLikeDirectoryPath(String argument) {
        return argument.endsWith("/") || argument.endsWith("\\");
    }
}
