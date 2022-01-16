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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.resources.Resources;

/**
 * Loading and printing predefined-tasks.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredefinedTasks {

    /**
     * Prints names of the predefined tasks that are available to the application.
     *
     * @param tasksDirectory the directory in which the task XML files reside
     * @param resources resource-loader
     * @param printTo the stream to print to
     */
    public static void printTasksToConsole(
            Path tasksDirectory, Resources resources, PrintStream printTo) {

        try {
            Multimap<String, String> tasksIndexed =
                    indexByDirectory(FindTasks.taskNames(tasksDirectory));
            if (!tasksIndexed.isEmpty()) {
                printTo.printf("There are %d predefined tasks:%n", tasksIndexed.size());
                printTo.println();
                for (String key : tasksIndexed.keySet()) {
                    printTo.println(describeDirectory(key, tasksIndexed.get(key)) );
                }
                printTo.println();
                printTo.printf(
                        "Consider running a task with the -%s <taskName> command line option.%n",
                        CommandLineOptions.SHORT_OPTION_TASK);
                printTo.println("e.g. anchor -t resize");
                printTo.println("e.g. anchor -t montage/reorder");
                printTo.println("e.g. anchor -t segment/text");
                printTo.println();
                printTo.println("When no task is explicitly specified, the default task is used: summmarize");
                printTo.println(resources.tasksFooter());
            } else {
                printTo.printf("No predefined tasks exist (in %s%n).", tasksDirectory);
            }
        } catch (InputReadFailedException e) {
            printTo.printf(
                    "An error occurred searching the file-system for predefined tasks: %s",
                    e.toString());
        }
    }

    /**
     * Indexes each identifier by its directory component.
     *
     * <p>If no directory component exists, the empty string is used as a key.
     *
     * @return a newly created map from the directory-component to the original identifier (both
     *     keys and values for a given key are in alphabetical order).
     */
    private static Multimap<String, String> indexByDirectory(Stream<String> identifiers) {
        Multimap<String, String> map = MultimapBuilder.treeKeys().treeSetValues().build();
        identifiers.forEach(identifier -> {
        	Path path = Paths.get(identifier);
        	map.put(directoryComponent(path), fileNameComponent(path));
        });
        return map;
    }

    /**
     * Extracts the directory component from a path.
     *
     * @param path the path to extract from.
     * @return the directory part of the path (using forward slashes) or an empty string it doesn't exist.
     */
    private static String directoryComponent(Path path) {
        Path parent = path.getParent();
        if (parent != null) {
            return FilePathToUnixStyleConverter.toStringUnixStyle(parent);
        } else {
            return "";
        }
    }
    
    /**
     * Extracts the filename component from a path.
     * 
     * @param path the path to extract from.
     * @return the filename part of the path.
     */
    private static String fileNameComponent(Path path) {
    	return path.getFileName().toString();
    }
    
    /**
     * A string describing all the tasks in a particular directory.
     * 
     * @param directory the directory part of a predefined-task, without a trailing forward slash, and using only forward slashes. Empty for the root directory.
     * @param filenames the filenames that exist for predefefined-tasks in {@code directory}.
     * @return a string with or without out newlines, describing all the predefined-tasks in this directory, such that a user can potentially call them from the command-line.
     */
    private static String describeDirectory(String directory, Collection<String> filenames) {
    	if (!directory.isEmpty()) {
    		String firstFilename = filenames.iterator().next();
    		if (filenames.size()==1) {
    			return directory + "/" + firstFilename;
    		} else {
    			return String.format("%s/{%s}", directory, String.join(" | ", filenames));
    		}
    	} else {
    		// Tasks lying in the root directory
    		return String.join(" | ", filenames);
    	}
    }
}
