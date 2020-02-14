package org.anchoranalysis.browser.launcher;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplate;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplateFactory;
import org.anchoranalysis.launcher.parser.CommandLineParserConfig;
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

class CommandLineParserConfigBrowser extends CommandLineParserConfig {
	
	/**
	 * A path relative to the current JAR where a properties file can be found
	 */
	private static final String PATH_RELATIVE_PROPERTIES = "anchorGUI.properties";

	private static final String RESOURCE_VERSION_FOOTER =  "org/anchoranalysis/browser/launcher/versionFooterDisplayMessage.txt";
	private static final String RESOURCE_USAGE_HEADER =  "org/anchoranalysis/browser/launcher/usageHeaderDisplayMessage.txt";
	private static final String RESOURCE_USAGE_FOOTER =  "org/anchoranalysis/browser/launcher/usageFooterDisplayMessage.txt";
	private static final String RESOURCE_MAVEN_PROPERTIES = "META-INF/maven/org.anchoranalysis.anchor/anchor-browser/pom.properties";
	
	@Override
	public ClassLoader classLoaderResources() {
		return getClass().getClassLoader();
	}

	@Override
	public String commandNameInHelp() {
		return "anchorGUI";
	}

	@Override
	public String firstArgumentInHelp() {
		return "configFile.xml";
	}
	

	@Override
	public String resourceVersionFooter() {
		return RESOURCE_VERSION_FOOTER;
	}

	@Override
	public String resourceMavenProperties() {
		return RESOURCE_MAVEN_PROPERTIES;
	}

	@Override
	public String resourceUsageHeader() {
		return RESOURCE_USAGE_HEADER;
	}

	@Override
	public String resourceUsageFooter() {
		return RESOURCE_USAGE_FOOTER;
	}

	@Override
	public boolean requiresFirstArgument() {
		return false;
	}

	@Override
	public ExperimentExecutionArguments createArguments( CommandLine line ) {
		 ExperimentExecutionArguments ea = new ExperimentExecutionArguments();
	     ea.setGUIEnabled(true);
	     return ea;
	}

	@Override
	public Class<?> classInCurrentJar() {
		return CommandLineParserConfigBrowser.class;
	}

	@Override
	public ExperimentExecutionTemplate createExperimentTemplate(CommandLine line) throws ExperimentExecutionException {
		return ExperimentExecutionTemplateFactory.create(
			line,
			PATH_RELATIVE_PROPERTIES,
			CommandLineParserConfigBrowser.class
		);
	}

	@Override
	public boolean newlinesBeforeError() {
		return true;
	}

	@Override
	public void addAdditionalOptions(Options options) {
	}

}
