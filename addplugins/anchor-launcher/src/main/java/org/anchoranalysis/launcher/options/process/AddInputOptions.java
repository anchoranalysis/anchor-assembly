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

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegativeFactory;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.InputArguments;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import org.anchoranalysis.launcher.options.CommandLineOptions;

/**
 * Adds options relating to inputting from the command-line.
 *
 * @author Owen Feehan
 */
public class AddInputOptions extends AddOptionsFromCommandLine<InputArguments> {

    private AddInputOptions(CommandLineExtracter extract, InputArguments arguments) {
        super(extract, arguments);
    }

    /**
     * Adds options to change the inputs from the command-line.
     *
     * @throws ExperimentExecutionException if the arguments to the command-line options do not
     *     correspond to expectations.
     */
    public static void addFrom(CommandLineExtracter extract, InputArguments arguments)
            throws ExperimentExecutionException {
        new AddInputOptions(extract, arguments).addOptionsFromCommandLine();
    }

    @Override
    public void addOptionsFromCommandLine() throws ExperimentExecutionException {

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_INPUT_COPY_NON_INPUTS,
                InputArguments::assignCopyNonInputs);

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_INPUT_RELATIVE,
                InputArguments::assignRelativeForIdentifier);

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_INPUT_SHUFFLE, InputArguments::assignShuffle);

        ifPresentSingleAssociated(
                CommandLineOptions.SHORT_OPTION_INPUT_SUBSET_IDENTIFIER,
                AddInputOptions::assignIdentifierSubrange);
    }

    private static void assignIdentifierSubrange(InputArguments arguments, String parameter)
            throws ExperimentExecutionException {
        try {
            arguments.assignIdentifierSubrange(IndexRangeNegativeFactory.parse(parameter));
        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException("Cannot set parameter for subsetting names.", e);
        }
    }
}
