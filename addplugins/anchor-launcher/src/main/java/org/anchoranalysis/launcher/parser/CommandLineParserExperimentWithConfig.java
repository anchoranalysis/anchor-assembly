package org.anchoranalysis.launcher.parser;

import java.nio.file.Path;
import org.anchoranalysis.core.file.PathUtilities;
import org.anchoranalysis.core.log.LogErrorReporter;

public abstract class CommandLineParserExperimentWithConfig extends CommandLineParserExperiment {
	
	/**
	 * A path to a folder where config files are stored (relative to the bin/ directory)
	 */
	private static String CONFIG_RELATIVE_PATH = "../config/";
	
	protected CommandLineParserExperimentWithConfig(LogErrorReporter logger, boolean newlinesBeforeError) {
		super(logger, newlinesBeforeError);
	}
	
	protected static Path configDir( Class<?> c ) {
		Path pathCurrentJARDir = PathUtilities.pathCurrentJAR(c);
	    return pathCurrentJARDir.resolve(CONFIG_RELATIVE_PATH);
	}
}
