package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Files;
import java.nio.file.Path;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/**
 * An experiment passed as a custom-path
 * 
 * @author Owen Feehan
 *
 */
class ExperimentPassedAsPath extends SelectParam<Path> {

	private Path path;
		
	public ExperimentPassedAsPath(Path path) {
		super();
		this.path = path;
	}

	@Override
	public Path select(ExperimentExecutionArguments eea) throws ExperimentExecutionException {
		
		if (Files.isDirectory(path)) {
			throw new ExperimentExecutionException("Please select a path to experiment FILE not a folder");
		}
		
		return path;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public String describe() throws ExperimentExecutionException {
		return String.format(
			"experiment %s",
			PrettyPathConverter.prettyPath(path)
		);
	}

}
