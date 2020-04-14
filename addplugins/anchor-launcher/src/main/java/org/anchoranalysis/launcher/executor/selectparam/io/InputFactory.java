package org.anchoranalysis.launcher.executor.selectparam.io;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import static org.anchoranalysis.launcher.executor.selectparam.io.PredicateUtilities.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.CustomManagerFromPath;
import org.anchoranalysis.launcher.executor.selectparam.path.PathConverter;


/**
 * SelectParam<Path> factory for inputs
 * 
 * @author owen
 *
 */
public class InputFactory {

	private InputFactory() {}
	
	public static SelectParam<Path> pathOrDirectoryOrGlobOrExtension( String[] args ) {

		// If it contains a wildcard, assume its a glob
		SelectParam<Path> selected = checkWildcard(args);
		if (selected!=null) {
			return selected;
		}
		
		// If it ends with .xml, assumes it's an input-manager
		selected = checkXmlExtension(args);
		if (selected!=null) {
			return selected;
		}
		
		// If it begins with a period, and no slashes, then assume it's a file extension
		selected = checkFileExtension(args);
		if (selected!=null) {
			return selected;
		}
				
		List<Path> paths = pathFromArgs(args);
		
		// If it's a directory path, then use the directory to find inputs
		selected = checkDirectory(paths);
		if (selected!=null) {
			return selected;
		}
				
		// Otherwise assume it's a list of files to open
		return new UseListFilesForManager(paths);
	}
	
	private static SelectParam<Path> checkWildcard( String[] args ) {
		if (anyHas(args, s->s.contains("*"))) {
			if (args.length==1) {
				return new UseAsGlob(args[0]);
			} else {
				throw new CommandLineException("Only a single wildcard argument is permitted to -i");
			}
		}
		return null;
	}
	
	private static SelectParam<Path> checkFileExtension( String[] args ) {
		if (anyHas(args, ExtensionUtilities::isFileExtension)) {
			if (allHave(args, ExtensionUtilities::isFileExtension)) {
				return new UseAsExtension(args);	
			} else {
				throw new CommandLineException("If a file-extension (e.g. .png) is specified, all other arguments to -i must also be file-extensions");
			}
		}
		return null;
	}
	
	private static SelectParam<Path> checkXmlExtension( String[] args ) {
		if (anyHas(args, ExtensionUtilities::hasXmlExtension)) {
			if (args.length==1) {
				return new CustomManagerFromPath( PathConverter.pathFromArg(args[0]) );
			} else {
				throw new CommandLineException("Only a single BeanXML argument is permitted after -i (i.e. with a path with a .xml extension)");
			}
		}
		return null;
	}
	
	private static SelectParam<Path> checkDirectory( List<Path> paths ) {
		if (anyHas(paths, p->p.toFile().isDirectory())) {
			if (paths.size()==1) {
				return new UseDirectoryForManager(paths.get(0), true);
			} else {
				throw new CommandLineException("Only a single argument is permitted after -i if it's a directory");
			}
		}		
		return null;
	}
		
	private static List<Path> pathFromArgs( String[] args ) {
		return Arrays.stream(args)
				.map( PathConverter::pathFromArg )
				.collect( Collectors.toList() );
	}
}
