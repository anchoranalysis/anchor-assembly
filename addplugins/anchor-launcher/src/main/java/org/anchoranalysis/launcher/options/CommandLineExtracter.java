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
package org.anchoranalysis.launcher.options;

import java.util.Optional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.function.CheckedConsumer;
import org.apache.commons.cli.CommandLine;

/**
 * Adds methods to {@link CommandLine} for querying and extracting options, with and without
 * arguments.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class CommandLineExtracter {

    /** The command-line from which options are extracted. */
    private CommandLine line;

    /**
     * Identical to {@link CommandLine#hasOption}.
     *
     * @param option short-name of option
     * @return true iff option exists on the command-line, irrespective of if arguments are present
     *     or not
     */
    public boolean hasOption(String option) {
        return line.hasOption(option);
    }

    /**
     * Executes a {@link Consumer} if an option is present.
     *
     * @param optionName the short-name of the option, which should be capable of accepting a
     *     single-argument
     * @param consumer a consumer that accepts a single argument as a string, an empty-string if no
     *     argument is provided.
     * @throws E if {@code consumer} throws this exception.
     */
    public <E extends Exception> void ifPresentSingle(
            String optionName, CheckedConsumer<String, E> consumer) throws E {
        OptionalUtilities.ifPresent(single(optionName, true), consumer);
    }

    /**
     * Extracts an option requiring a single argument.
     *
     * @param optionName the particular option (short-name)
     * @return the single-argument if the option is present
     */
    private Optional<String> single(String optionName, boolean convertNullToEmptyString) {
        if (hasOption(optionName)) {
            String element = line.getOptionValue(optionName);
            if (element != null) {
                return Optional.of(element);
            } else {
                return maybeConvertNullString(convertNullToEmptyString);
            }
        } else {
            return Optional.empty();
        }
    }

    private static Optional<String> maybeConvertNullString(boolean convertNullToEmptyString) {
        if (convertNullToEmptyString) {
            return Optional.of("");
        } else {
            return Optional.empty();
        }
    }
}
