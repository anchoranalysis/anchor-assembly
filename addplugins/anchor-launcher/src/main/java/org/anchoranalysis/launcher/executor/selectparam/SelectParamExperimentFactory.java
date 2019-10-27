package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.FilePathResolver;
import org.apache.commons.cli.CommandLine;

public class SelectParamExperimentFactory {

	public static SelectParam<Path> defaultExperimentOrCustom( CommandLine line, String relativePathProperties, FilePathResolver resolver ) throws ExperimentExecutionException {
		
		// It should only be possible to have 0 or 1 args, due to prior check
		if (line.getArgs().length==1) {
			return new ExperimentPassedAsPath( extractPath(line) );
			
		} else {
			// We check to see if a defaultExperimentPath is passed and use this instead
			//
			// This is a useful workaround to allow a helper application (e.g. WinRunJ) to always pass a defaultExperimentPath into the application
			//  on the command-line, rather than through a properties file or some other method (which might not have the correct
			//  path relative to the working directory.
			//
			// The default path is simply ignored if the user specifies their own explicit path
			return new DefaultExperiment(relativePathProperties, resolver);
		}
	}
	
	private static Path extractPath( CommandLine line ) {
		return Paths.get( line.getArgs()[0] );
	}
}
