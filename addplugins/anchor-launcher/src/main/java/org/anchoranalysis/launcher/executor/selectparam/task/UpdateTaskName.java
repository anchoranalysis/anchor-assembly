/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.task;

import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Updates task-name AND delegates to another SelectParam<Path>
 *
 * @author Owen Feehan
 * @param <T> delegate-type for {@link SelectParam}
 */
class UpdateTaskName<T> implements SelectParam<T> {

    private SelectParam<T> delegate;
    private String taskName;

    public UpdateTaskName(SelectParam<T> delegate, String taskName) {
        super();
        this.delegate = delegate;
        this.taskName = taskName;
    }

    @Override
    public T select(ExperimentExecutionArguments eea) throws ExperimentExecutionException {
        eea.setTaskName(Optional.of(taskName));
        return delegate.select(eea);
    }

    @Override
    public boolean isDefault() {
        return delegate.isDefault();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return delegate.describe();
    }
}
