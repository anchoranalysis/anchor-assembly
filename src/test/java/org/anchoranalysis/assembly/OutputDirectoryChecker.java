/*-
 * #%L
 * Anchor Distribution
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.assembly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Checks the files in the output-directory against expectations. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class OutputDirectoryChecker {

    /**
     * Asserts the expected-files (as well as logExperiment.txt) are found in {@code
     * outputDirectory}.
     *
     * <p>It also checks no other files (that aren't expected) exist.
     *
     * <p>If there are no expected-files, we expect the directory never to have been created.
     *
     * @param outputDirectory the directory to be checked for output files.
     * @param expectedFiles the file-names (or relative-paths to the files, encoding subdirectories
     *     using a forward slash) for <b>all</b> files expected to be outputted.
     * @throws IOException
     */
    public static void assertAsExpected(Path outputDirectory, List<String> expectedFiles)
            throws IOException {

        if (!expectedFiles.isEmpty()) {
            assertWithExpectedOutput(outputDirectory, expectedFiles);
        } else {
            assertNoExpectedOutput(outputDirectory);
        }
    }

    /** Checks when output-files are expected. */
    private static void assertWithExpectedOutput(Path outputDirectory, List<String> expectedFiles)
            throws IOException {
        Set<String> actualFiles = findFilesRecursively(outputDirectory);

        assertTotalCount(actualFiles, expectedFiles);

        assertEachExpectedFileExists(actualFiles, expectedFiles);
    }

    /** Checks when no output files are expected. */
    private static void assertNoExpectedOutput(Path outputDirectory) {
        assertTrue(!Files.exists(outputDirectory));
    }

    /** Asserts the total counts of actual and expected files are identical. */
    private static void assertTotalCount(Set<String> actualFiles, List<String> expectedFiles) {
        // Check the total number of files
        assertEquals(
                expectedFiles.size(),
                actualFiles.size(),
                () ->
                        String.format(
                                "%d files were expected, but a non-identical %d files were found.",
                                expectedFiles.size(), actualFiles.size()));
    }

    /** Asserts each expected-files exists in the output-directory. */
    private static void assertEachExpectedFileExists(
            Set<String> actualFiles, Iterable<String> expectedFiles) {

        for (String expectedFile : expectedFiles) {
            assertTrue(
                    actualFiles.contains(expectedFile),
                    () ->
                            String.format(
                                    "Expected file %s does not exist in the output directory. These files do: %s",
                                    expectedFile, actualFiles));
        }
    }

    /**
     * Lists all files in a directory.
     *
     * @return a set of file-paths (relative to the directory) for each file. If a subdirectory
     *     exists it will be expressed as a relative-path using forward-slashes, but no
     *     backward-slashes.
     * @throws IOException an error occurred reading the files in the directory.
     */
    private static Set<String> findFilesRecursively(Path directory) throws IOException {
        return Files.walk(directory)
                .filter(Files::isRegularFile)
                .map(path -> directory.relativize(path).toString().replace('\\', '/'))
                .collect(Collectors.toSet());
    }
}
