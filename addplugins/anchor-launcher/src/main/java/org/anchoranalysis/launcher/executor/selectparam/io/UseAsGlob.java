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

package org.anchoranalysis.launcher.executor.selectparam.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.io.glob.GlobExtractor;
import org.anchoranalysis.io.glob.GlobExtractor.GlobWithDirectory;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Uses the path directory as a manager
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class UseAsGlob implements SelectParam<Optional<Path>> {

    /** String containing a wildcard */
    private String stringWithWildcard;

    @Override
    public Optional<Path> select(ExperimentExecutionArguments executionArguments) {

        // Isolate a directory component, from the rest of the glob
        // to allow matches like sdsds/sdsds/*.jpg
        GlobWithDirectory glob = GlobExtractor.extract(stringWithWildcard);

        executionArguments.setInputDirectory(glob.getDirectory().map(Paths::get));
        executionArguments.setInputFilterGlob(Optional.of(glob.getGlob()));

        // An empty set, means no filter check is applied
        executionArguments.setInputFilterExtensions(Optional.of(new HashSet<String>()));

        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return stringWithWildcard;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
