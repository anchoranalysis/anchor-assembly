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
