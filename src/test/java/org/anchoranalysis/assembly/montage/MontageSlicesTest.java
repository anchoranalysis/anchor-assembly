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
package org.anchoranalysis.assembly.montage;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.assembly.TaskTestBase;

/** Tests the <b>montage/slices</b> task in an Anchor distribution. */
class MontageSlicesTest extends TaskTestBase {

    @Override
    protected String taskName() {
        return "montage/slices";
    }

    @Override
    protected List<String> expectedFiles() {
        return Arrays.asList(
                "blue_corner_input_image.tif",
                "blue_corner_montage.tif",
                "chur_input_image.tif",
                "chur_montage.tif",
                "green_shelf_input_image.tif",
                "green_shelf_montage.tif",
                "tuebingen_input_image.tif",
                "tuebingen_montage.tif");
    }

    @Override
    protected boolean consoleOnly() {
        return false;
    }

    @Override
    protected boolean identicalSizes() {
        return false;
    }
}
