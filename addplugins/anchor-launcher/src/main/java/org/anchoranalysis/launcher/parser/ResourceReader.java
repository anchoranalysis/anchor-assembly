/* (C)2020 */
package org.anchoranalysis.launcher.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

class ResourceReader {

    private ResourceReader() {
        // Only accessible through static methods
    }

    /**
     * Reads a string from a resource, or displays an error message
     *
     * @param resourceFileName the file-name to identify the resource (in the root directory)
     * @param cl class-loader where resource is found
     */
    public static String readStringFromResource(String resourceFileName, ClassLoader cl)
            throws IOException {
        InputStream helpDisplayResource = cl.getResourceAsStream(resourceFileName);
        if (helpDisplayResource != null) {
            return IOUtils.toString(helpDisplayResource, StandardCharsets.UTF_8);
        } else {
            return resourceFileName + " is missing, so cannot display.";
        }
    }
}
