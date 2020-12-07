package org.anchoranalysis.launcher.run.tasks;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.resources.Resources;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Loading and printing predefined-tasks.
 * 
 * @author Owen Feehan
  */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class PredefinedTasks {
    
    /**
     * Prints names of the predefined tasks that are available to the application. 
     * 
     * @param tasksDirectory the directory in which the task XML files reside
     * @param resources resource-loader
     * @param printTo the stream to print to
     */
    public static void printTasksToConsole(Path tasksDirectory, Resources resources, PrintStream printTo) {

        try {
            Multimap<String,String> tasksIndexed = indexByDirectory(FindTasks.taskNames(tasksDirectory));
            if (!tasksIndexed.isEmpty()) {
                printTo.printf("There are %d predefined tasks:%n", tasksIndexed.size());
                printTo.println();
                for(String key : tasksIndexed.keySet()) {
                    printTo.println( String.join(", ", tasksIndexed.get(key)));
                }
                printTo.println();
                printTo.printf("Consider running a task with the -%s <taskName> command line option.%n", CommandLineOptions.SHORT_OPTION_TASK);
                printTo.println(resources.tasksFooter());
            } else {
                printTo.printf("No predefined tasks exist (in %s%n).", tasksDirectory);
            }
        } catch (InputReadFailedException e) {
            printTo.printf("An error occurred searching the file-system for predefined tasks: %s", e.toString());
        }
    }
    
    /**
     * Indexes each identifier by its directory component.
     * 
     * <p>If no directory component exists, the empty string is used as a key.
     * 
     * @return a newly created map from the directory-component to the original identifier (both keys and values for a given key are in alphabetical order).
     */
    private static Multimap<String,String> indexByDirectory(Stream<String> identifiers) {
        Multimap<String,String> map = MultimapBuilder.treeKeys().treeSetValues().build();
        identifiers.forEach( identifier -> map.put(directoryComponent(identifier), identifier) );
        return map;
    }
    
    /**
     * Extracts the directory component from a path
     * 
     * @param path a path encoded as a string
     * @return the directory part of the path (using slashes as default for the operating system) or an empty string it doesn't exist.
     */
    private static String directoryComponent(String path) {
        Path parent = Paths.get(path).getParent();
        if (parent!=null) {
            return parent.toString();
        } else {
            return "";
        }
    }
}
