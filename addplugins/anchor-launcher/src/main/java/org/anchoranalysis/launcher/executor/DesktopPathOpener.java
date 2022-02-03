package org.anchoranalysis.launcher.executor;

import com.google.common.base.Preconditions;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.error.ErrorReporter;

/**
 * Opens a directory path in the desktop.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DesktopPathOpener {

    /**
     * Opens a directory path in the desktop.
     *
     * @param the directory path to open.
     * @param errorReporter how to record any error that may occur.
     */
    public static void openPathInDesktop(Path path, ErrorReporter errorReporter) {
        Preconditions.checkArgument(path.toFile().isDirectory());
        try {
            // Some experiments have an output-directory but never create anything in it.
            // We do not want to open these directories, so we check first if the directory exists.
            if (path.toFile().exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(path.toFile());
            }
        } catch (UnsupportedOperationException e) {
            // Ignore as irrelevant
        } catch (IOException e) {
            errorReporter.recordError(
                    DesktopPathOpener.class,
                    "Failed to open output-directory in desktop, as requested:",
                    e);
        }
    }
}
