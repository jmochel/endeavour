package org.saltations.endeavour;

import java.util.regex.Pattern;

/**
 * Represents the type of failure that occurred and provide a default title and any additional
 * template information for the details.
 */

public interface FailureType
{
    String getTitle();
    String getTemplate();

    default long templateParameterCount()
    {
        return Pattern.compile("\\{\\}")
                      .matcher(getTemplate())
                      .results()
                      .count();
    }

}
