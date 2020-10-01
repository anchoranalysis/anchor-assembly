/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.anchoranalysis.launcher;

import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.config.ResourcesConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.options.ExtractArguments;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * A command-line interface for executing experiments
 *
 * @author Owen Feehan
 */
class LauncherConfigCommandLine extends LauncherConfig {

    // START: Resource PATHs
    private static final String RESOURCE_VERSION_FOOTER =
            "org/anchoranalysis/launcher/versionFooterDisplayMessage.txt";
    private static final String RESOURCE_MAVEN_PROPERTIES =
            "META-INF/maven/org.anchoranalysis.anchor/anchor-launcher/pom.properties";
    private static final String RESOURCE_USAGE_HEADER =
            "org/anchoranalysis/launcher/usageHeaderDisplayMessage.txt";
    private static final String RESOURCE_USAGE_FOOTER =
            "org/anchoranalysis/launcher/usageFooterDisplayMessage.txt";
    // END: Resource PATHs

    /** A path relative to the current JAR where a properties file can be found */
    private static final String PATH_RELATIVE_PROPERTIES = "anchor.properties";

    /** Adds additional options unique to this implementation */
    @Override
    public void addAdditionalOptions(Options options) {
        CommandLineOptions.addAdditionalOptions(options);
    }

    @Override
    public ResourcesConfig resources() {
        return new ResourcesConfig(
                getClass().getClassLoader(),
                RESOURCE_VERSION_FOOTER,
                RESOURCE_MAVEN_PROPERTIES,
                RESOURCE_USAGE_HEADER,
                RESOURCE_USAGE_FOOTER);
    }

    @Override
    public ExperimentExecutionArguments createArguments(CommandLine line) throws ExperimentExecutionException {
        ExperimentExecutionArguments arguments = new ExperimentExecutionArguments();
        
        ExtractArguments extract = new ExtractArguments(line);
        extract.single(CommandLineOptions.SHORT_OPTION_DEBUG, true).ifPresent(arguments::activateDebugMode);
        OptionalUtilities.ifPresent( extract.single(CommandLineOptions.SHORT_OPTION_OUTPUT_ADD, true), outputs ->
            arguments.assignAdditionalOutputs( AdditionalOutputsFactory.parseFrom(outputs, CommandLineOptions.SHORT_OPTION_OUTPUT_ADD)) );
        return arguments;
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
    protected void customizeExperimentTemplate(ExperimentExecutor template, CommandLine line)
            throws ExperimentExecutionException {
        template.setInput(SelectParamFactory.inputSelectParam(line, CommandLineOptions.SHORT_OPTION_INPUT));
        template.setOutput(SelectParamFactory.outputSelectParam(line, CommandLineOptions.SHORT_OPTION_OUTPUT));
        template.setTask(
                SelectParamFactory.pathOrTaskNameOrDefault(
                        line, CommandLineOptions.SHORT_OPTION_TASK, template.getConfigDir()));
        template.setDefaultBehaviourString(
                Optional.of("Searching recursively for image files. CTRL+C cancels"));
    }
}
