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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.launcher.Launch;
import org.apache.commons.text.RandomStringGenerator;

/** Executes anchor simulating as if it was run in a command-line (CLI) environment. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExecutorFixture {

    /**
     * The path, relative to the project-root, of a directory of image files that can be used as
     * input.
     */
    private static final Path INPUT_DIRECTORY =
            Paths.get("src/test/resources/input/differentSizes");

    /**
     * The path, relative to the project-root, of the configuration files that are bundles with an
     * Anchor distribution and which describe the different possible tasks that can be run with
     * Anchor.
     */
    private static final Path DEFAULT_EXPERIMENT =
            Paths.get("src/main/resources/config/defaultExperiment.xml");

    /**
     * Launch Anchor via a simulated CLI environment, run a task, and check that certain assertions
     * are met.
     *
     * <p>The STDOUT is checked for the string indicating all jobs completed successfully.
     *
     * <p>The STDERR is checked so that is either empty or contains only whitespace.
     *
     * <p>The output-directory is checked so that each file in {@code expectedFiles} is present, and
     * exactly the correct number of expected-files are found in it (and no more).
     *
     * @param taskName the name of the task to run (as per the --task option to Anchor's CLI)
     * @param otherArgs any other arguments that are passed to the CLI (in addition to existing
     *     input, output and task arguments)
     * @param tempDirectory a temporary directory, in which, we create an arbitrary path to an
     *     output-directory.
     * @param expectedFiles filenames (or relative file-paths to the output-directory) that are
     *     expected to be found in the output-directory. Use forward-slashes to encode any
     *     subdirectory e.g. {@code subdir/foo.bar}.
     * @param expectExperimentLog whether an {@code experimentLog.txt} file is expected to be
     *     outputted, or not.
     * @param expectCompletedSuccessfully when true, a message to STDOUT is expected indicating that
     *     all jobs completed successfully. when false, it is not.
     * @throws IOException if the in the output directory cannot be successfully traversed.
     */
    public static void runAndVerify(
            String taskName,
            List<String> otherArgs,
            Path tempDirectory,
            List<String> expectedFiles,
            boolean expectCompletedSuccessfully)
            throws IOException {

        // A non-existing directory is needed, so we cannot use the temporary-directory directly.
        // Instead we point to a (not yet created) subdirectory inside of it.
        Path outputDirectory = tempDirectory.resolve(generateRandomString());

        List<String> command = buildCommand(taskName, otherArgs, outputDirectory);

        String outContent = executeExperimentCaptureOutput(command);

        // The number of jobs should be identical to the number of input files
        assertEquals(
                expectCompletedSuccessfully,
                outContent.contains("All 4 jobs completed successfully."),
                () ->
                        "The presence/absence of a message that all jobs completed successfully, is unexpected.");

        OutputDirectoryChecker.assertAsExpected(outputDirectory, expectedFiles);
    }

    /** Generates a random-string 10 characters long using only the letters a-z. */
    private static String generateRandomString() {
        RandomStringGenerator generator =
                new RandomStringGenerator.Builder().withinRange('a', 'z').build();
        return generator.generate(10);
    }

    /** Execute the experiment and capture the STDOUT. */
    private static String executeExperimentCaptureOutput(List<String> command) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outContent));
            System.setErr(new PrintStream(errContent));

            Launch.mainDefaultExperiment(command.toArray(new String[0]), DEFAULT_EXPERIMENT);
        } finally {
            // Always restore original streams
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        assertTrue(errContent.toString().isBlank(), () -> "STDERR is not blank as expected");

        return outContent.toString();
    }

    /** Builds the command-line arguments that are passed to the CLI for this particular test. */
    private static List<String> buildCommand(
            String taskName, List<String> otherArgs, Path outputDirectory) {
        List<String> command = new ArrayList<>();

        command.add("--input");
        command.add(INPUT_DIRECTORY.toString());

        command.add("--outputOmitExperimentIdentifier");
        command.add(outputDirectory.toString());

        // Disables the open-in-desktop behavior
        command.add("--outputConsoleOnly");

        command.add("--task");
        command.add(taskName);

        command.add(DEFAULT_EXPERIMENT.toString());

        command.addAll(otherArgs);

        return command;
    }
}
