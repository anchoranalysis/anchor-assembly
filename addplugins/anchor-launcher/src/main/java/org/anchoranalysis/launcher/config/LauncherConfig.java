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

import org.anchoranalysis.core.file.PathUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.ExperimentExecutionTemplate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public abstract class LauncherConfig {
	
	/**
	 * A path to a folder where config files are stored (relative to the bin/ directory)
	 */
	private static String CONFIG_RELATIVE_PATH = "../config/";

	/**
	 * Directory where the configuration files are stored
	 * 
	 * @return a path to the directory
	 */
	public Path configDir() {
		Path pathCurrentJARDir = PathUtilities.pathCurrentJAR( classInCurrentJar() );
	    return pathCurrentJARDir.resolve(CONFIG_RELATIVE_PATH);
	}
	
	public abstract Class<?> classInCurrentJar();
	
	/** Config for resources sued by the launcher */
	public abstract ResourcesConfig resources();
	
	/** Config for displaying help message */
	public abstract HelpConfig help();
	
	public abstract ExperimentExecutionArguments createArguments( CommandLine line );
	
	public abstract ExperimentExecutionTemplate createExperimentTemplate( CommandLine line ) throws ExperimentExecutionException;
	
	
	/**
	 * if TRUE, then some extra newlines are inserted before error messages
	 *  
	 *  This useful for the GUI client in Windows due to WinRun4j running as a Windows app, and not as
	 *    a shell app. This changes how output is displayed;
	 */
	public abstract boolean newlinesBeforeError();
	
	public abstract void addAdditionalOptions(Options options);
}
