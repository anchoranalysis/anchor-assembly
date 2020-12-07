package org.anchoranalysis.launcher.run.tasks;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.path.matcher.MatchGlob;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Searches a tasks directory to find the names of pre-defined tasks.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class FindTasks {

    private static final MatchGlob MATCHER = new MatchGlob("*." + NonImageFileFormat.XML.getDefaultExtension());
    
    /** 
     * Ignore any task's whose identifier begins with this string
     */
    private static final String IGNORE_SUBDIRECTORY = "include/";
    
    /**
     * All task-names.
     * 
     * <p>The name is:
     * <ul>
     * <li>The relative-path of the task-name (to {@code tasksDirectory}</li>
     * <li>without any XML extension</li>
     * <li>using forward-slashes as directory separators, irrespective of operating system.</li>
     * </ul>
     */
    public static Stream<String> taskNames(Path tasksDirectory) throws InputReadFailedException {
        Stream<Path> taskPaths = allXmlFiles(tasksDirectory).stream().map(File::toPath);
        Stream<String> taskNames = taskPaths.map(path -> taskIdentifier(tasksDirectory, path));
        return taskNames.filter( name ->
            !name.startsWith(IGNORE_SUBDIRECTORY) 
        );
    }
    
    /** 
     * All XML files recursively in the tasks directory.
     */
    private static Collection<File> allXmlFiles(Path tasksDirectory) throws InputReadFailedException {
        return MATCHER.matchingFiles(tasksDirectory, true, true, true, Optional.empty(), Optional.empty());
    }
    
    private static String taskIdentifier(Path tasksDirectory, Path path) {
        String relative = FilePathToUnixStyleConverter.toStringUnixStyle(
            tasksDirectory.relativize(path)
        );
        return removeXmlExtension(relative, NonImageFileFormat.XML.getDefaultExtension());
    }
    
    private static String removeXmlExtension(String identifier, String extension) {
        Preconditions.checkArgument(identifier.endsWith(extension));
        return identifier.substring(0, identifier.length() - extension.length() - 1 );
    }
}
