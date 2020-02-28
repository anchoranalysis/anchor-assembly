package org.anchoranalysis.launcher;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.ResourcesConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
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
	private static final String OPTION_DEBUG = "d";
	private static final String OPTION_INPUT = "i";
	private static final String OPTION_OUTPUT = "o";
	private static final String OPTION_TASK = "t";
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
		
		options.addOption(OPTION_DEBUG, false, "enables debug mode");
		
		addInputOption(options);
		
		options.addOption(OPTION_OUTPUT, true, "an output-directory OR path to BeanXML");
		
		options.addOption(OPTION_TASK, true, "a task-name OR path to BeanXML");
	}
	
	/** The input option is added separately as it can take more than a single argument */
	private void addInputOption(Options options) {
		Option optionInput = new Option(OPTION_INPUT, true, "an input-directory OR glob (e.g. small_*.jpg) OR file extension (e.g. .png) OR path to BeanXML");
		optionInput.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(optionInput);		
	}
	
	@Override
	public ResourcesConfig resources() {
		return new ResourcesConfig(
			getClass().getClassLoader(),
			RESOURCE_VERSION_FOOTER,
			RESOURCE_MAVEN_PROPERTIES,
			RESOURCE_USAGE_HEADER,
			RESOURCE_USAGE_FOOTER
		);
	}
	
	@Override
	public ExperimentExecutionArguments createArguments( CommandLine line ) {
		ExperimentExecutionArguments ea = new ExperimentExecutionArguments();
        if (line.hasOption(OPTION_DEBUG)) {
        	ea.setDebugEnabled(true);
        }
        return ea;
	}

	@Override
	protected Class<?> classInCurrentJar() {
		return LauncherConfigCommandLine.class;
	}

	@Override
	public boolean newlinesBeforeError() {
		return false;
	}

	@Override
	public HelpConfig help() {
		return new HelpConfig("anchor", "experimentFile.xml");
	}

	@Override
	protected String pathRelativeProperties() {
		return PATH_RELATIVE_PROPERTIES;
	}
	
	@Override
	protected void customizeExperimentTemplate(ExperimentExecutor template, CommandLine line) {
		template.setInput(
			SelectParamFactory.inputSelectParam( line, OPTION_INPUT )
		);
		template.setOutput(
			SelectParamFactory.outputSelectParam(line, OPTION_OUTPUT )
		);
		template.setTask(
			SelectParamFactory.pathOrTaskNameOrDefault(line, OPTION_TASK, configDir() )
		);
		template.setDefaultBehaviourString( "Searching for inputs as per default experiment. CTRL+C cancels" );
	}
}
