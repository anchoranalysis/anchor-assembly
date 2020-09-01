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

package org.anchoranalysis.launcher.executor.selectparam.path;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrettyPathConverter {

    // If we have more than this in a relative-path, we show absolute paths instead
    private static final int MAX_DOUBLE_DOTS_CNT = 3;

    /**
     * Converts to either a normalized absolute-path or relative-path depending on which is prettier
     * to the user
     */
    public static String prettyPath(String path) {
        return prettyPath(Paths.get(path));
    }

    /**
     * Converts to either a normalized absolute-path or relative-path depending on which is prettier
     * to the user
     */
    public static String prettyPath(Path path) {
        Path workingDir = completelyNormalize(Paths.get(""));
        return prettyPath(completelyNormalize(path), completelyNormalize(workingDir));
    }

    static String prettyPath(Path path, Path workingDir) {

        // First we make both paths absolute and normalized, so they have non-null roots
        path = path.toAbsolutePath().normalize();
        workingDir = workingDir.toAbsolutePath().normalize();

        if (workingDir.equals(path)) {
            // Special case to handle when directories are equal, as for some reason the Java
            //   relativize command returns .. rather than .
            return ".";
        }

        if (workingDir.getRoot().equals(path.getRoot())) {
            // If on the same root, then find a relative path between them

            Path relativePath = workingDir.relativize(path).normalize();

            // Depending on the number of dots in the relative path, we show
            //   either the absolute-path or the relative-path.
            if (countDoubleDotsInRelativePath(relativePath) > MAX_DOUBLE_DOTS_CNT) {
                return path.toString();
            } else {
                return relativePath.toString();
            }

        } else {
            // If on different roots
            return path.toAbsolutePath().normalize().toString();
        }
    }

    static Path completelyNormalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    /** Counts how many times ../ appears in a relative-path */
    private static int countDoubleDotsInRelativePath(Path path) {

        int cnt = 0;

        for (Path p : path) {
            if (p.getFileName().toString().equals("..")) {
                cnt++;
            }
        }

        return cnt;
    }
}
