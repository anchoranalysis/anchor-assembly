/* (C)2020 */
package org.anchoranalysis.launcher.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/**
 * Displays some common error messages to the user
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ErrorPrinter {

    /**
     * How long the indentation+message can be before wrapping in the error log. -1 disables
     * wrapping
     */
    private static final int ERROR_LOG_WRAP_MESSAGE = 200;

    public static void printTooManyArguments() {
        System.err.println( // NOSONAR
                "Please only pass a single experiment-file as an argument. Multiple files are not allowed");
    }

    /**
     * Prints an exception to the file-system as an error log
     *
     * @param cause the exception that occurred to cause the error
     */
    public static void printErrorLog(ExperimentExecutionException cause, Path errorLogPath) {
        try {
            // Let's also store the error in a file, along with a stack trace
            FileWriter writer = new FileWriter(errorLogPath.toFile());
            cause.friendlyMessageHierarchy(writer, ERROR_LOG_WRAP_MESSAGE, true);
            writer.write(System.lineSeparator());
            writer.write(System.lineSeparator());
            writer.write("STACK TRACE:");
            writer.write(System.lineSeparator());
            cause.printStackTrace(new PrintWriter(writer));
            writer.close();
        } catch (IOException exc) {
            System.err.printf("Cannot write the error log due to an I/O error%n");
            System.err.println(exc.toString());
        }
    }
}
