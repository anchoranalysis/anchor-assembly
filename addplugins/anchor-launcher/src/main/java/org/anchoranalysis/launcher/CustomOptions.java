package org.anchoranalysis.launcher;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.apache.commons.cli.Option;

/**
 * Different types of arguments used by Anchor
 * 
 * @author Owen Feehan
 *
 */
class CustomOptions {
	
	private CustomOptions() {
	}

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
