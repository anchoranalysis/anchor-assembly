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
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.PrettyPathConverter;

/**
 * Uses the path directory as a manager
 *
 * @author Owen Feehan
 */
class UseDirectoryForManager implements SelectParam<Optional<Path>> {

    private final boolean input;
    private final Path directory;

    /**
     * Constructor
     *
     * @param input iff true, then we are replacing the input-manager, otherwise the output-manager
     * @param checkDirectoryExists performs a check that the directory already exists before using
     *     it as a manager
     */
    public UseDirectoryForManager(Path directory, boolean input, boolean checkDirectoryExists) {
        this.input = input;
        this.directory = directory;
        if (checkDirectoryExists && !directory.toFile().isDirectory()) {
            throw new CommandLineException(
                    String.format(
                            "The path %s to UseDirectoryForManager must be a directory",
                            directory));
        }
    }

    @Override
    public Optional<Path> select(ExecutionArguments executionArguments) {
        if (input) {
            executionArguments
                    .input()
                    .getContextParameters()
                    .assignInputDirectory(Optional.of(directory));
        } else {
            executionArguments.output().getPrefixer().assignOutputDirectory(directory);
        }
        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return PrettyPathConverter.prettyPath(directory);
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
