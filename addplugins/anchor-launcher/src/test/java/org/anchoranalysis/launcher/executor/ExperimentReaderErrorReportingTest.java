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

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.ExperimentExecutorObj;
import org.anchoranalysis.launcher.executor.ExperimentReader;
import org.junit.BeforeClass;
import org.junit.Test;

import anchor.test.TestLoader;

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

	private static TestLoader tl = TestLoader.createFromMavenWorkingDirTest();
		
	@BeforeClass
    public static void setUp() throws Exception {
        ExperimentExecutorObj.initializeIfNecessary(false, tl.getRoot(), false, false );
    }
	
	@Test(expected=ExperimentExecutionException.class)
	public void testNonExistentFilePath() throws ExperimentExecutionException {
		
		// We assume this path does not exist
		String testPath = "a non-existent file path";
		
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue( output.length()>0 );
			assertTrue( numberOfLines(output)==1 );
			assertTrue( containsPathWithForwardSlashes(output,testPath) );
			
			throw e;
		}
	}
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectEndTag() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/incorrectEndTag/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==3);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			
			throw e;
		}
	}
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testMissingRootTag() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/missingRootTag/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==2);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			assertTrue( output.contains("experiment") );		
			throw e;
		}
	}
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectClass() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/incorrectClass/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==8);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			assertTrue( output.contains("Please check spelling of config-class attributes") );
			assertTrue( output.contains("ch.ethz.biol.cell.imageprocessing.io.task.ANonExistentClass"));
			
			throw e;
		}
	}
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectFactory() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/incorrectFactory/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==7);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			assertTrue( output.contains("Unknown bean factory") );
			assertTrue( output.contains("nonExistentFactory"));
			
			throw e;
		}
	}
	
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectClassNested() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/incorrectClassNested/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==12);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			assertTrue( output.contains("Please check spelling of config-class attributes") );
			assertTrue( output.contains("ch.ethz.biol.cell.imageprocessing.chnl.provider.SomeNoneExistentClass"));
			
			throw e;
		}
	}
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectIncludeFile() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/incorrectIncludeFile/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==8);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			assertTrue( output.contains("Cannot find included file") );
			
			throw e;
		}
	}
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncorrectIncludeFileNested() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/incorrectIncludeFileNested/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==5);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			assertTrue( output.contains("Cannot find included file") );
			
			throw e;
		}
	}
	
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testMalformedXMLTag() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/malformedXMLTag/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==6);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			assertTrue( output.contains("/>") );
			
			throw e;
		}
	}
	
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testNonExistingBeanField() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/nonExistingBeanField/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==12);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );			
			
			throw e;
		}
	}
	
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testMissingRequiredBeanField() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/missingRequiredBeanField/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==11);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );
			assertTrue( output.contains("stackProviderID") );
			assertTrue( output.contains("ch.ethz.biol.cell.imageprocessing.chnl.provider.ChnlProviderStackReference") );
			assertTrue( output.contains("must be non-null") );
			
			throw e;
		}
	}
	
	
	
	
	@Test(expected=ExperimentExecutionException.class)
	public void testIncludeFileOverflow() throws ExperimentExecutionException {
		
		String testPath = "erroredConfig/includeFileOverflow/config.xml";
				
		Path experimentConfigFile = tl.resolveTestPath(testPath);

		// Catch and display the output
		try {
			ExperimentReader.readExperimentFromXML(experimentConfigFile, new ExperimentExecutionArguments() );
		} catch (ExperimentExecutionException e) {
		
			String output = e.friendlyMessageHierarchy();
			
			// Some qualities we'd like the error message to have
			assertTrue(output.length()>0);
			assertTrue(numberOfLines(output)==8);
			assertTrue( containsPathWithForwardSlashes(output,testPath) );
			assertTrue( output.contains("Including file would cause overflow") );
			
			throw e;
		}
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
