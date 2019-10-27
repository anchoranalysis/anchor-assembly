package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;

/**
 * Creates an appropriate SelectParam based upon the options passed to the command-line
 * 
 * @author Owen Feehan
 *
 */
public class SelectParamManagerFactory {
	
	/**
	 * Default option
	 * 
	 * @return
	 */
	public static SelectParam<Path> useDefault() {
		return new UseDefaultManager();
	}
	
	/**
	 * Constructor
	 * 
	 * @param line command-line to consider if certain options have been selected or not
	 * @param optionName which option we consider
	 * @param iff TRUE, this is an input-manager, otherwise it's an output manager
	 * @return an appropriate SelectParam object
	 */
	public static SelectParam<Path> create(
		CommandLine line,
		String optionName,
		boolean input
	) {
        if (line.hasOption(optionName)) {
        	
        	Path optionValuePath = Paths.get(
        		line.getOptionValue(optionName)
        	);
        	return pathOrDirectory(optionValuePath, input);
        	
        } else {
        	return new UseDefaultManager();
        }
	}
	
	/** If a path is a directory, execIfDir is called, and NULL is returned.  If a path is a non-directory, is is returned directly */
	private static SelectParam<Path> pathOrDirectory( Path path, boolean input ) {
				
		// If it's a relative path, we convert to absolute
		if (!path.isAbsolute()) {
			path = path.toAbsolutePath();
		}
		
		if (path.toFile().isDirectory()) {
    		return new UseDirectoryAsManager(path, input);
    	} else {
    		return new CustomManagerFromPath(path);	
    	}
	}
}
