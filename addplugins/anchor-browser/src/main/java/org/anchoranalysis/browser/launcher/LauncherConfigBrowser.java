package org.anchoranalysis.browser.launcher;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.ResourcesConfig;
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
 * A command-line interface for launching the GUI browser
 * 
 * @author owen
 *
 */
class LauncherConfigBrowser extends LauncherConfig {
	
	/**
	 * A path relative to the current JAR where a properties file can be found
	 */
	private static final String PATH_RELATIVE_PROPERTIES = "anchorGUI.properties";

	private static final String RESOURCE_VERSION_FOOTER =  "org/anchoranalysis/browser/launcher/versionFooterDisplayMessage.txt";
	private static final String RESOURCE_USAGE_HEADER =  "org/anchoranalysis/browser/launcher/usageHeaderDisplayMessage.txt";
	private static final String RESOURCE_USAGE_FOOTER =  "org/anchoranalysis/browser/launcher/usageFooterDisplayMessage.txt";
	private static final String RESOURCE_MAVEN_PROPERTIES = "META-INF/maven/org.anchoranalysis.anchor/anchor-browser/pom.properties";

	@Override
	public ExperimentExecutionArguments createArguments( CommandLine line ) {
		 ExperimentExecutionArguments ea = new ExperimentExecutionArguments();
	     ea.setGUIEnabled(true);
	     return ea;
	}

	@Override
	public Class<?> classInCurrentJar() {
		return LauncherConfigBrowser.class;
	}

	@Override
	public ExperimentExecutionTemplate createExperimentTemplate(CommandLine line) throws ExperimentExecutionException {
		return ExperimentExecutionTemplateFactory.create(
			line,
			PATH_RELATIVE_PROPERTIES,
			LauncherConfigBrowser.class
		);
	}

	@Override
	public boolean newlinesBeforeError() {
		return true;
	}

	@Override
	public void addAdditionalOptions(Options options) {
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
	public HelpConfig help() {
		return new HelpConfig("anchorGUI", "configFile.xml");
	}

}
