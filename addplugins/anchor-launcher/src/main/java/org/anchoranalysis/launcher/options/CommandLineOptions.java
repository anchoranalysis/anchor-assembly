package org.anchoranalysis.launcher.options;

import static org.anchoranalysis.launcher.options.CustomArgumentOptions.multipleArguments;
import static org.anchoranalysis.launcher.options.CustomArgumentOptions.optionalSingleArgument;
import static org.anchoranalysis.launcher.options.CustomArgumentOptions.requiredSingleArgument;
import org.apache.commons.cli.Options;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * All command-line options used by the launcher.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class CommandLineOptions {

    // START: Options
    public static final String SHORT_OPTION_HELP = "h";
    public static final String SHORT_OPTION_VERSION = "v";
    public static final String SHORT_OPTION_LOG_ERROR = "l";
    public static final String SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS = "sa";
    
    public static final String SHORT_OPTION_DEBUG = "d";
    public static final String SHORT_OPTION_INPUT = "i";
    public static final String SHORT_OPTION_OUTPUT = "o";
    public static final String SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL = "oe";
    public static final String SHORT_OPTION_OUTPUT_ENABLE_ALL = "oa";
    public static final String SHORT_OPTION_TASK = "t";
    
    private static final String LONG_OPTION_HELP = "help";
    private static final String LONG_OPTION_VERSION = "version";
    private static final String LONG_OPTION_LOG_ERROR = "logError";
    private static final String LONG_OPTION_SHOW_EXPERIMENT_ARGUMENTS = "showArguments";
    
    private static final String LONG_OPTION_DEBUG = "debug";
    private static final String LONG_OPTION_INPUT = "input";
    private static final String LONG_OPTION_OUTPUT = "output";
    private static final String LONG_OPTION_OUTPUT_ENABLE_ADDITIONAL = "outputEnable";
    private static final String LONG_OPTION_OUTPUT_ENABLE_ALL = "outputEnableAll";
    private static final String LONG_OPTION_TASK = "task";
    // END: Options
    
    public static void addBasicOptions(Options options) {
        options.addOption(SHORT_OPTION_HELP, LONG_OPTION_HELP, false, "print this message and exit");

        options.addOption(SHORT_OPTION_VERSION, LONG_OPTION_VERSION, false, "print version information and exit");

        // This logs the errors in greater detail
        options.addOption(SHORT_OPTION_LOG_ERROR, LONG_OPTION_LOG_ERROR, true, "log BeanXML parsing errors to file");

        options.addOption(
                SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS, LONG_OPTION_SHOW_EXPERIMENT_ARGUMENTS, false, "print experiment path arguments");
    }
    
    public static void addAdditionalOptions(Options options) {

        options.addOption(optionalSingleArgument(SHORT_OPTION_DEBUG, LONG_OPTION_DEBUG, "enables debug mode"));

        options.addOption(
                multipleArguments(
                        SHORT_OPTION_INPUT,
                        LONG_OPTION_INPUT,
                        "an input-directory OR glob (e.g. small_*.jpg) OR file extension (e.g. .png) OR path to BeanXML"));

        options.addOption(
                requiredSingleArgument(SHORT_OPTION_OUTPUT, LONG_OPTION_OUTPUT, "an output-directory OR path to BeanXML"));

        options.addOption(optionalSingleArgument(SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL, LONG_OPTION_OUTPUT_ENABLE_ADDITIONAL, "enables specific additional output(s)"));
        
        options.addOption(SHORT_OPTION_OUTPUT_ENABLE_ALL, LONG_OPTION_OUTPUT_ENABLE_ALL, false, "enables all outputs");
        
        options.addOption(requiredSingleArgument(SHORT_OPTION_TASK, LONG_OPTION_TASK, "a task-name OR path to BeanXML"));
        
    }
}
