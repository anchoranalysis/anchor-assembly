package org.anchoranalysis.launcher.config;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

public class HelpConfig {

    /**
     * What the application command is described as in the help message e.g.&nbsp;anchor or
     * anchorGUI
     *
     * @return a word describing the application command (for the help message)
     */
    private String commandName;

    /**
     * What the application argument is described as in the help message
     * e.g.&nbsp;experimentFile.xml
     *
     * @return a word describing the application arguments (for the help message)
     */
    private String firstArgument;

    public HelpConfig(String commandName, String firstArgument) {
        super();
        this.commandName = commandName;
        this.firstArgument = firstArgument;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getFirstArgument() {
        return firstArgument;
    }
}
