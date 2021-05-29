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

    /** The arguments associated with the experiment */
    protected final T arguments;

    /** Maybe add options to the arguments from the command-line. */
    public abstract void addOptionsFromCommandLine() throws ExperimentExecutionException;

    protected boolean ifOptionWithoutArgument(String optionShort, Consumer<T> consumer) {
        if (extract.hasOption(optionShort)) {
            consumer.accept(arguments);
            return true;
        } else {
            return false;
        }
    }

    protected <E extends Exception> void ifPresentSingle(
            String optionName, CheckedConsumer<String, E> consumer) throws E {
        extract.ifPresentSingle(optionName, consumer);
    }
}
