package org.anchoranalysis.launcher.run.tasks;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Builds an index of available predefined-task names, indexed by their subdirectory-name.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class TasksIndexer {

	 /**
     * Indexes each identifier by its directory component.
     *
     * <p>If no directory component exists, the empty string is used as a key.
     *
     * @return a newly created map from the directory-component to the original identifier (both
     *     keys and values for a given key are in alphabetical order).
     */
    public static Multimap<String, String> indexBySubdirectory(Stream<String> identifiers) {
        Multimap<String, String> map = MultimapBuilder.treeKeys().treeSetValues().build();
        identifiers.forEach(
                identifier -> {
                    Path path = Paths.get(identifier);
                    map.put(directoryComponent(path), fileNameComponent(path));
                });
        return map;
    }

    /**
     * Extracts the directory component from a path.
     *
     * @param path the path to extract from.
     * @return the directory part of the path (using forward slashes) or an empty string it doesn't
     *     exist.
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
}
