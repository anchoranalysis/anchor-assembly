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
package org.anchoranalysis.launcher.executor.selectparam.path.convert;

import java.nio.file.InvalidPathException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.launcher.CommandLineException;

/**
 * An exception throw if an invalid-path is inputted as an argument.
 *
 * @author Owen Feehan
 */
public class InvalidPathArgumentException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public InvalidPathArgumentException(String message) {
        super(message);
    }

    public InvalidPathArgumentException(String argument, InvalidPathException exception) {
        super(
                String.format(
                        "A path passed as an argument is invalid.%nArgument:\t%s%nError:\t\t%s",
                        argument, exception.getMessage()));
    }

    public CommandLineException toCommandLineException() {
        throw new CommandLineException(getMessage());
    }
}
