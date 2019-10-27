package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;


/**
 * Uses the path directory as a manager
 * 
 * @author Owen Feehan
 *
 */
class UseDirectoryAsManager extends SelectParam<Path> {

	private boolean input = true;
	private Path directory;
	
	/**
	 * Constructor
	 *  
	 * @param input iff TRUE, then we are replacing the input-manager, otherwise the output-manager
	 */
	public UseDirectoryAsManager(Path directory, boolean input) {
		super();
		this.input = input;
		this.directory = directory;
		assert( directory.toFile().isDirectory() );
	}



	@Override
	public Path select( ExperimentExecutionArguments eea ) {
		
		if (input) {
			eea.setInputDirectory(directory);
		} else {
			eea.getOutputDirectory();
		}
		
		return null;
	}

	@Override
	public String describe() throws ExperimentExecutionException {
		return PrettyPathConverter.prettyPath(directory);
	}

	@Override
	public boolean isDefault() {
		return false;
	}
}
