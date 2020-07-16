/* (C)2020 */
package org.anchoranalysis.launcher;

import static org.anchoranalysis.launcher.CustomOptions.*;

import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.config.ResourcesConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * A command-line interface for executing experiments
 *
 * @author Owen Feehan
 */
class LauncherConfigCommandLine extends LauncherConfig {

    // START: Options
    private static final String OPTION_DEBUG = "d";
    private static final String OPTION_INPUT = "i";
    private static final String OPTION_OUTPUT = "o";
    private static final String OPTION_TASK = "t";
    // END: Options

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

        options.addOption(optionalSingleArgument(OPTION_DEBUG, "enables debug mode"));

        options.addOption(
                multipleArguments(
                        OPTION_INPUT,
                        "an input-directory OR glob (e.g. small_*.jpg) OR file extension (e.g. .png) OR path to BeanXML"));

        options.addOption(
                requiredSingleArgument(OPTION_OUTPUT, "an output-directory OR path to BeanXML"));

        options.addOption(requiredSingleArgument(OPTION_TASK, "a task-name OR path to BeanXML"));
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
    public ExperimentExecutionArguments createArguments(CommandLine line) {
        ExperimentExecutionArguments ea = new ExperimentExecutionArguments();
        if (line.hasOption(OPTION_DEBUG)) {
            ea.activateDebugMode(line.getOptionValue(OPTION_DEBUG));
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
    protected void customizeExperimentTemplate(ExperimentExecutor template, CommandLine line)
            throws ExperimentExecutionException {
        template.setInput(SelectParamFactory.inputSelectParam(line, OPTION_INPUT));
        template.setOutput(SelectParamFactory.outputSelectParam(line, OPTION_OUTPUT));
        template.setTask(
                SelectParamFactory.pathOrTaskNameOrDefault(
                        line, OPTION_TASK, template.getConfigDir()));
        template.setDefaultBehaviourString(
                Optional.of("Searching recursively for image files. CTRL+C cancels"));
    }
}
