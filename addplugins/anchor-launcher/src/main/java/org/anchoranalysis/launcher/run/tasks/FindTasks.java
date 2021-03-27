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
package org.anchoranalysis.launcher.run.tasks;

import com.google.common.base.Preconditions;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.path.matcher.MatchGlob;

/**
 * Searches a tasks directory to find the names of pre-defined tasks.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindTasks {

    private static final MatchGlob MATCHER =
            new MatchGlob("*." + NonImageFileFormat.XML.getDefaultExtension());

    /** Ignore any task's whose identifier begins with this string */
    private static final String IGNORE_SUBDIRECTORY = "include/";

    /**
     * All task-names.
     *
     * <p>The name is:
     *
     * <ul>
     *   <li>The relative-path of the task-name (to {@code tasksDirectory}
     *   <li>without any XML extension
     *   <li>using forward-slashes as directory separators, irrespective of operating system.
     * </ul>
     */
    public static Stream<String> taskNames(Path tasksDirectory) throws InputReadFailedException {
        // Note that on some systems, for currently undiagnoses reasons, the tasks identifiers
        // emerge
        //  with leading . and .. relatie-path elements. As a workaround, these are filtered from
        //  the task identifiers.
        Stream<Path> taskPaths = allXmlFiles(tasksDirectory).stream().map(File::toPath);
        Stream<String> taskNames = taskPaths.map(path -> taskIdentifier(tasksDirectory, path));
        return taskNames.filter(name -> !name.startsWith(IGNORE_SUBDIRECTORY));
    }

    /** All XML files recursively in the tasks directory. */
    private static Collection<File> allXmlFiles(Path tasksDirectory)
            throws InputReadFailedException {
        return MATCHER.matchingFiles(
                tasksDirectory, true, true, true, Optional.empty(), Optional.empty());
    }

    private static String taskIdentifier(Path tasksDirectory, Path path) {
        Path relative = removeLeadingPeriods(tasksDirectory.relativize(path));
        String unixStyle = FilePathToUnixStyleConverter.toStringUnixStyle(relative);
        return removeXmlExtension(unixStyle, NonImageFileFormat.XML.getDefaultExtension());
    }

    /** Removes the <code>.xml</code> suffix from a task. */
    private static String removeXmlExtension(String identifier, String extension) {
        Preconditions.checkArgument(identifier.endsWith(extension));
        return identifier.substring(0, identifier.length() - extension.length() - 1);
    }

    /** Removing any leading <code>.</code> and <code>..</code> elements from a path. */
    private static Path removeLeadingPeriods(Path path) {
        Preconditions.checkArgument(!path.isAbsolute());
        Preconditions.checkArgument(path.getNameCount() > 0);
        int firstNonPeriod = -1;
        for (int i = 0; i < path.getNameCount(); i++) {
            String element = path.getName(i).toString();
            if (!element.equals(".") && !element.equals("..")) {
                firstNonPeriod = i;
                break;
            }
        }
        Preconditions.checkArgument(firstNonPeriod != -1);
        return path.subpath(firstNonPeriod, path.getNameCount());
    }
}
