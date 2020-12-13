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
package org.anchoranalysis.launcher.options;

import static org.anchoranalysis.launcher.options.CustomArgumentOptions.multipleArguments;
import static org.anchoranalysis.launcher.options.CustomArgumentOptions.optionalSingleArgument;
import static org.anchoranalysis.launcher.options.CustomArgumentOptions.requiredSingleArgument;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.Options;

/**
 * All command-line options used by the launcher.
 *
 * @see <a href="https://www.anchoranalysis.org/user_guide_command_line.html">User Guide - Command
 *     Line</a>
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandLineOptions {

    // START: SHORT input options
    /** Changes inputs. */
    public static final String SHORT_OPTION_INPUT = "i";
    // END: SHORT input options

    // START: SHORT task options
    /** Changes task. */
    public static final String SHORT_OPTION_TASK = "t";

    /** Suggests dimensions to resize to or a scaling factor for certain tasks. */
    public static final String SHORT_OPTION_TASK_RESIZE = "pr";
    // END: SHORT task options

    // START: SHORT output options
    /** Changes outputs. */
    public static final String SHORT_OPTION_OUTPUT = "o";

    /** Enables all outputs. */
    public static final String SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL = "oe";

    /** Disables specific output(s). Multiple outputs are comma-separated. */
    public static final String SHORT_OPTION_OUTPUT_DISABLE_ADDITIONAL = "od";

    /** Enables specific output(s). Multiple outputs are comma-separated. */
    public static final String SHORT_OPTION_OUTPUT_ENABLE_ALL = "oa";

    /** Suggests an output image file format: e.g -of jpg or -of ome.xml. */
    public static final String SHORT_OPTION_OUTPUT_IMAGE_FILE_FORMAT = "of";

    /**
     * Outputs with an incrementing number instead of the input identifier.
     *
     * <p>(useful for creating sequences of images)
     */
    public static final String SHORT_OPTION_OUTPUT_INCREMENTING_NUMBER = "on";
    // END: SHORT output options

    // START: SHORT debug options
    /** Enables debug-mode: runs only the first available input [whose name contains string]. */
    public static final String SHORT_OPTION_DEBUG = "d";

    /** Logs initial BeanXML errors in greater detail to a file-path. */
    public static final String SHORT_OPTION_LOG_ERROR = "l";

    /** Shows additional argument information, otherwise executes as normal. */
    public static final String SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS = "sa";

    /** Prints the names of predefined tasks that can be easily used with -t. */
    public static final String SHORT_OPTION_SHOW_TASKS = "st";
    // END: SHORT debug options

    // START: SHORT application information options
    /** Displays help message with all command-line options. */
    public static final String SHORT_OPTION_HELP = "h";

    /** Displays version and authorship information. */
    public static final String SHORT_OPTION_VERSION = "v";
    // END: SHORT application information options

    // START: All LONG options
    private static final String LONG_OPTION_HELP = "help";
    private static final String LONG_OPTION_VERSION = "version";
    private static final String LONG_OPTION_LOG_ERROR = "logError";
    private static final String LONG_OPTION_SHOW_EXPERIMENT_ARGUMENTS = "showArguments";
    private static final String LONG_OPTION_SHOW_TASKS = "showTasks";

    private static final String LONG_OPTION_DEBUG = "debug";
    private static final String LONG_OPTION_INPUT = "input";
    private static final String LONG_OPTION_OUTPUT = "output";
    private static final String LONG_OPTION_OUTPUT_ENABLE_ADDITIONAL = "outputEnable";
    private static final String LONG_OPTION_OUTPUT_DISABLE_ADDITIONAL = "outputDisable";
    private static final String LONG_OPTION_OUTPUT_ENABLE_ALL = "outputEnableAll";
    private static final String LONG_OPTION_OUTPUT_IMAGE_FILE_FORMAT = "outputFileFormat";
    private static final String LONG_OPTION_OUTPUT_INCREMENTING_NUMBER = "outputIncrementingNumber";

    private static final String LONG_OPTION_TASK = "task";
    private static final String LONG_OPTION_TASK_RESIZE = "paramResize";
    // END: All LONG options

    public static void addBasicOptions(Options options) {
        options.addOption(
                SHORT_OPTION_HELP, LONG_OPTION_HELP, false, "print this message and exit");

        options.addOption(
                SHORT_OPTION_VERSION,
                LONG_OPTION_VERSION,
                false,
                "print version information and exit");

        // This logs the errors in greater detail
        options.addOption(
                SHORT_OPTION_LOG_ERROR,
                LONG_OPTION_LOG_ERROR,
                true,
                "log BeanXML parsing errors to file");

        options.addOption(
                SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS,
                LONG_OPTION_SHOW_EXPERIMENT_ARGUMENTS,
                false,
                "print experiment path arguments");

        options.addOption(
                SHORT_OPTION_SHOW_TASKS,
                LONG_OPTION_SHOW_TASKS,
                false,
                "print task-names as useful for -t <name>");
    }

    public static void addAdditionalOptions(Options options) {

        options.addOption(
                optionalSingleArgument(
                        SHORT_OPTION_DEBUG, LONG_OPTION_DEBUG, "enables debug mode"));

        options.addOption(
                multipleArguments(
                        SHORT_OPTION_INPUT,
                        LONG_OPTION_INPUT,
                        "an input-directory OR glob (e.g. small_*.jpg) OR file extension (e.g. .png) OR path to BeanXML"));

        addOutputOptions(options);
        addTaskOptions(options);
    }

    private static void addOutputOptions(Options options) {

        options.addOption(
                requiredSingleArgument(
                        SHORT_OPTION_OUTPUT,
                        LONG_OPTION_OUTPUT,
                        "an output-directory OR path to BeanXML"));

        options.addOption(
                optionalSingleArgument(
                        SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL,
                        LONG_OPTION_OUTPUT_ENABLE_ADDITIONAL,
                        "enables specific additional output(s)"));

        options.addOption(
                optionalSingleArgument(
                        SHORT_OPTION_OUTPUT_DISABLE_ADDITIONAL,
                        LONG_OPTION_OUTPUT_DISABLE_ADDITIONAL,
                        "disables specific additional output(s)"));

        options.addOption(
                SHORT_OPTION_OUTPUT_ENABLE_ALL,
                LONG_OPTION_OUTPUT_ENABLE_ALL,
                false,
                "enables all outputs");

        options.addOption(
                optionalSingleArgument(
                        SHORT_OPTION_OUTPUT_IMAGE_FILE_FORMAT,
                        LONG_OPTION_OUTPUT_IMAGE_FILE_FORMAT,
                        "suggested image-format for writing"));

        options.addOption(
                SHORT_OPTION_OUTPUT_INCREMENTING_NUMBER,
                LONG_OPTION_OUTPUT_INCREMENTING_NUMBER,
                false,
                "outputs with incrementing number sequence");
    }

    private static void addTaskOptions(Options options) {
        options.addOption(
                requiredSingleArgument(
                        SHORT_OPTION_TASK, LONG_OPTION_TASK, "a task-name OR path to BeanXML"));

        options.addOption(
                requiredSingleArgument(
                        SHORT_OPTION_TASK_RESIZE,
                        LONG_OPTION_TASK_RESIZE,
                        "suggests an image size to scale to (or scaling factor)"));
    }
}
