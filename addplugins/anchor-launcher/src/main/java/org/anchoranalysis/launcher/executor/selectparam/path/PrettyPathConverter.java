/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.path;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PrettyPathConverter {

    private PrettyPathConverter() {}

    // If we have more than this in a relative-path, we show absolute paths instead
    private static final int MAX_DOUBLE_DOTS_CNT = 3;

    /**
     * Converts to either a normalized absolute-path or relative-path depending on which is prettier
     * to the user
     */
    public static String prettyPath(String path) {
        return prettyPath(Paths.get(path));
    }

    /**
     * Converts to either a normalized absolute-path or relative-path depending on which is prettier
     * to the user
     */
    public static String prettyPath(Path path) {
        Path workingDir = completelyNormalize(Paths.get(""));
        return prettyPath(completelyNormalize(path), completelyNormalize(workingDir));
    }

    static String prettyPath(Path path, Path workingDir) {

        // First we make both paths absolute and normalized, so they have non-null roots
        path = path.toAbsolutePath().normalize();
        workingDir = workingDir.toAbsolutePath().normalize();

        if (workingDir.equals(path)) {
            // Special case to handle when directories are equal, as for some reason the Java
            //   relativize command returns .. rather than .
            return ".";
        }

        if (workingDir.getRoot().equals(path.getRoot())) {
            // If on the same root, then find a relative path between them

            Path relativePath = workingDir.relativize(path).normalize();

            // Depending on the number of dots in the relative path, we show
            //   either the absolute-path or the relative-path.
            if (countDoubleDotsInRelativePath(relativePath) > MAX_DOUBLE_DOTS_CNT) {
                return path.toString();
            } else {
                return relativePath.toString();
            }

        } else {
            // If on different roots
            return path.toAbsolutePath().normalize().toString();
        }
    }

    static Path completelyNormalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    /** Counts how many times ../ appears in a relative-path */
    private static int countDoubleDotsInRelativePath(Path path) {

        int cnt = 0;

        for (Path p : path) {
            if (p.getFileName().toString().equals("..")) {
                cnt++;
            }
        }

        return cnt;
    }
}
