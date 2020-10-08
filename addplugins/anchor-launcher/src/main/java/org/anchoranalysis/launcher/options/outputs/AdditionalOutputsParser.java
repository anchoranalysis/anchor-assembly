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
package org.anchoranalysis.launcher.options.outputs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;

/**
 * Creates a {@link OutputEnabledMutable} with output-names to use in addition to defaults.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class AdditionalOutputsParser {

    /**
     * What element separates the first-level output from the second-level output.
     *
     * <p>Note this will also be interpreted as a regular-expression in a split.
     */
    private static final String ELEMENT_SEPERATOR = ":";

    /**
     * Creates a {@link OutputEnabledMutable} from a user-supplied string.
     *
     * <p>The format of the string is one or more comma-separated elements.
     *
     * <p>If the element string contains no colon, then it is interpreted as a first-level
     * output-name to enable.
     *
     * <p>If the element string contains a colon in the format {@code prefix:suffix}, {@code suffix}
     * is interpreted as a second-level output-name to enable, for a corresponding first-level
     * output of {@code prefix}.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>{@code "csv"}
     *   <li>{@code "csv, outline"}
     *   <li>{@code "outline,stacks:background"}
     *   <li>{@code "stacks:foreground"}
     * </ul>
     *
     * @param optionsArgument a string in the format
     * @param optionName the name of the option the string was inputted in (for error messages)
     * @return the additional outputs
     * @throws ExperimentExecutionException if the argument doesn't correspond to the expected
     *     format.
     */
    public static OutputEnabledMutable parseFrom(String optionsArgument, String optionName)
            throws ExperimentExecutionException {

        OutputEnabledMutable outputs = new OutputEnabledMutable();

        if (optionsArgument.isEmpty()) {
            throw new ExperimentExecutionException(
                    String.format("The -%s option requires an argument.", optionName));
        }

        for (String element : optionsArgument.split(",")) {
            addElement(outputs, element);
        }
        return outputs;
    }

    private static final String EXCEPTION_MESSAGE_SNIPPET =
            "It must be in the format of either outputName or firstLevelOutputName:secondLevelOutputName.";

    private static void addElement(OutputEnabledMutable outputs, String element)
            throws ExperimentExecutionException {
        if (element.contains(ELEMENT_SEPERATOR)) {
            addSecondLevelElement(outputs, element);
        } else {
            outputs.addEnabledOutputFirst(element);
        }
    }

    private static void addSecondLevelElement(OutputEnabledMutable outputs, String element)
            throws ExperimentExecutionException {
        if (element.startsWith(ELEMENT_SEPERATOR) || element.endsWith(ELEMENT_SEPERATOR)) {
            throw createException("An output-name may not start or end with a colon.", element);
        }

        String[] splits = element.split(ELEMENT_SEPERATOR);

        if (splits.length != 2) {
            throw createException("Invalid output-name.", element);
        }

        outputs.addEnabledOutputSecond(splits[0], splits[1]);
    }

    private static ExperimentExecutionException createException(String prefix, String element) {
        return new ExperimentExecutionException(
                String.format("%s%n%s%n%s", prefix, EXCEPTION_MESSAGE_SNIPPET, element));
    }
}
