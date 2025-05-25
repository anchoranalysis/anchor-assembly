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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Runs tests on a particular task, checking for expected outputted files. */
public abstract class TaskTestBase {

    @Test
    void testTask(@TempDir Path tempDirectory) throws IOException, InterruptedException {
        ExecutorFixture.runAndVerify(taskName(), List.of(), tempDirectory, expectedFiles());
    }

    /**
     * The name of the task to test.
     *
     * @return the task-name.
     */
    protected abstract String taskName();

    /**
     * The files that are expected.
     *
     * <p>Please omit logExperiment.txt as this is always expected.
     *
     * @return the expected-files as a newly-created list.
     */
    protected abstract List<String> expectedFiles();
}
