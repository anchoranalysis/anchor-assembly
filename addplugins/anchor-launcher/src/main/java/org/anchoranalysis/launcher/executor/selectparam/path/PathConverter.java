/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.path;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter {

    private PathConverter() {}

    public static Path pathFromArg(String arg) {
        return Paths.get(arg).toAbsolutePath();
    }
}
