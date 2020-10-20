package org.anchoranalysis.launcher;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/** An exception thrown at run-time while processing command-line args */
public class CommandLineException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public CommandLineException(String message) {
        super(message);
    }
}
