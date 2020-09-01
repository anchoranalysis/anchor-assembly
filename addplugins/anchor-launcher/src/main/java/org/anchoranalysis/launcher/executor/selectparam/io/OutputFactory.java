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

package org.anchoranalysis.launcher.executor.selectparam.io;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.CustomManagerFromPath;
import org.anchoranalysis.launcher.executor.selectparam.path.InvalidPathArgumentException;
import org.anchoranalysis.launcher.executor.selectparam.path.ArgumentConverter;

/**
 * SelectParam<Path> factory for outputs
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputFactory {

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

        try {
            Path path = ArgumentConverter.pathFromArgument(arg[0]);
    
            if (path.toFile().isDirectory()) {
                return new UseDirectoryForManager(path, input);
            } else {
                return new CustomManagerFromPath(path);
            }
        } catch (InvalidPathArgumentException e) {
            throw e.toCommandLineException();
        }
    }
}
