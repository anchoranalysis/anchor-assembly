/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.anchoranalysis.launcher.run;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Prints version information to the console
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class VersionPrinter {

    /**
     * Gets the current version of the software by reading a properties-file provided by the Maven
     * build
     *
     * <p>NOTE that this pom.proper
     *
     * @param classLoader class-loader where resource is found
     * @param versionResourcePath resource-path (with class-loader) to obtain the version of the
     *     software
     * @return string describing the current version number of anchor-launcher
     * @throws IOException if the properties file cannot be read, or is missing the appropriate
     *     version key
     */
    private static String obtainVersionFromMavenProperties(
            ClassLoader classLoader, String versionResourcePath) throws IOException {
        Properties props = new Properties();

        InputStream mavenPropertiesResource = classLoader.getResourceAsStream(versionResourcePath);

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
     *     Ignored if null
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
