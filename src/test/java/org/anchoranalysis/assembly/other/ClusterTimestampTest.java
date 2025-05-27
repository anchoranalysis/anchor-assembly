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

/** Tests the <b>cluster/timestamp</b> task in an Anchor distribution. */
class ClusterTimestampTest extends TaskTestBase {

    @Override
    protected String taskName() {
        return "cluster/timestamp";
    }

    @Override
    protected List<String> expectedFiles() {
        return Arrays.asList(
                "outliers/blue_corner.png",
                "outliers/chur.jpg",
                "outliers/green_shelf.jpg",
                "outliers/tuebingen.JPG");
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
