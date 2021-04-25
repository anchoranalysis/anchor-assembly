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
