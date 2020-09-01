package org.anchoranalysis.launcher.executor.selectparam.path;

import java.nio.file.InvalidPathException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.launcher.CommandLineException;

/**
 * An exception throw if an invalid-path is inputted as an argument.
 * 
 * @author Owen Feehan
 *
 */
public class InvalidPathArgumentException extends AnchorFriendlyCheckedException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InvalidPathArgumentException(String argument, InvalidPathException exception) {
        super( String.format("A path passed as an argument is invalid.%nArgument:\t%s%nError:\t\t%s", argument, exception.getMessage()) );
    }
    
    public CommandLineException toCommandLineException() {
        throw new CommandLineException( getMessage() );
    }
}
