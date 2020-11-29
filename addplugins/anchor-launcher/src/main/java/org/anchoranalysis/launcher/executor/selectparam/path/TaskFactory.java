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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.ArgumentConverter;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;

/**
 * {@code SelectParam<Path>} factory for tasks.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskFactory {

    /**
     * If the argument a name (no extension, no root, no special-chars apart from forward-slashes),
     * then construct an automatic path to the tasks in the configuration directory. Otherwise treat
     * as path to BeanXML
     */
    public static SelectParam<Optional<Path>> pathOrTaskName(String[] args, Path configDir) {

        if (args.length != 1) {
            throw new CommandLineException("One and only one argument is permitted after -t");
        }

        String taskArg = args[0];

        if (isTaskName(taskArg)) {
            return new UpdateTaskName<>(
                    new UseAsCustomManager(constructPathForTaskName(taskArg, configDir)),
                    taskArg);
        } else {
            try {
                return new UseAsCustomManager(ArgumentConverter.pathFromArgument(taskArg));
            } catch (InvalidPathArgumentException e) {
                throw new CommandLineException(e.toString());
            }
        }
    }

    private static Path constructPathForTaskName(
            String filenameWithoutExtension, Path configDirectory) {
        return NonImageFileFormat.XML.buildPath(
                configDirectory.resolve("tasks"), filenameWithoutExtension);
    }

    /**
     * Check if it contains only a restricted set of characters... alphaNumeric, hyphen, underscore,
     * forward-slash.
     */
    private static boolean isTaskName(String arg) {
        return arg.matches("^[a-zA-Z0-9_\\-\\/]+$");
    }
}
