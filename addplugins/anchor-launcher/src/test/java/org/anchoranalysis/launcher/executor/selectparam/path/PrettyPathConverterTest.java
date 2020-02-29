package org.anchoranalysis.launcher.executor.selectparam.path;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.path.PrettyPathConverter;
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
		test(PATH_ABS_BIG, PATH_ABS_MEDIUM, PATH_ABS_DIFF_BIG_TO_MEDIUM.toString() ); 
	}
	
	@Test
	public void testSmallerAbs() throws ExperimentExecutionException {
		test(PATH_ABS_MEDIUM, PATH_ABS_BIG, PATH_ABS_DIFF_MEDIUM_TO_BIG.toString() ); 
	}
	
	@Test
	public void testIdenticalAbs() throws ExperimentExecutionException {
		test(PATH_ABS_MEDIUM, PATH_ABS_MEDIUM, IDENTICAL); 
	}
	
	@Test
	public void testBiggerRel() throws ExperimentExecutionException {
		test(PATH_REL_BIG, PATH_REL_MEDIUM, PATH_REL_DIFF_BIG_TO_MEDIUM.toString() ); 
	}
	
	@Test
	public void testIdenticalRel() throws ExperimentExecutionException {
		test(PATH_REL_MEDIUM, PATH_REL_MEDIUM, IDENTICAL ); 
	}
	
	private static void test( Path test, Path workingDir, String expected ) {
		assertEquals(
			expected,
			PrettyPathConverter.prettyPath(test, workingDir)
		);
	}
	
	private static Path path( String str ) {
		return Paths.get(str);
	}
}
