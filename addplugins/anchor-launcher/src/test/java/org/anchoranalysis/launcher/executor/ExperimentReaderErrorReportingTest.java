package org.anchoranalysis.launcher.executor;

/*
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.test.TestLoader;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the different kind of error messages produced by configuration-files that have deliberate errors
 * 
 * When debugging, a line System.out.println(output) can be inserted into a test, so that the error message is
 *  outputted to the console.
 * 
 * @author Owen Feehan
 *
 */
public class ExperimentReaderErrorReportingTest {

	private static TestLoader tl = TestLoader.createFromMavenWorkingDir();
		
	@BeforeClass
    public static void setUp() throws Exception {
        ExperimentExecutorObj.initializeIfNecessary(tl.getRoot(), false, false );
    }
	
	@Test(expected=ExperimentExecutionException.class)
	public void testNonExistentFilePath() throws ExperimentExecutionException {
		readExperiment(
			"a non-existent config-name",
			1
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectEndTag() throws ExperimentExecutionException {
		readExperiment(
			"incorrectEndTag",
			3
		);
	}
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testMissingRootTag() throws ExperimentExecutionException {
		readExperiment(
			"missingRootTag",
			2,
			"experiment"
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectClass() throws ExperimentExecutionException {
		readExperiment(
			"incorrectClass",
			8,
			new String[] {
				"Please check spelling of config-class attributes",
				"ch.ethz.biol.cell.imageprocessing.io.task.ANonExistentClass"
			}
		);
	}
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectFactory() throws ExperimentExecutionException {
		readExperiment(
			"incorrectFactory",
			7,
			new String[] {
				"Unknown bean factory",
				"nonExistentFactory"
			}
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectClassNested() throws ExperimentExecutionException {
		readExperiment(
			"incorrectClassNested",
			12,
			new String[] {
				"Please check spelling of config-class attributes",
				"ch.ethz.biol.cell.imageprocessing.chnl.provider.SomeNoneExistentClass"
			}
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectIncludeFile() throws ExperimentExecutionException {
		readExperiment(
			"incorrectIncludeFile",
			9,
			"Cannot find included file"
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectIncludeFileNested() throws ExperimentExecutionException {
		readExperiment(
			"incorrectIncludeFileNested",
			5,
			"Cannot find included file"
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testMalformedXMLTag() throws ExperimentExecutionException {
		readExperiment(
			"malformedXMLTag",
			6,
			"/>"
		);
	}
		
	@Test(expected=ExperimentExecutionException.class)
	public void testNonExistingBeanField() throws ExperimentExecutionException {
		readExperiment(
			"nonExistingBeanField",
			12
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testMissingRequiredBeanField() throws ExperimentExecutionException {
		readExperiment(
			"missingRequiredBeanField",
			11,
			new String[] {
				"stackProviderID",
				"ch.ethz.biol.cell.imageprocessing.chnl.provider.ChnlProviderStackReference",
				"must be non-null"
			}
		);
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncludeFileOverflow() throws ExperimentExecutionException {
		readExperiment(
			"includeFileOverflow",
			9,
			"Including file would cause overflow"
		);
	}
	
	
	/** With no contains... */
	private void readExperiment( String configName, int expectedNumberOfLines ) throws ExperimentExecutionException {
		readExperiment(
			configName,
			expectedNumberOfLines,
			new String[]{}
		);
	}
	
	/** With a single string as contains... */
	private void readExperiment( String configName, int expectedNumberOfLines, String contains ) throws ExperimentExecutionException {
		readExperiment(
			configName,
			expectedNumberOfLines,
			new String[]{contains}
		);
	}
	
	/** With multiple strings as contains... */
	private void readExperiment( String configName, int expectedNumberOfLines, String[] contains ) throws ExperimentExecutionException {
		
		String testPath = testPathToConfig(configName);
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			assertOutput(output, testPath, expectedNumberOfLines);
			assertOutputContains(output, contains);
			throw e;
		}
	}

	
	private static String testPathToConfig( String configName ) {
		return String.format(
			"erroredConfig/%s/config.xml",
			configName
		);
	}
	
	/** 
	 * Asserts that the output contains each item in an array
	 * 
	 * @param output the output of running the test
	 * @param contains the array of items, each one should be contained in output, or else an assertion is thrown
	 */
	private void assertOutputContains( String output, String[] contains ) {
		for (String str : contains) {
			assertTrue( output.contains(str) );
		}
	}
	
	/**
	 * Asserts the output has certain attributes
	 * 
	 * @param output the output of running the test
	 * @param testPath the path used to form the input
	 * @param expectedNumberOfLines expected number of lines in the error message
	 */
	private void assertOutput( String output, String testPath, int expectedNumberOfLines ) {
		assertTrue(output.length()>0);
		assertEquals(expectedNumberOfLines, numberOfLines(output));
		assertTrue( containsPathWithForwardSlashes(output,testPath) );			
	}
	
	
	/**
	 * The number of lines in a string (splitting by the current environments line seperator)
	 * @param str a string with 0 or more lines
	 * @return the number of lines
	 */
	private static int numberOfLines( String str ) {
		return str.split(System.getProperty("line.separator")).length;
	}
	
	
	/**
	 * Converts all backslashes to forward slashes in the source string, and searches for a path
	 * 
	 * @param search the string to search
	 * @param path path to search for (String.contains method)
	 * @return TRUE if path is contained within search, after it is converted to forward slashes
	 */
	private boolean containsPathWithForwardSlashes( String search, String path ) {
		String searchConvert = search.replace("\\", "/");
		return searchConvert.contains(path);
	}
}
