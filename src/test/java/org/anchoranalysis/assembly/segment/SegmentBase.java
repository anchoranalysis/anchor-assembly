/*-
 * #%L
 * Anchor Distribution
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.assembly.segment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.anchoranalysis.assembly.TaskTestBase;

/** A base for testing a task that segments an image. */
abstract class SegmentBase extends TaskTestBase {

    @Override
    protected List<String> expectedFiles() {
        List<String> nonThumbnails =
                Arrays.asList(
                        "summary.csv",
                        "blue_corner_mask.png",
                        "blue_corner_outline.png",
                        "chur_mask.png",
                        "chur_outline.png",
                        "green_shelf_mask.png",
                        "green_shelf_outline.png",
                        "tuebingen_mask.png",
                        "tuebingen_outline.png");
        Stream<String> thumbnails = thumbnailFiles(maxIndexThumbnails());
        return Stream.concat(nonThumbnails.stream(), thumbnails).collect(Collectors.toList());
    }

    @Override
    protected boolean consoleOnly() {
        return false;
    }

    /**
     * The maximum index of the thumbnails created by the segmentation.
     *
     * @return the maximum-index.
     */
    protected abstract int maxIndexThumbnails();

    /**
     * Creates a stream of file-names in the thumbnails directory, in the form {@code
     * thumbnails/thiumbnails_000000.png}.
     *
     * <p>The latter number is replaced with a index that begins at 0 and ends at {@code maxIndex}.
     *
     * @param maxIndex the maximum index that is created.
     * @return the stream of filenames
     */
    private static Stream<String> thumbnailFiles(int maxIndex) {
        return IntStream.rangeClosed(0, maxIndex)
                .mapToObj(i -> String.format("thumbnails/thumbnails_%06d.png", i));
    }
}
