/* (C)2020 */
package org.anchoranalysis.launcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

public class PathCurrentJarUtilities {

    private PathCurrentJarUtilities() {}

    /**
     * Determines the path to the current jar directory (or folder with class files) so we can
     * resolve a properties file
     *
     * @param c the class which was used to launch the application (or another class with the same
     *     codeSource)
     * @return a path (always a folder) to the current jar (or folder with class files)
     */
    public static Path pathCurrentJAR(Class<?> c) {
        URI pathURI;
        try {
            pathURI = c.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new AnchorFriendlyRuntimeException(
                    "An invalid URI was used in establishing the path to the current JAR", e);
        }
        Path path = Paths.get(pathURI);

        if (path.toFile().isDirectory()) {
            // If it's a folder this is good enough, and we return it
            return path;
        } else {
            // If it's a file, then we assume this is path to the jar, and return its parent folder
            return path.getParent();
        }
    }
}
