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

public class ResourcesConfig {

    // Which class-loader to use for loading resources
    private ClassLoader classLoader;

    private String versionFooter;
    private String mavenProperties;
    private String usageHeader;
    private String usageFooter;

    public ResourcesConfig(
            ClassLoader classLoader,
            String versionFooter,
            String mavenProperties,
            String usageHeader,
            String usageFooter) {
        super();
        this.classLoader = classLoader;
        this.versionFooter = versionFooter;
        this.mavenProperties = mavenProperties;
        this.usageHeader = usageHeader;
        this.usageFooter = usageFooter;
    }

    public String getVersionFooter() {
        return versionFooter;
    }

    public String getMavenProperties() {
        return mavenProperties;
    }

    public String getUsageHeader() {
        return usageHeader;
    }

    public String getUsageFooter() {
        return usageFooter;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
