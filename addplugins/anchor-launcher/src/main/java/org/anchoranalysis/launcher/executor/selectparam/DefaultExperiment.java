package org.anchoranalysis.launcher.executor.selectparam;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.FilePathResolver;

class DefaultExperiment extends SelectParam<Path> {

	/**
	 * A property that indicates a relative path to a default properties file
	 */
	private static final String PROPERTY_PATH_RELATIVE_DEFAULT_CONFIG = "default.config.path.relative";
	

	private String relativePathProperties;
	private FilePathResolver resolver;
		
	// Caches experimentPath
	private String experimentPath = null;
	
	public DefaultExperiment(String relativePathProperties, FilePathResolver resolver ) {
		super();
		this.relativePathProperties = relativePathProperties;
		this.resolver = resolver;
	}
	
	@Override
	public Path select(ExperimentExecutionArguments eea) throws ExperimentExecutionException {

		try {
			return resolver.resolvePathToCurrentJar(
				defaultExperimentPathRelative()
			);
			
		} catch (Exception e) {
			
			throw new ExperimentExecutionException(
				String.format(
					"Cannot load a default experiment from %s",
					relativePathProperties
				),
				e
			);
		}
	}


	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public String describe() throws ExperimentExecutionException {
		return String.format(
			"default experiment (%s)",
			PrettyPathConverter.prettyPath( defaultExperimentPathRelative() )
		);
	}
	
	// Calculates the path to the default experiment (caching)
	private String defaultExperimentPathRelative() throws ExperimentExecutionException {
		
		if (experimentPath==null) {
			experimentPath = relativePathConfig(
				propertyPath()
			);
		}
		
		return experimentPath;
	}
	
	private Path propertyPath() throws ExperimentExecutionException {
		
		Path pathProperties = resolver.resolvePathToCurrentJar(relativePathProperties);
		
		if (!Files.exists(pathProperties)) {
			throw new ExperimentExecutionException(
				String.format("Cannot find properties file at: %s", pathProperties)
			);
		}
		
		return pathProperties;
	}
	
	private static String relativePathConfig( Path pathProperties ) throws ExperimentExecutionException {
		
		Properties props = new Properties();
		
		try {
			props.load( new FileInputStream(pathProperties.toFile()) );
		} catch (IOException e) {
			throw new ExperimentExecutionException(
				String.format(
					"An error occurred loading properties from the properties file at %s)",
					pathProperties
				),
				e
			);
		}
		
		if (!props.containsKey(PROPERTY_PATH_RELATIVE_DEFAULT_CONFIG)) {
			throw new ExperimentExecutionException(
				String.format("Properties file is missing key: %s", PROPERTY_PATH_RELATIVE_DEFAULT_CONFIG)
			);
		}
		
		return props.getProperty(PROPERTY_PATH_RELATIVE_DEFAULT_CONFIG);
	}
}
