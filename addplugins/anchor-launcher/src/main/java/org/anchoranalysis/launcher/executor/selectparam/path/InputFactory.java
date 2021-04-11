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

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.ArgumentConverter;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;

/**
 * {@code SelectParam<Path>} factory for inputs.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputFactory {

    public static SelectParam<Optional<Path>> pathOrDirectoryOrGlobOrExtension(String[] arguments)
            throws InvalidPathArgumentException {
        List<Path> paths = pathFromArguments(arguments);
        return OptionalUtilities.orFlat(
                        checkWildcard(arguments),
                        checkXmlExtension(arguments),
                        checkFileExtension(arguments),
                        checkDirectory(paths))
                .orElseGet(() -> new UseListFilesForManager(paths));
    }

    /** If it contains a wildcard, assume its a glob */
    private static Optional<SelectParam<Optional<Path>>> checkWildcard(String[] arguments)
            throws InvalidPathArgumentException {
        return check(
                Arrays.stream(arguments).anyMatch(s -> s.contains("*")),
                arguments.length == 1,
                () -> new UseAsGlob(arguments[0]),
                "Only a single wildcard argument is permitted to -i");
    }

    /** If it begins with a period, and no slashes, then assume it's a file extension */
    private static Optional<SelectParam<Optional<Path>>> checkFileExtension(String[] args)
            throws InvalidPathArgumentException {
        return check(
                Arrays.stream(args).anyMatch(ExtensionHelper::isFileExtension),
                Arrays.stream(args).allMatch(ExtensionHelper::isFileExtension),
                () -> new UseAsExtension(args),
                "If a file-extension (e.g. .png) is specified, all other arguments to -i must also be file-extensions");
    }

    /** If an argument end with .xml, assumes it's an input-manager */
    private static Optional<SelectParam<Optional<Path>>> checkXmlExtension(String[] args)
            throws InvalidPathArgumentException {
        return check(
                Arrays.stream(args).anyMatch(ExtensionHelper::hasXmlExtension),
                args.length == 1,
                () -> new UseAsCustomManager(ArgumentConverter.pathFromArgument(args[0])),
                "Only a single BeanXML argument is permitted after -i (i.e. with a path with a .xml extension)");
    }

    /** If it's a directory path, then use the directory to find inputs */
    private static Optional<SelectParam<Optional<Path>>> checkDirectory(List<Path> paths)
            throws InvalidPathArgumentException {
        return check(
                paths.stream().anyMatch(path -> path.toFile().isDirectory()),
                paths.size() == 1,
                () -> new UseDirectoryForManager(paths.get(0), true, true),
                "Only a single argument is permitted after -i if it's a directory");
    }

    private static <T> Optional<T> check(
            boolean condition1,
            boolean condition2,
            CheckedSupplier<T, InvalidPathArgumentException> val,
            String errorMessage)
            throws InvalidPathArgumentException {
        if (condition1) {
            if (condition2) {
                return Optional.of(val.get());
            } else {
                throw new CommandLineException(errorMessage);
            }
        }
        return Optional.empty();
    }

    private static List<Path> pathFromArguments(String[] args) throws InvalidPathArgumentException {
        return FunctionalList.mapToList(
                args, InvalidPathArgumentException.class, ArgumentConverter::pathFromArgument);
    }
}
