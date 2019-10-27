package org.anchoranalysis.launcher.executor.selectparam;

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
