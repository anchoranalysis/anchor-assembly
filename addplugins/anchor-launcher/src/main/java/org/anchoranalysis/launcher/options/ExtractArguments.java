package org.anchoranalysis.launcher.options;

import java.util.Optional;
import java.util.function.Consumer;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.function.CheckedConsumer;
import org.apache.commons.cli.CommandLine;
import lombok.AllArgsConstructor;

/**
 * Extracts options with one or more arguments as {@link Optional}.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class ExtractArguments {
    
    /** The command-line from which options are extracted. */
    private CommandLine line;
    
    /**
     * Executes a {@link Consumer} if an option is present.
     * 
     * @param optionName the short-name of the option, which should be capable of accepting a single-argument
     * @param consumer a consumer that accepts a single argument as a string, an empty-string if no argument is provided.
     * @throws E if {@code consumer} throws this exception.
     */
    public <E extends Exception>void ifPresentSingle( String optionName, CheckedConsumer<String,E> consumer ) throws E {
        OptionalUtilities.ifPresent( single(optionName, true), consumer);
    }
    
    /**
     * Extracts an option requiring a single argument.
     * 
     * @param optionName the particular option (short-name)
     * @return the single-argument if the option is present
     */
    private Optional<String> single(String optionName, boolean convertNullToEmptyString) {
        if (line.hasOption(optionName)) {
            String element = line.getOptionValue(optionName);
            if (element!=null) {
                return Optional.of(element);
            } else {
                return maybeConvertNullString(convertNullToEmptyString);
            }
        } else {
            return Optional.empty();
        }
    }
    
    private static Optional<String> maybeConvertNullString(boolean convertNullToEmptyString) {
        if (convertNullToEmptyString) {
            return Optional.of("");
        } else {
            return Optional.empty();
        }
    }
}
