/* (C)2020 */
package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import org.anchoranalysis.bean.xml.BeanXmlLoader;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bean.OutputManager;

class ExperimentReader {

    private ExperimentReader() {
        // Only accessible through static methods
    }

    public static Experiment readExperimentFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "experiment", true);
    }

    public static InputManager<InputFromManager> readInputManagerFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "bean", false);
    }

    public static OutputManager readOutputManagerFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "bean", false);
    }

    public static Task<InputFromManager, Object> readTaskFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "bean", false);
    }

    /**
     * Read bean from xml
     *
     * @param configPath the path where the xml file exists
     * @param xmlPath the xpath inside the xmlpath specifying the root-element
     * @param associateXml if TRUE, the xml is associated with the object (see BeanXmlLoader). if
     *     FALSE, it is not.
     * @return an object created from the read BeanXML
     * @throws ExperimentExecutionException
     */
    private static <T> T readBeanFromXML(Path configPath, String xmlPath, boolean associateXml)
            throws ExperimentExecutionException {

        // To avoid any .. or . in error reporting
        configPath = configPath.normalize();

        if (!configPath.toFile().exists()) {
            throw new ExperimentExecutionException(
                    String.format("Error: a file does not exist at \"%s\"", configPath));
        }

        try {
            if (associateXml) {
                return BeanXmlLoader.loadBeanAssociatedXml(configPath, xmlPath);
            } else {
                return BeanXmlLoader.loadBean(configPath, xmlPath);
            }

        } catch (BeanXmlException e) {
            String errorMsg =
                    String.format(
                            "An error occurred reading the experiment bean XML at \"%s\".%nPlease ensure this is validly-formatted BeanXML for an experiment.",
                            configPath);
            throw new ExperimentExecutionException(errorMsg, e);
        }
    }
}
