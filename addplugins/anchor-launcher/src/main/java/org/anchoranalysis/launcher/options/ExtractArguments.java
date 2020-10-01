package org.anchoranalysis.launcher.options;

import java.util.Optional;
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
     * Extracts an option requiring a single argument.
     * 
     * @param optionName the particular option (short-name)
     * @return the single-argument if the option is present
     */
    public Optional<String> single(String optionName, boolean convertNullToEmptyString) {
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
