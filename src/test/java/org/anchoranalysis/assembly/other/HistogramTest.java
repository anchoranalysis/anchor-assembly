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
package org.anchoranalysis.assembly.other;

import java.util.*;
import org.anchoranalysis.assembly.TaskTestBase;

/** Tests the <b>copy</b> task in an Anchor distribution. */
class HistogramTest extends TaskTestBase {

    @Override
    protected String taskName() {
        return "histogram";
    }

    @Override
    protected List<String> expectedFiles() {
        return Arrays.asList(
                "blue_corner_blue.csv",
                "blue_corner_green.csv",
                "blue_corner_red.csv",
                "chur_blue.csv",
                "chur_green.csv",
                "chur_red.csv",
                "green_shelf_blue.csv",
                "green_shelf_green.csv",
                "green_shelf_red.csv",
                "tuebingen_blue.csv",
                "tuebingen_green.csv",
                "tuebingen_red.csv",
                "sum/red.csv",
                "sum/green.csv",
                "sum/blue.csv");
    }
}
