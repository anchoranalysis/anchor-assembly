package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/**
 * Loads a custom-manager from a path
 * 
 * @author Owen Feehan
 *
 */
class CustomManagerFromPath extends SelectParam<Path> {

	private Path path;

	public CustomManagerFromPath(Path path) {
		super();
		this.path = path;
	}

	@Override
	public Path select( ExperimentExecutionArguments eea ) {
		return path;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public String describe() throws ExperimentExecutionException {
		return String.format(
			"from %s",
			PrettyPathConverter.prettyPath(path)
		);
	}
}
