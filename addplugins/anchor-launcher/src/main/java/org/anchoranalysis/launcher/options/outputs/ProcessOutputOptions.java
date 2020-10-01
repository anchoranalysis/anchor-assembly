package org.anchoranalysis.launcher.options.outputs;

import java.util.function.Consumer;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import lombok.AllArgsConstructor;

/**
 * Processes command-line options relating to additional outputs.
 * 
 * @author Owen Feehan
 */
@AllArgsConstructor public class ProcessOutputOptions {
    
    /** Extracts options/arguments from the command-line. */
    private final CommandLineExtracter extract;
    
    /** The arguments associated with the experiment */
    private final ExperimentExecutionArguments arguments;
    
    /**
     * Processes any options that have been defined to add/remove change the outputs that are enabled.
     * 
     * @throws ExperimentExecutionException if the arguments to the command-line options do not correspond to expectations.
     */
    public void maybeAddOutputs() throws ExperimentExecutionException {
        if (extract.hasOption(CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ALL)) {
            arguments.getOutputEnabledDelta().enableAdditionalOutputs( Permissive.INSTANCE );
        } else {
            ifAdditionalOptionsPresent(CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL, arguments.getOutputEnabledDelta()::enableAdditionalOutputs);
        }
        
        ifAdditionalOptionsPresent(CommandLineOptions.SHORT_OPTION_OUTPUT_DISABLE_ADDITIONAL, arguments.getOutputEnabledDelta()::disableAdditionalOutputs);
    }
    
    private void ifAdditionalOptionsPresent(String optionName, Consumer<MultiLevelOutputEnabled> consumer) throws ExperimentExecutionException {
        extract.ifPresentSingle(optionName, outputs ->
            consumer.accept(AdditionalOutputsParser.parseFrom(outputs, optionName) ));        
    }

}