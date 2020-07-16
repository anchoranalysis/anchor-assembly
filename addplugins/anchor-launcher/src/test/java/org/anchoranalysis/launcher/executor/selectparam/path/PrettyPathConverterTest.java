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

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.junit.Test;

public class PrettyPathConverterTest {

    private static Path PATH_ABS_BIG = path("/a/b/c/d/e/f");
    private static Path PATH_ABS_MEDIUM = path("/a/b/c/d");
    private static Path PATH_ABS_DIFF_BIG_TO_MEDIUM = path("e/f");
    private static Path PATH_ABS_DIFF_MEDIUM_TO_BIG = path("../..");

    private static Path PATH_REL_BIG = path("../../../");
    private static Path PATH_REL_MEDIUM = path("../../");
    private static Path PATH_REL_DIFF_BIG_TO_MEDIUM = path("../");

    private static String IDENTICAL = ".";

    @Test
    public void testBiggerAbs() throws ExperimentExecutionException {
        test(PATH_ABS_BIG, PATH_ABS_MEDIUM, PATH_ABS_DIFF_BIG_TO_MEDIUM.toString());
    }

    @Test
    public void testSmallerAbs() throws ExperimentExecutionException {
        test(PATH_ABS_MEDIUM, PATH_ABS_BIG, PATH_ABS_DIFF_MEDIUM_TO_BIG.toString());
    }

    @Test
    public void testIdenticalAbs() throws ExperimentExecutionException {
        test(PATH_ABS_MEDIUM, PATH_ABS_MEDIUM, IDENTICAL);
    }

    @Test
    public void testBiggerRel() throws ExperimentExecutionException {
        test(PATH_REL_BIG, PATH_REL_MEDIUM, PATH_REL_DIFF_BIG_TO_MEDIUM.toString());
    }

    @Test
    public void testIdenticalRel() throws ExperimentExecutionException {
        test(PATH_REL_MEDIUM, PATH_REL_MEDIUM, IDENTICAL);
    }

    private static void test(Path test, Path workingDir, String expected) {
        assertEquals(expected, PrettyPathConverter.prettyPath(test, workingDir));
    }

    private static Path path(String str) {
        return Paths.get(str);
    }
}
