package org.saltations.endeavour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

@Slf4j
@Data
@Getter
@Setter
@AllArgsConstructor
public class Fail
{
    private FailType type;
    private String title;
    private String detail;
    private Exception cause;

    public boolean hasCause()
    {
        return cause != null;
    }

    @Getter
    @AllArgsConstructor
    public enum GenericFail implements FailType
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

        private FailType type = GenericFail.GENERIC;
        private Exception cause;

        // Additional fields input for building

        private Object[] args = new Object[]{};
        private String title;
        private String template;

        private Builder()
        {
        }

        private Builder(Fail initialData)
        {
            this.type = initialData.type;
            this.title = initialData.title;
            this.cause = initialData.cause;
        }

        public Builder type(FailType type)
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

        public Fail build()
        {
            return new Fail(type, title, MessageFormatter.basicArrayFormat(template, args), cause);
        }
    }
}
