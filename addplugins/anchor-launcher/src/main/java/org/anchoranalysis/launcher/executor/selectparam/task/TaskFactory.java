/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
/* (C)2020 */
package org.anchoranalysis.launcher.executor.selectparam.task;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.CustomManagerFromPath;
import org.anchoranalysis.launcher.executor.selectparam.path.PathConverter;

/**
 * SelectParam<Path> factory for tasks
 *
 * @author Owen Feehan
 */
public class TaskFactory {

    private TaskFactory() {}

    /**
     * If the argument a name (no extension, no root, no special-chars apart from forward-slashes),
     * then construct an automatic path to the tasks in the configuration directory. Otherwise treat
     * as path to BeanXML
     */
    public static SelectParam<Optional<Path>> pathOrTaskName(String[] args, Path configDir) {

        if (args.length != 1) {
            throw new CommandLineException("One and only one argument is permitted after -t");
        }

        String taskArg = args[0];

        if (isTaskName(taskArg)) {
            return new UpdateTaskName<>(
                    new CustomManagerFromPath(constructPathForTaskName(taskArg, configDir)),
                    taskArg);
        } else {
            return new CustomManagerFromPath(PathConverter.pathFromArg(taskArg));
        }
    }

    private static Path constructPathForTaskName(String arg, Path configDir) {
        return configDir.resolve("tasks").resolve(arg + ".xml");
    }

    // Check if it contains only a restricted set of characters... alphaNumeric, hyphen, underscore,
    // forward-slash
    private static boolean isTaskName(String arg) {
        return arg.matches("^[a-zA-Z0-9_\\-\\/]+$");
    }
}
