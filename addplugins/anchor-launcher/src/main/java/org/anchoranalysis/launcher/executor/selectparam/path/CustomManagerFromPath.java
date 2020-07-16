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

package org.anchoranalysis.launcher.executor.selectparam.path;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Loads a custom-manager from a path
 *
 * @author Owen Feehan
 */
public class CustomManagerFromPath implements SelectParam<Optional<Path>> {

    private Path path;

    public CustomManagerFromPath(Path path) {
        super();
        this.path = path;
    }

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {
        return Optional.of(path);
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.format("from %s", PrettyPathConverter.prettyPath(path));
    }
}
