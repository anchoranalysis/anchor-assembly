/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.anchoranalysis.launcher.executor.selectparam;

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;

/**
 * Different methods of selecting a object {@code T} that is used as a parameter for an experiment.
 *
 * @param <T> object-type that is used as a parameter for an experiment.
 */
public interface SelectParam<T> {

    /**
     * Retrieves the parameter.
     *
     * <p>Note that the {@link ExecutionArguments} might be modified during this operation (e.g.
     * adding a directory parameter).
     */
    T select(ExecutionArguments executionArguments) throws ExperimentExecutionException;

    /**
     * Is this the default option that occurs without any additional user effort?
     *
     * @return true iff it is the default option.
     */
    boolean isDefault();

    /**
     * A string that can be displayed to the user to describe this particular {@code SelectParam}.
     *
     * @return a descriptive-string.
     */
    String describe() throws ExperimentExecutionException;
}
