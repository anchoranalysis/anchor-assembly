package org.anchoranalysis.launcher;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamManagerFactory;
import org.anchoranalysis.launcher.config.CommandLineResources;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplate;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplateFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

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
 * A command-line interface for executing experiments
 * 
 * @author Owen Feehan
 *
 */
class LauncherConfigCommandLine extends LauncherConfig {
	
	// START: Options
	private static final String OPTION_GUI = "g";
	private static final String OPTION_DEBUG = "d";
	private static final String OPTION_INPUT = "i";
	private static final String OPTION_OUTPUT = "o";
	// END: Options
	
	// START: Resource PATHs
	private static final String RESOURCE_VERSION_FOOTER =  "org/anchoranalysis/launcher/versionFooterDisplayMessage.txt";
	private static final String RESOURCE_MAVEN_PROPERTIES = "META-INF/maven/org.anchoranalysis.anchor/anchor-launcher/pom.properties";
	private static final String RESOURCE_USAGE_HEADER =  "org/anchoranalysis/launcher/usageHeaderDisplayMessage.txt";
	private static final String RESOURCE_USAGE_FOOTER =  "org/anchoranalysis/launcher/usageFooterDisplayMessage.txt";
	// END: Resource PATHs
	
	/**
	 * A path relative to the current JAR where a properties file can be found
	 */
	private static final String PATH_RELATIVE_PROPERTIES = "anchor.properties";
	
	/**
	 * Adds additional options unique to this implementation
	 */
	@Override
	public void addAdditionalOptions(Options options) {
	
		options.addOption(OPTION_GUI, false, "enables GUI display dialogs");
		
		options.addOption(OPTION_DEBUG, false, "enables debug mode");
		
		options.addOption(OPTION_INPUT, true, "an input-directory or manager (path to BeanXML)");
		
		options.addOption(OPTION_OUTPUT, true, "an output-directory or manager (path to BeanXML)");
	}

	@Override
	public String commandNameInHelp() {
		return "anchor";
	}

	@Override
	public String firstArgumentInHelp() {
		return "experimentFile.xml";
	}
	
	@Override
	public CommandLineResources resources() {
		return new CommandLineResources(
			getClass().getClassLoader(),
			RESOURCE_VERSION_FOOTER,
			RESOURCE_MAVEN_PROPERTIES,
			RESOURCE_USAGE_HEADER,
			RESOURCE_USAGE_FOOTER
		);
	}
	
	@Override
	public ExperimentExecutionTemplate createExperimentTemplate( CommandLine line ) throws ExperimentExecutionException {
		
		ExperimentExecutionTemplate template = ExperimentExecutionTemplateFactory.create(
			line,
			PATH_RELATIVE_PROPERTIES,
			LauncherConfigCommandLine.class
		);
		template.setInput(
			SelectParamManagerFactory.create( line, OPTION_INPUT, true )
		);
		template.setOutput(
			SelectParamManagerFactory.create(line, OPTION_OUTPUT, false )
		);
		
        return template;
	}
	
	@Override
	public ExperimentExecutionArguments createArguments( CommandLine line ) {
		ExperimentExecutionArguments ea = new ExperimentExecutionArguments();
        if (line.hasOption(OPTION_GUI)) {
        	ea.setGUIEnabled(true);
        }
        if (line.hasOption(OPTION_DEBUG)) {
        	ea.setDebugEnabled(true);
        }
        return ea;
	}

	@Override
	public Class<?> classInCurrentJar() {
		return LauncherConfigCommandLine.class;
	}

	@Override
	public boolean newlinesBeforeError() {
		return false;
	}
}
