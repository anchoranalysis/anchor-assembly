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

package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.experiment.ExperimentFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.InputFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.OutputFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.TaskFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.apache.commons.cli.CommandLine;

/**
 * Creates an appropriate {@link SelectParam} based upon the options passed to the command-line.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectParamFactory {

    /**
     * Use default manager.
     *
     * @return a manager that uses the default path.
     */
    public static SelectParam<Optional<Path>> useDefault() {
        return new UseDefaultManager();
    }

    /**
     * Can point to either a path to beanXML
     *
     * @param line command-line to consider if certain options have been selected or not
     * @param optionName which option we consider
     * @param tasksDirectory path to the "tasks" directory in the anchor configuration files
     * @return an appropriate SelectParam object
     */
    public static SelectParam<Optional<Path>> pathOrTaskNameOrDefault(
            CommandLine line, String optionName, Path tasksDirectory) {
        return ifOption(line, optionName, args -> TaskFactory.pathOrTaskName(args, tasksDirectory));
    }

    /**
     * Can point to either:
     *
     * <ol>
     *   <li>a path ending in <i>.xml</i>, assumed to BeanXML for an input manager
     *   <li>a directory, set as an the inputDirectory in the input-context
     *   <li>a string with a wild-card, assumed to be a glob, set into the input-context as a glob
     *   <li>a string with a period and without any forward or backwards slashes, set into the
     *       input-context as an extension to match
     * </ol>
     *
     * @param line command-line to consider if certain options have been selected or not
     * @return an appropriate SelectParam object
     */
    public static SelectParam<Optional<Path>> inputSelectParam(CommandLine line) {
        try {
            return ifOption(
                    line,
                    CommandLineOptions.SHORT_OPTION_INPUT,
                    InputFactory::pathOrDirectoryOrGlobOrExtension);
        } catch (InvalidPathArgumentException e) {
            throw e.toCommandLineException();
        }
    }

    /**
     * Can point to either:
     *
     * <ol>
     *   <li>a path ending in <i>.xml</i>, assumed to BeanXML for an output manager
     *   <li>a directory, set as the outputDirectory in the input-context
     * </ol>
     *
     * @param line command-line to consider if certain options have been selected or not
     * @return an appropriate SelectParam object
     */
    public static SelectParam<Optional<Path>> outputSelectParam(CommandLine line) {
        boolean writeIntoRoot =
                line.hasOption(CommandLineOptions.SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER);
        return ifOption(
                line,
                CommandLineOptions.SHORT_OPTION_OUTPUT,
                arg -> OutputFactory.pathOrDirectory(arg, writeIntoRoot));
    }

    /**
     * Can point to either:
     *
     * <ol>
     *   <li>a path ending in <i>.xml</i>, assumed to BeanXML for an experiment
     *   <li>nothing, then default experiment is used
     * </ol>
     *
     * @param line command-line to consider if certain options have been selected or not
     * @param defaultExperiment path to the default experiment
     * @return an appropriate SelectParam object
     * @throws ExperimentExecutionException
     */
    public static SelectParam<Path> experimentSelectParam(CommandLine line, Path defaultExperiment)
            throws ExperimentExecutionException {
        return ExperimentFactory.defaultExperimentOrCustom(line, defaultExperiment);
    }

    private static <E extends Exception> SelectParam<Optional<Path>> ifOption(
            CommandLine line,
            String optionName,
            CheckedFunction<String[], SelectParam<Optional<Path>>, E> func)
            throws E {
        if (line.hasOption(optionName)) {
            return func.apply(line.getOptionValues(optionName));

        } else {
            return new UseDefaultManager();
        }
    }
}
