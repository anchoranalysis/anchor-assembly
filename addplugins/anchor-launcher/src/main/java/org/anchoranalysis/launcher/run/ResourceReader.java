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
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class ResourceReader {

    /**
     * Reads a string from a resource, or displays an error message
     *
     * @param resourceFileName the file-name to identify the resource (in the root directory)
     * @param classLoader class-loader where resource is found
     */
    public static String readStringFromResource(String resourceFileName, ClassLoader classLoader)
            throws IOException {
        InputStream helpDisplayResource = classLoader.getResourceAsStream(resourceFileName);
        if (helpDisplayResource != null) {
            return IOUtils.toString(helpDisplayResource, StandardCharsets.UTF_8);
        } else {
            return resourceFileName + " is missing, so cannot display.";
        }
    }
}
