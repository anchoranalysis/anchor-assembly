/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExtensionUtilities {

    public static boolean hasXmlExtension(String path) {
        return FilenameUtils.getExtension(path).equalsIgnoreCase("xml");
    }

    public static boolean isFileExtension(String arg) {
        return startsWithPeriod(".") && !isFileSeperator(arg) && !isDirectoryChange(arg);
    }

    private static boolean startsWithPeriod(String arg) {
        return arg.startsWith(".");
    }

    private static boolean isDirectoryChange(String arg) {
        return arg.equals(".") || arg.equals("..");
    }

    private static boolean isFileSeperator(String arg) {
        return arg.contains("/") || arg.contains("\\");
    }
}
