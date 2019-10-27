package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;


/**
 * Uses whatever default-manager exists
 * 
 * @author Owen Feehan
 *
 */
class UseDefaultManager extends SelectParam<Path> {

	@Override
	public Path select( ExperimentExecutionArguments eea ) {
		return null;
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	@Override
	public String describe() throws ExperimentExecutionException {
		return "default manager on current working directory";
	}

}
