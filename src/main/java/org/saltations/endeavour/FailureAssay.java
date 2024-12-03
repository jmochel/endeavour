package org.saltations.endeavour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

/**
 * Represents the breakdown of a specific failure for an operation.
 * <p>
 * This class is used to provide a detailed breakdown of a failure that occurred during an operation.
 * It is used to provide a detailed breakdown of the failure, including the type or category of the failure, the title of the failure,
 * any additional details, the root cause (if any).
 */

@Slf4j
@Data
@Getter
@Setter
@AllArgsConstructor
public class FailureAssay
{
    private FailureType type;
    private String title;
    private String detail;
    private Exception cause;

    public boolean hasCause()
    {
        return cause != null;
    }

    /**
     * The default generic failure type that is used when no other failure type is provided.
     */

    @Getter
    @AllArgsConstructor
    public enum GenericFailureType implements FailureType
    {
        GENERIC("generic-failure", "");

        private final String title;
        private final String template;
    }

    public String getTotalMessage()
    {
        return title + "-" + detail;
    }

    public static Builder of()
    {
        return new Builder();
    }

    public static final class Builder
    {
        // Final values that get passed into the XFail.

        private FailureType type = GenericFailureType.GENERIC;
        private Exception cause;

        // Additional fields input for building

        private Object[] args = new Object[]{};
        private String title;
        private String template;

        private Builder()
        {
        }

        private Builder(FailureAssay initialData)
        {
            this.type = initialData.type;
            this.title = initialData.title;
            this.cause = initialData.cause;
        }

        public Builder type(FailureType type)
        {
            this.type = type;

            if (this.title == null)
            {
                this.title = type.getTitle();
            }

            if (this.template == null)
            {
                this.template = type.getTemplate();
            }

            return this;
        }

        public Builder title(String title)
        {
            this.title = title;
            return this;
        }

        public Builder template(String template)
        {
            this.template = template;
            return this;
        }

        public Builder cause(Exception cause)
        {
            this.cause = cause;
            return this;
        }

        public Builder args(Object...args)
        {
            this.args = args;
            return this;
        }

        public FailureAssay build()
        {
            return new FailureAssay(type, title, MessageFormatter.basicArrayFormat(template, args), cause);
        }
    }
}
