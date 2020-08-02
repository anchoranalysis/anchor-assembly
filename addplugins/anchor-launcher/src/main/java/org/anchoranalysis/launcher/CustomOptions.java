package org.anchoranalysis.launcher;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.Option;

/**
 * Different types of arguments used by Anchor
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
