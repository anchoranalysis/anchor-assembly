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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.format.FileFormatFactory;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.OutputArguments;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.recorded.OutputEnabledDelta;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import org.anchoranalysis.launcher.options.CommandLineOptions;

/**
 * Processes command-line options relating to additional outputs.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ProcessOutputOptions {

    /** Extracts options/arguments from the command-line. */
    private final CommandLineExtracter extract;

    /** The arguments associated with the experiment */
    private final OutputArguments arguments;

    /**
     * Processes any options that have been defined to add/remove change the outputs that are
     * enabled.
     *
     * @throws ExperimentExecutionException if the arguments to the command-line options do not
     *     correspond to expectations.
     */
    public void maybeAddOutputs() throws ExperimentExecutionException {
        if (!ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ALL,
                outputArguments ->
                        outputArguments
                                .getOutputEnabledDelta()
                                .enableAdditionalOutputs(Permissive.INSTANCE))) {
            ifAdditionalOptionsPresent(
                    CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL,
                    OutputEnabledDelta::enableAdditionalOutputs);
        }

        ifAdditionalOptionsPresent(
                CommandLineOptions.SHORT_OPTION_OUTPUT_DISABLE_ADDITIONAL,
                OutputEnabledDelta::disableAdditionalOutputs);

        ifOutputFormatPresent(
                CommandLineOptions.SHORT_OPTION_OUTPUT_IMAGE_FILE_FORMAT,
                OutputArguments::assignSuggestedImageOutputFormat);

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_OUTPUT_INCREMENTING_NUMBER,
                OutputArguments::requestOutputIncrementingNumberSequence);
    }

    private void ifAdditionalOptionsPresent(
            String optionName, BiConsumer<OutputEnabledDelta, MultiLevelOutputEnabled> function)
            throws ExperimentExecutionException {
        extract.ifPresentSingle(
                optionName,
                outputs ->
                        function.accept(
                                arguments.getOutputEnabledDelta(),
                                AdditionalOutputsParser.parseFrom(outputs, optionName)));
    }

    private void ifOutputFormatPresent(
            String optionName, BiConsumer<OutputArguments, ImageFileFormat> consumer)
            throws ExperimentExecutionException {
        extract.ifPresentSingle(
                optionName,
                identifier -> {
                    ImageFileFormat format =
                            FileFormatFactory.createImageFormat(identifier)
                                    .orElseThrow(
                                            () ->
                                                    new ExperimentExecutionException(
                                                            String.format(
                                                                    "No file format identified by %s is supported.",
                                                                    identifier)));
                    consumer.accept(arguments, format);
                });
    }

    private boolean ifOptionWithoutArgument(
            String optionShort, Consumer<OutputArguments> consumer) {
        if (extract.hasOption(optionShort)) {
            consumer.accept(arguments);
            return true;
        } else {
            return false;
        }
    }
}
