/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.launcher.options.process;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;
import org.anchoranalysis.core.functional.checked.CheckedConsumer;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.options.CommandLineExtracter;

/**
 * Base class for adding options from command-line arguments.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class AddOptionsFromCommandLine<T> {

    /** Extracts options/arguments from the command-line. */
    private final CommandLineExtracter extract;

    /** An associated entity which consumers accept. */
    protected final T associated;

    /** Maybe add options to the arguments from the command-line. */
    public abstract void addOptionsFromCommandLine() throws ExperimentExecutionException;

    /**
     * Executes <code>consumer</code> if an option exists <b>without any argument</b>.
     *
     * @param optionShort name of the option in short form
     * @param consumer called with the associated element, if the option is present.
     * @return true if the option is present, false otherwise
     */
    protected boolean ifOptionWithoutArgument(String optionShort, Consumer<T> consumer) {
        if (extract.hasOption(optionShort)) {
            consumer.accept(associated);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Executes <code>consumer</code> if an option exists <b>with a single argument</b>.
     *
     * @param <E> exception-type that {@code consumer} may throw.
     * @param optionShort name of the option in short form
     * @param consumer called with the associated object and the extracted single-argument, if the
     *     option is present.
     * @throws E if {@code consumer} throws it.
     */
    protected <E extends Exception> void ifOptionWithSingleArgument(
            String optionShort, CheckedBiConsumer<T, String, E> consumer) throws E {
        extract.ifPresentSingle(optionShort, value -> consumer.accept(associated, value));
    }

    protected <E extends Exception> void ifPresentSingle(
            String optionName, CheckedConsumer<String, E> consumer) throws E {
        extract.ifPresentSingle(optionName, consumer);
    }
}
