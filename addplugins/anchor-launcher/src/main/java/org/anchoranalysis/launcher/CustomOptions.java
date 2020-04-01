package org.anchoranalysis.launcher;

import org.apache.commons.cli.Option;

/**
 * Different types of arguments used by Anchor
 * 
 * @author owen
 *
 */
class CustomOptions {

	public static Option multipleArguments(String optionName, String dscr) {
		Option optionInput = new Option(optionName, true, dscr);
		optionInput.setArgs(Option.UNLIMITED_VALUES);
		return optionInput;		
	}
	
	public static Option optionalSingleArgument(String optionName, String dscr) {
		Option option = new Option(optionName, true, dscr);
		option.setOptionalArg(true);
		return option;	
	}
	
	public static Option requiredSingleArgument(String optionName, String dscr) {
		return new Option(optionName, true, dscr);
	}
	
}
