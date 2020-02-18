package org.anchoranalysis.launcher.executor;



import java.nio.file.Files;

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


import java.nio.file.Path;

import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bean.OutputManager;


class ExperimentReader {
	
	private ExperimentReader() {
		// Only accessible through static methods
	}

	public static Experiment readExperimentFromXML( Path configPath, ExperimentExecutionArguments ea ) throws ExperimentExecutionException {
		return readBeanFromXML( configPath, ea, "experiment", true );
	}
	
	public static InputManager<InputFromManager> readInputManagerFromXML( Path configPath, ExperimentExecutionArguments ea ) throws ExperimentExecutionException {
		return readBeanFromXML( configPath, ea, "bean", false );
	}
	
	public static OutputManager readOutputManagerFromXML( Path configPath, ExperimentExecutionArguments ea ) throws ExperimentExecutionException {
		return readBeanFromXML( configPath, ea, "bean", false );
	}
	
	public static Task<InputFromManager,Object> readTaskFromXML( Path configPath, ExperimentExecutionArguments ea ) throws ExperimentExecutionException {
		return readBeanFromXML( configPath, ea, "bean", false );
	}
	
	/**
	 * Read bean from xml
	 * 
	 * @param configPath the path where the xml file exists
	 * @param ea experiment-arguments
	 * @param xmlPath the xpath inside the xmlpath specifying the root-element
	 * @param associateXml if TRUE, the xml is associated with the object (see BeanXmlLoader). if FALSE, it is not.
	 * @return an object created from the read BeanXML
	 * @throws ExperimentExecutionException
	 */
	private static <T> T readBeanFromXML( Path configPath, ExperimentExecutionArguments ea, String xmlPath, boolean associateXml ) throws ExperimentExecutionException {

		// To avoid any .. or . in error reporting
		configPath = configPath.normalize();
		
		if (!Files.exists(configPath)) {
			throw new ExperimentExecutionException( String.format("Error: a file does not exist at \"%s\"", configPath) );
		}
		
		try {
			if (associateXml) {
				return BeanXmlLoader.loadBeanAssociatedXml(configPath, xmlPath);
			} else {
				return BeanXmlLoader.loadBean(configPath, xmlPath);
			}
	
		} catch (BeanXmlException e) {
			String errorMsg = String.format("An error occurred interpreting the bean XML at \"%s\"", configPath);
			throw new ExperimentExecutionException(errorMsg,e);
		}
		
	}


}
