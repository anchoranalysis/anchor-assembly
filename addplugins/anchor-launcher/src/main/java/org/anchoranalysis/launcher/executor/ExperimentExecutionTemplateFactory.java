package org.anchoranalysis.launcher.executor;

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.FilePathResolver;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamExperimentFactory;
import org.apache.commons.cli.CommandLine;

public class ExperimentExecutionTemplateFactory {

	/**
	 * Creates an execution-template from a command line that EITHER:
	 *       uses a default-experiment
	 *    OR accepts a path passed as the first command-line argument
	 *    
	 * @param line the command-line arguments
	 * @param pathRelativeProperties a property that defines a relative-path to the default experiment in bean XML
	 * @param executingClass a class which we use to determine the base location for pathRelativeProperties
	 * @return
	 * @throws ExperimentExecutionException
	 */
	public static ExperimentExecutionTemplate create( CommandLine line, String pathRelativeProperties, Class<?> executingClass ) throws ExperimentExecutionException {
		return new ExperimentExecutionTemplate(
			SelectParamExperimentFactory.defaultExperimentOrCustom(
				line,
				pathRelativeProperties,
				new FilePathResolver(executingClass)
			)
		);
	}
}
