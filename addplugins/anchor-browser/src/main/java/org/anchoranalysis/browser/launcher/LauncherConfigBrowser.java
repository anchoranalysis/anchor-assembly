/* (C)2020 */
package org.anchoranalysis.browser.launcher;

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.config.ResourcesConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * A command-line interface for launching the GUI browser
 *
 * @author Owen Feehan
 */
class LauncherConfigBrowser extends LauncherConfig {

    /** A path relative to the current JAR where a properties file can be found */
    private static final String PATH_RELATIVE_PROPERTIES = "anchorGUI.properties";

    private static final String RESOURCE_VERSION_FOOTER =
            "org/anchoranalysis/browser/launcher/versionFooterDisplayMessage.txt";
    private static final String RESOURCE_USAGE_HEADER =
            "org/anchoranalysis/browser/launcher/usageHeaderDisplayMessage.txt";
    private static final String RESOURCE_USAGE_FOOTER =
            "org/anchoranalysis/browser/launcher/usageFooterDisplayMessage.txt";
    private static final String RESOURCE_MAVEN_PROPERTIES =
            "META-INF/maven/org.anchoranalysis.anchor/anchor-browser/pom.properties";

    @Override
    public ExperimentExecutionArguments createArguments(CommandLine line) {
        return new ExperimentExecutionArguments();
    }

    @Override
    protected Class<?> classInCurrentJar() {
        return LauncherConfigBrowser.class;
    }

    @Override
    public boolean newlinesBeforeError() {
        return true;
    }

    @Override
    public void addAdditionalOptions(Options options) {
        // Nothing to do here for the browser
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
    public HelpConfig help() {
        return new HelpConfig("anchorGUI", "configFile.xml");
    }

    @Override
    protected String pathRelativeProperties() {
        return PATH_RELATIVE_PROPERTIES;
    }

    @Override
    protected void customizeExperimentTemplate(ExperimentExecutor template, CommandLine line) {
        // Nothing to do here for the browser
    }
}
