/* (C)2020 */
package org.anchoranalysis.launcher.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Prints version information to the console
 *
 * @author Owen Feehan
 */
class VersionPrinter {

    private VersionPrinter() {
        // Only accessible through static methods
    }

    /**
     * Gets the current version of the software by reading a properties-file provided by the Maven
     * build
     *
     * <p>NOTE that this pom.proper
     *
     * @param cl class-loader where resource is found
     * @param versionResourcePath resource-path (with class-loader) to obtain the version of the
     *     software
     * @return string describing the current version number of anchor-launcher
     * @throws IOException if the properties file cannot be read, or is missing the appropriate
     *     version key
     */
    private static String obtainVersionFromMavenProperties(
            ClassLoader cl, String versionResourcePath) throws IOException {
        Properties props = new Properties();

        InputStream mavenPropertiesResource = cl.getResourceAsStream(versionResourcePath);

        if (mavenPropertiesResource == null) {
            return "<unknown>";
        }

        try (InputStream resourceStream = mavenPropertiesResource) {
            props.load(resourceStream);
        }

        if (!props.containsKey("version")) {
            throw new IOException("version property is missing from maven properties");
        }

        return props.getProperty("version");
    }

    /**
     * Describes which version of anchor is being used, and what version number
     *
     * @param cl class-loader where resource is found
     * @param footerResourcePath resource-path (with class-loader) to a displayed footer message.
     *     Ignored if NULL
     * @param versionResourcePath resource-path (with class-loader) to obtain the version of the
     *     software
     * @throws IOException if it's not possible to determine the version number
     */
    public static void printVersion(
            ClassLoader cl, String footerResourcePath, String versionResourcePath)
            throws IOException {
        System.out.printf(
                "anchor version %s by Owen Feehan (ETH Zurich, University of Zurich, 2016)%n",
                obtainVersionFromMavenProperties(cl, versionResourcePath));
        if (footerResourcePath != null) {
            System.out.println();
            String footer = ResourceReader.readStringFromResource(footerResourcePath, cl);
            System.out.print(footer);
        }
    }
}
