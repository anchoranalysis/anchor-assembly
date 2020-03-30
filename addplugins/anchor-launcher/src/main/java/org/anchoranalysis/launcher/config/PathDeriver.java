package org.anchoranalysis.launcher.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.anchoranalysis.experiment.ExperimentExecutionException;

class PathDeriver {
	
	/**
	 * A property that indicates a relative path to a default properties file
	 */
	private static final String PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT = "default.config.path.relative";
	
	private PathDeriver() {
	}
	
	/**
	 * Path to default-experiment
	 * 
	 * @return a path to the defaultExperiment
	 * @throws ExperimentExecutionException 
	 */
	public static Path pathDefaultExperiment( Path pathCurrentJARDir, String pathRelativeProperties ) throws ExperimentExecutionException {
		
		String relativePathDefaultExperiment = relativePathDefaultExperiment(
    		propertyPath( pathCurrentJARDir, pathRelativeProperties )
    	); 
		
	    return pathCurrentJARDir.resolve(relativePathDefaultExperiment);
	}
	
	private static String relativePathDefaultExperiment( Path propertyPath ) throws ExperimentExecutionException {
		
		Properties props = new Properties();
		
		try {
			props.load( new FileInputStream(propertyPath.toFile()) );
		} catch (IOException e) {
			throw new ExperimentExecutionException(
				String.format(
					"An error occurred loading properties from the properties file at %s)",
					propertyPath
				),
				e
			);
		}
		
		if (!props.containsKey(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT)) {
			throw new ExperimentExecutionException(
				String.format("Properties file is missing key: %s", PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT)
			);
		}
		
		return props.getProperty(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT);
	}
	
	
	private static Path propertyPath( Path currentJARDir, String pathRelativeProperties ) throws ExperimentExecutionException {
		
		Path pathProperties = currentJARDir.resolve( pathRelativeProperties );
		
		if (!Files.exists(pathProperties)) {
			throw new ExperimentExecutionException(
				String.format("Cannot find properties file at: %s", pathProperties)
			);
		}
		
		return pathProperties;
	}
}
