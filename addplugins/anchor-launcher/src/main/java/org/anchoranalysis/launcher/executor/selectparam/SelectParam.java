/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/** Different methods of selecting a object T that is used as a parameter for the experiment */
public interface SelectParam<T> {

    /**
     * Retrieves the parameter
     *
     * <p>Note that the ExperimentExecutionArguments might be modified during this operation (e.g.
     * adding a directory parameter )
     */
    T select(ExperimentExecutionArguments eea) throws ExperimentExecutionException;

    /**
     * Returns TRUE iff this is the default option that occurs without any additional user effort
     *
     * @return
     */
    boolean isDefault();

    /**
     * Returns a string that can be displayed to the user to describe this particular SelectParan
     *
     * @return
     */
    String describe() throws ExperimentExecutionException;
}
