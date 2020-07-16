/* (C)2020 */
package org.anchoranalysis.launcher.config;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import org.anchoranalysis.experiment.ExperimentExecutionException;

class PathDeriver {

    /** A property that indicates a relative path to a default properties file */
    private static final String PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT =
            "default.config.path.relative";

    private PathDeriver() {}

    /**
     * Path to default-experiment
     *
     * @return a path to the defaultExperiment
     * @throws ExperimentExecutionException
     */
    public static Path pathDefaultExperiment(Path pathCurrentJARDir, String pathRelativeProperties)
            throws ExperimentExecutionException {

        String relativePathDefaultExperiment =
                relativePathDefaultExperiment(
                        propertyPath(pathCurrentJARDir, pathRelativeProperties));

        return pathCurrentJARDir.resolve(relativePathDefaultExperiment);
    }

    private static String relativePathDefaultExperiment(Path propertyPath)
            throws ExperimentExecutionException {

        Properties props = new Properties();

        try {
            props.load(new FileInputStream(propertyPath.toFile()));
        } catch (IOException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "An error occurred loading properties from the properties file at %s)",
                            propertyPath),
                    e);
        }

        if (!props.containsKey(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT)) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Properties file is missing key: %s",
                            PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT));
        }

        return props.getProperty(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT);
    }

    private static Path propertyPath(Path currentJARDir, String pathRelativeProperties)
            throws ExperimentExecutionException {

        Path pathProperties = currentJARDir.resolve(pathRelativeProperties);

        if (!pathProperties.toFile().exists()) {
            throw new ExperimentExecutionException(
                    String.format("Cannot find properties file at: %s", pathProperties));
        }

        return pathProperties;
    }
}
