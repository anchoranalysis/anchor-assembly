package org.anchoranalysis.launcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A centralized location for hyperlinks to the Anchor website.
 * 
 * <p>Note that this does not include any links that are mentioned in {@code resources/} that
 * are ultimately exposed to the user.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class AnchorWebsiteLinks {

    /** A URL for showing command-line options relating to outputting. */
    public static final String URL_OUTPUT_OPTIONS = "https://www.anchoranalysis.org/user_guide_command_line.html#output-options";
}
