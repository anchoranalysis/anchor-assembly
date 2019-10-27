package org.anchoranalysis.launcher;

import java.nio.file.Path;

import org.anchoranalysis.core.file.PathUtilities;

public class FilePathResolver {

	private Class<?> classInExecutingLoader;

	public FilePathResolver(Class<?> classInExecutingLoader) {
		super();
		this.classInExecutingLoader = classInExecutingLoader;
	}
		
	public Path resolvePathToCurrentJar( Path pathRelative ) {
		return basePath().resolve(pathRelative);
	}
	
	public Path resolvePathToCurrentJar( String pathRelative ) {
		return basePath().resolve(pathRelative);
	}
	
	private Path basePath() {
		return PathUtilities.pathCurrentJAR( classInExecutingLoader );
	}

	
}
