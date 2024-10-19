package org.saltations.endeavour;

import java.util.regex.Pattern;

public interface FailType
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
