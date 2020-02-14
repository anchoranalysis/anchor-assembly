package org.anchoranalysis.browser.launcher;

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplate;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplateFactory;
import org.anchoranalysis.launcher.parser.CommandLineParserExperimentWithConfig;
import org.apache.commons.cli.CommandLine;

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

class CommandLineParserExperimentBrowser extends CommandLineParserExperimentWithConfig {
	
	/**
	 * A path relative to the current JAR where a properties file can be found
	 */
	private static final String PATH_RELATIVE_PROPERTIES = "anchorGUI.properties";

	private static final String RESOURCE_VERSION_FOOTER =  "org/anchoranalysis/browser/launcher/versionFooterDisplayMessage.txt";
	private static final String RESOURCE_USAGE_HEADER =  "org/anchoranalysis/browser/launcher/usageHeaderDisplayMessage.txt";
	private static final String RESOURCE_USAGE_FOOTER =  "org/anchoranalysis/browser/launcher/usageFooterDisplayMessage.txt";
	private static final String RESOURCE_MAVEN_PROPERTIES = "META-INF/maven/org.anchoranalysis.anchor/anchor-browser/pom.properties";
	
	
	/**
	 * A command line parser application for the browser application
	 * @param logger for reporting user-friendly errors
	 */
	public CommandLineParserExperimentBrowser(LogErrorReporter logger) {
		super(logger, true);
	}
	
	@Override
	protected ClassLoader classLoaderResources() {
		return getClass().getClassLoader();
	}

	@Override
	protected String commandNameInHelp() {
		return "anchorGUI";
	}

	@Override
	protected String firstArgumentInHelp() {
		return "configFile.xml";
	}
	

	@Override
	protected String resourceVersionFooter() {
		return RESOURCE_VERSION_FOOTER;
	}

	@Override
	protected String resourceMavenProperties() {
		return RESOURCE_MAVEN_PROPERTIES;
	}

	@Override
	protected String resourceUsageHeader() {
		return RESOURCE_USAGE_HEADER;
	}

	@Override
	protected String resourceUsageFooter() {
		return RESOURCE_USAGE_FOOTER;
	}

	@Override
	protected boolean requiresFirstArgument() {
		return false;
	}

	@Override
	protected ExperimentExecutionArguments createArguments( CommandLine line ) {
		 ExperimentExecutionArguments ea = new ExperimentExecutionArguments();
	     ea.setGUIEnabled(true);
	     return ea;
	}

	@Override
	protected Class<?> classInCurrentJar() {
		return CommandLineParserExperimentBrowser.class;
	}

	@Override
	protected ExperimentExecutionTemplate createExperimentTemplate(CommandLine line) throws ExperimentExecutionException {
		return ExperimentExecutionTemplateFactory.create(
			line,
			PATH_RELATIVE_PROPERTIES,
			CommandLineParserExperimentBrowser.class
		);
	}

}
