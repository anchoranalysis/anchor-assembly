package org.anchoranalysis.launcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathCurrentJarUtilities {

	private PathCurrentJarUtilities() {
	}
	
	/**
	 * Determines the path to the current jar directory (or folder with class files) so we can resolve
	 *   a properties file
	 * 
	 * @param c the class which was used to launch the application (or another class with the same codeSource)
	 * @return a path (always a folder) to the current jar (or folder with class files)
	 */
	public static Path pathCurrentJAR( Class<?> c ) {
		URI pathURI;
		try {
			pathURI = c.getProtectionDomain().getCodeSource().getLocation().toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		Path path = Paths.get(pathURI);
		
		if (Files.isDirectory(path)) {
			// If it's a folder this is good enough, and we return it
			return path;
		} else {
			// If it's a file, then we assume this is path to the jar, and return its parent folder
			return path.getParent();
		}
	}
}
