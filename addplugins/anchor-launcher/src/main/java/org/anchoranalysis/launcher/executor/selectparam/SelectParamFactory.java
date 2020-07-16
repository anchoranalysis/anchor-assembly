/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.experiment.ExperimentFactory;
import org.anchoranalysis.launcher.executor.selectparam.io.InputFactory;
import org.anchoranalysis.launcher.executor.selectparam.io.OutputFactory;
import org.anchoranalysis.launcher.executor.selectparam.task.TaskFactory;
import org.apache.commons.cli.CommandLine;

/**
 * Creates an appropriate SelectParam based upon the options passed to the command-line
 *
 * @author Owen Feehan
 */
public class SelectParamFactory {

    private SelectParamFactory() {}

    /**
     * Default option
     *
     * @return
     */
    public static SelectParam<Optional<Path>> useDefault() {
        return new UseDefaultManager();
    }

    /**
     * Can point to either a path to beanXML
     *
     * @param line command-line to consider if certain options have been selected or not
     * @param optionName which option we consider
     * @param configDir path to the configuration-directory of anchor
     * @return an appropriate SelectParam object
     */
    public static SelectParam<Optional<Path>> pathOrTaskNameOrDefault(
            CommandLine line, String optionName, Path configDir) {
        return ifOption(line, optionName, args -> TaskFactory.pathOrTaskName(args, configDir));
    }

    /**
     * Can point to either:
     *
     * <ol>
     *   <li>a path ending in .xml -> assumed to BeanXML for an input manager
     *   <li>a directory -> set as an the inputDirectory in the input-context
     *   <li>a string with a wild-card, assumed to be a glob, set into the input-context as a glob
     *   <li>a string with a period, and without any forward or backwards slashes -> set into the
     *       input-context as an extension to match
     * </ol>
     *
     * @param line command-line to consider if certain options have been selected or not
     * @param optionName which option we consider
     * @return an appropriate SelectParam object
     */
    public static SelectParam<Optional<Path>> inputSelectParam(
            CommandLine line, String optionName) {
        return ifOption(line, optionName, InputFactory::pathOrDirectoryOrGlobOrExtension);
    }

    /**
     * Can point to either:
     *
     * <ol>
     *   <li>a path ending in .xml -> assumed to BeanXML for an output manager
     *   <li>a directory -> set as the outputDirectory in the input-context
     * </ol>
     *
     * @param line command-line to consider if certain options have been selected or not
     * @param optionName which option we consider
     * @return an appropriate SelectParam object
     */
    public static SelectParam<Optional<Path>> outputSelectParam(
            CommandLine line, String optionName) {
        return ifOption(line, optionName, arg -> OutputFactory.pathOrDirectory(arg, false));
    }

    /**
     * Can point to either:
     *
     * <ol>
     *   <li>a path ending in .xml -> assumed to BeanXML for an experiment
     *   <li>nothing -> then default experiment is used
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

    private static SelectParam<Optional<Path>> ifOption(
            CommandLine line,
            String optionName,
            Function<String[], SelectParam<Optional<Path>>> func) {
        if (line.hasOption(optionName)) {
            return func.apply(line.getOptionValues(optionName));

        } else {
            return new UseDefaultManager();
        }
    }
}
