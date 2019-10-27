package org.anchoranalysis.launcher.executor.selectparam;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/** Different methods of selecting a object T that is used as a parameter for the experiment */
public abstract class SelectParam<T> {

	/** Retrieves the parameter
	 * 
	 *  Note that the ExperimentExecutionArguments might be modified during this operation
	 *   (e.g. adding a directory parameter )
	 *  */
	public abstract T select( ExperimentExecutionArguments eea ) throws ExperimentExecutionException;
	
	/**
	 * Returns TRUE iff this is the default option that occurs without any additional user effort
	 * @return
	 */
	public abstract boolean isDefault();
	
	/**
	 * Returns a string that can be displayed to the user to describe this particular SelectParan
	 * @return
	 */
	public abstract String describe() throws ExperimentExecutionException;
}
