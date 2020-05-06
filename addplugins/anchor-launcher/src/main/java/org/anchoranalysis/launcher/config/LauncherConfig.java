package org.anchoranalysis.launcher.config;



/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.PathCurrentJarUtilities;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.executor.ExperimentExecutorFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public abstract class LauncherConfig {
	
	/** Config for resources sued by the launcher */
	public abstract ResourcesConfig resources();
	
	/** Config for displaying help message */
	public abstract HelpConfig help();
		
	/**
	 * if TRUE, then some extra newlines are inserted before error messages
	 *  
	 *  This useful for the GUI client in Windows due to WinRun4j running as a Windows app, and not as
	 *    a shell app. This changes how output is displayed;
	 */
	public abstract boolean newlinesBeforeError();
		
	public abstract ExperimentExecutionArguments createArguments( CommandLine line );
	
	public abstract void addAdditionalOptions(Options options);
	
	public ExperimentExecutor createExperimentExecutor(CommandLine line) throws ExperimentExecutionException {
				
		Path pathCurrentJARDir = PathCurrentJarUtilities.pathCurrentJAR( classInCurrentJar() );
		
		Path pathDefaultExperiment = pathDefaultExperiment(pathCurrentJARDir); 
		
		// Assumes config-dir is always the directory of defaultExperiment.xml
		ExperimentExecutor executor = ExperimentExecutorFactory.create(
			line,
			pathDefaultExperiment,
			pathDefaultExperiment.getParent(),
			pathCurrentJARDir
		);
		customizeExperimentTemplate(executor, line);
		return executor;
	}

	/** path to a property file that defines a relative-path to the default experiment in bean XML */
	protected abstract String pathRelativeProperties();
	
	protected abstract void customizeExperimentTemplate(ExperimentExecutor template, CommandLine line) throws ExperimentExecutionException;
	
	/** a class which we use to determine the base location for pathRelativeProperties */
	protected abstract Class<?> classInCurrentJar();
		
	/**
	 * Path to default-experiment
	 * 
	 * @return a path to the defaultExperiment
	 * @throws ExperimentExecutionException 
	 */
	private Path pathDefaultExperiment( Path pathCurrentJARDir ) throws ExperimentExecutionException {
		return PathDeriver.pathDefaultExperiment( pathCurrentJARDir, pathRelativeProperties() ); 
	}
}
