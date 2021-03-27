package org.anchoranalysis.launcher.options.process;

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
                CommandLineOptions.SHORT_OPTION_INPUT_RELATIVE,
                InputArguments::assignRelativeForIdentifier);
    }
}
