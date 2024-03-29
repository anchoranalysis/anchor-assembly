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

package org.anchoranalysis.launcher.executor.selectparam.experiment;

import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.PrettyPathConverter;

@AllArgsConstructor
class UseDefaultExperiment implements SelectParam<Path> {

    private final Path defaultExperiment;

    @Override
    public Path select(ExecutionArguments executionArguments) throws ExperimentExecutionException {
        return defaultExperiment;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.format(
                "default experiment (%s)", PrettyPathConverter.prettyPath(defaultExperiment));
    }
}
