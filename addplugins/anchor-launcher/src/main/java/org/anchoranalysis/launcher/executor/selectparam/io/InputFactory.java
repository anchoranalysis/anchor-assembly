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
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.anchoranalysis.core.functional.OptionalUtilities;
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
	
	public static SelectParam<Optional<Path>> pathOrDirectoryOrGlobOrExtension( String[] args ) {
		List<Path> paths = pathFromArgs(args);
		return OptionalUtilities.orFlat(
			checkWildcard(args),			// If it contains a wildcard, assume its a glob
			checkXmlExtension(args),		// If it ends with .xml, assumes it's an input-manager
			checkFileExtension(args),		// If it begins with a period, and no slashes, then assume it's a file extension
			checkDirectory(paths)			// If it's a directory path, then use the directory to find inputs
		).orElseGet( ()->
			new UseListFilesForManager(paths)
		);
	}
	
	private static Optional<SelectParam<Optional<Path>>> checkWildcard( String[] args ) {
		return check(
			anyHas(args, s->s.contains("*")),
			args.length==1,
			() -> new UseAsGlob(args[0]),
			"Only a single wildcard argument is permitted to -i"
		);
	}
	
	private static Optional<SelectParam<Optional<Path>>> checkFileExtension( String[] args ) {
		return check(
			anyHas(args, ExtensionUtilities::isFileExtension),
			allHave(args, ExtensionUtilities::isFileExtension),
			() -> new UseAsExtension(args),
			"If a file-extension (e.g. .png) is specified, all other arguments to -i must also be file-extensions"
		);
	}
	
	private static Optional<SelectParam<Optional<Path>>> checkXmlExtension( String[] args ) {
		return check(
			anyHas(args, ExtensionUtilities::hasXmlExtension),
			args.length==1,
			() -> new CustomManagerFromPath( PathConverter.pathFromArg(args[0]) ),
			"Only a single BeanXML argument is permitted after -i (i.e. with a path with a .xml extension)"
		);
	}
	
	private static Optional<SelectParam<Optional<Path>>> checkDirectory( List<Path> paths ) {
		return check(
			anyHas(paths, p->p.toFile().isDirectory()),		
			paths.size()==1,
			() -> new UseDirectoryForManager(paths.get(0), true),
			"Only a single argument is permitted after -i if it's a directory"
		);
	}
	
	
	private static <T> Optional<T> check( boolean condition1, boolean condition2, Supplier<T> val, String errorMsg) {
		if (condition1) {
			if (condition2) {
				return Optional.of(val.get());
			} else {
				throw new CommandLineException(errorMsg);
			}
		}		
		return Optional.empty();
	}
		
	private static List<Path> pathFromArgs( String[] args ) {
		return Arrays.stream(args)
				.map( PathConverter::pathFromArg )
				.collect( Collectors.toList() );
	}
}
