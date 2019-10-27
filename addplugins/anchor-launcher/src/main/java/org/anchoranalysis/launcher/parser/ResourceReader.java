package org.anchoranalysis.launcher.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

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

class ResourceReader {
	
	private ResourceReader() {
		// Only accessible through static methods
	}
	
	/**
	 * Reads a string from a resource, or displays an error message
	 * 
	 * @param resourceFileName the file-name to identify the resource (in the root directory)
	 * @param cl class-loader where resource is found
	 */
	public static String readStringFromResource( String resourceFileName, ClassLoader cl ) throws IOException {
		InputStream helpDisplayResource = cl.getResourceAsStream( resourceFileName);
		if (helpDisplayResource!=null) {
	    	return IOUtils.toString(
		      helpDisplayResource,
		      "UTF-8"
		    );
	    } else {
	    	return resourceFileName + " is missing, so cannot display.";
	    }		
	}
}
