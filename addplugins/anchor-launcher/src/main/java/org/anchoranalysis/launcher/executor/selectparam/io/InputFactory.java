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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.CustomManagerFromPath;
import org.anchoranalysis.launcher.executor.selectparam.path.PathConverter;

/**
 * SelectParam<Path> factory for inputs
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputFactory {

    public static SelectParam<Optional<Path>> pathOrDirectoryOrGlobOrExtension(String[] args) {
        List<Path> paths = pathFromArgs(args);
        return OptionalUtilities.orFlat(
                        checkWildcard(args), // If it contains a wildcard, assume its a glob
                        checkXmlExtension(
                                args), // If it ends with .xml, assumes it's an input-manager
                        checkFileExtension(
                                args), // If it begins with a period, and no slashes, then assume
                        // it's a file extension
                        checkDirectory(
                                paths) // If it's a directory path, then use the directory to find
                        // inputs
                        )
                .orElseGet(() -> new UseListFilesForManager(paths));
    }

    private static Optional<SelectParam<Optional<Path>>> checkWildcard(String[] args) {
        return check(
                Arrays.stream(args).anyMatch(s -> s.contains("*")),
                args.length == 1,
                () -> new UseAsGlob(args[0]),
                "Only a single wildcard argument is permitted to -i");
    }

    private static Optional<SelectParam<Optional<Path>>> checkFileExtension(String[] args) {
        return check(
                Arrays.stream(args).anyMatch(ExtensionUtilities::isFileExtension),
                Arrays.stream(args).allMatch(ExtensionUtilities::isFileExtension),
                () -> new UseAsExtension(args),
                "If a file-extension (e.g. .png) is specified, all other arguments to -i must also be file-extensions");
    }

    private static Optional<SelectParam<Optional<Path>>> checkXmlExtension(String[] args) {
        return check(
                Arrays.stream(args).anyMatch(ExtensionUtilities::hasXmlExtension),
                args.length == 1,
                () -> new CustomManagerFromPath(PathConverter.pathFromArg(args[0])),
                "Only a single BeanXML argument is permitted after -i (i.e. with a path with a .xml extension)");
    }

    private static Optional<SelectParam<Optional<Path>>> checkDirectory(List<Path> paths) {
        return check(
                paths.stream().anyMatch(p -> p.toFile().isDirectory()),
                paths.size() == 1,
                () -> new UseDirectoryForManager(paths.get(0), true),
                "Only a single argument is permitted after -i if it's a directory");
    }

    private static <T> Optional<T> check(
            boolean condition1, boolean condition2, Supplier<T> val, String errorMsg) {
        if (condition1) {
            if (condition2) {
                return Optional.of(val.get());
            } else {
                throw new CommandLineException(errorMsg);
            }
        }
        return Optional.empty();
    }

    private static List<Path> pathFromArgs(String[] args) {
        return FunctionalList.mapToList(args, PathConverter::pathFromArg);
    }
}
