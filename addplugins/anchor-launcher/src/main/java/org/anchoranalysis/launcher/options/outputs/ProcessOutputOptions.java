package org.anchoranalysis.launcher.options.outputs;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Processes command-line options relating to additional outputs.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE) public class ProcessOutputOptions {
    
    /**
     * Processes any options that have been defined to add/remove change the outputs that are enabled.
     * 
     * @param extract extracts options/arguments from the command-line
     * @param arguments the arguments associated with the experiment
     * @throws ExperimentExecutionException if the arguments to the command-line options do not correspond to expectations.
     */
    public static void maybeAddOutputs(CommandLineExtracter extract, ExperimentExecutionArguments arguments) throws ExperimentExecutionException {
        if (extract.hasOption(CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ALL)) {
            arguments.assignAdditionalOutputs( Permissive.INSTANCE );
        } else {
            extract.ifPresentSingle(CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL, outputs ->
                addAdditionalOutputs(arguments, outputs) );
        }            
    }
    
    private static void addAdditionalOutputs(ExperimentExecutionArguments arguments, String outputsCommaSeparated) throws ExperimentExecutionException {
        arguments.assignAdditionalOutputs( AdditionalOutputsParser.parseFrom(outputsCommaSeparated, CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL));
    }
}