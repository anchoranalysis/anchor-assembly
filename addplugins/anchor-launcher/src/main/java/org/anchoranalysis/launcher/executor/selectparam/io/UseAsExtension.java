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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import lombok.AllArgsConstructor;

/**
 * Uses the path directory as a manager
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class UseAsExtension implements SelectParam<Optional<Path>> {

    private String[] extensions;

    @Override
    public Optional<Path> select(ExperimentExecutionArguments eea) {

        // Remove the period from the left side
        List<String> extWithoutPeriod = removeLeadingPeriod(extensions);

        eea.setInputFilterExtensions(Optional.of(new HashSet<>(extWithoutPeriod)));

        return Optional.empty();
    }

    private static List<String> removeLeadingPeriod(String[] exts) {
        return Arrays.stream(exts).map(s -> s.substring(1)).collect(Collectors.toList());
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.join(", ", extensions);
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
