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

package org.anchoranalysis.launcher;

import java.nio.file.Path;

public class FilePathResolver {

    private Class<?> classInExecutingLoader;

    public FilePathResolver(Class<?> classInExecutingLoader) {
        super();
        this.classInExecutingLoader = classInExecutingLoader;
    }

    public Path resolvePathToCurrentJar(Path pathRelative) {
        return basePath().resolve(pathRelative);
    }

    public Path resolvePathToCurrentJar(String pathRelative) {
        return basePath().resolve(pathRelative);
    }

    private Path basePath() {
        return PathCurrentJarUtilities.pathCurrentJAR(classInExecutingLoader);
    }
}
