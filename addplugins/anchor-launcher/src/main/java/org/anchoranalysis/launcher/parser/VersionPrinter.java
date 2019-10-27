package org.anchoranalysis.launcher.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * #%L
 * anchor-browser
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

/**
 * Prints version information to the console
 * 
 * @author Owen Feehan
 *
 */
class VersionPrinter {

	private VersionPrinter() {
		// Only accessible through static methods
	}
	
	/**
	 * Gets the current version of the software by reading a properties-file provided by the Maven build
	 * 
	 * NOTE that this pom.proper
	 * 
	 * @param cl class-loader where resource is found
	 * @param versionResourcePath resource-path (with class-loader) to obtain the version of the software 
	 * @return string describing the current version number of anchor-launcher
	 * @throws IOException if the properties file cannot be read, or is missing the appropriate version key
	 */
	private static String obtainVersionFromMavenProperties( ClassLoader cl, String versionResourcePath ) throws IOException {
		Properties props = new Properties();
    	
    	InputStream mavenPropertiesResource = cl.getResourceAsStream(versionResourcePath);
    	
    	if (mavenPropertiesResource==null) {
    		return "<unknown>";
    	}
    	
    	try(InputStream resourceStream = mavenPropertiesResource) {
    	    props.load(resourceStream);
    	}
    	
    	if (!props.containsKey("version")) {
    		throw new IOException("version property is missing from maven properties");
    	}
    	
    	return props.getProperty("version");
	}
	
	
	/**
	 * Describes which version of anchor is being used, and what version number
	 * 
	 * @param cl class-loader where resource is found
	 * @param footerResourcePath resource-path (with class-loader) to a displayed footer message. Ignored if NULL
	 * @param versionResourcePath resource-path (with class-loader) to obtain the version of the software
	 * @throws IOException if it's not possible to determine the version number
	 */
	public static void printVersion( ClassLoader cl, String footerResourcePath, String versionResourcePath ) throws IOException {
    	System.out.printf(
    		"anchor version %s by Owen Feehan (ETH Zurich, University of Zurich, 2016)%n",
    		obtainVersionFromMavenProperties( cl, versionResourcePath )
    	);
    	if (footerResourcePath!=null) {
    		System.out.println();
    		String footer = ResourceReader.readStringFromResource(footerResourcePath,cl);
    		System.out.print(footer);
    	}
	}
	
}
