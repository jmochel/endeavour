package org.saltations.endeavour;

import java.util.regex.Pattern;

/**
 * Represents the type (category) of a failure .
 * <p>
 * Supplies a default title and any additional template information for the details.
 * The template uses the {@link org.slf4j.helpers.MessageFormatter} format for the templates.
 * basically what this means is you use {@code {}} to indicate where a parameter needs to be replaced
 * with an argument and make sure you have the arguments in order for the template.
 * Everything else is handled automagically.
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
