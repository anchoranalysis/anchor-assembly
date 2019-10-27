package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PrettyPathConverter {

	// If we have more than this in a relative-path, we show absolute paths instead
	private static int MAX_DOUBLE_DOTS_CNT = 3;
	
	/** Converts to either a normalized absolute-path or relative-path depending on which is prettier to the user */
	public static String prettyPath( String path ) {
		return prettyPath(
			Paths.get(path)
		);
	}
	
	/** Converts to either a normalized absolute-path or relative-path depending on which is prettier to the user */
	public static String prettyPath( Path path ) {
		Path workingDir = completelyNormalize( Paths.get("") );
		return prettyPath(
			completelyNormalize(path),
			completelyNormalize(workingDir)
		);
	}
	
	public static String prettyPath( Path path, Path workingDir ) {
		
		if (workingDir.equals(path)) {
			// Special case to handle when directories are equal, as for some reason the Java
			//   relativize command returns .. rather than .
			return ".";
		}
		
		Path relativePath = workingDir.relativize(path).normalize();
		
		if( countDoubleDotsInRelativePath(relativePath) > MAX_DOUBLE_DOTS_CNT ) {
			return path.toString();
		} else {
			return relativePath.toString();
		}
	}
	
	static Path completelyNormalize( Path path ) {
		return path.toAbsolutePath().normalize();
	}
	
	
	/** Counts how many times ../ appears in a relative-path */
	private static int countDoubleDotsInRelativePath( Path path ) {

		int cnt = 0;
		
		for (Path p : path ) {
			if (p.getFileName().toString().equals("..")) {
				cnt++;
			}
		}
		
		return cnt++;
	}
}
