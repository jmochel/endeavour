package org.saltations.endeavour;

import java.util.Enumeration;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Represents the breakdown of a specific failure for an operation.
 * <p>
 * This class provides a detailed breakdown of a failure that occurred during an operation. This breakdown includes
 * <ul>
 *  <li>The type or category of the failure</li>
 *  <li>The title of the failure</li>
 *  <li>Any additional details</li>
 *  <li>The root cause exception (if any)</li>
 * </ul>
 */

@Slf4j
@Data
@Getter
@Setter
@AllArgsConstructor
public class FailureAssay
{
    static final String FAILURE_FROM_EXCEPTION = "Failure from exception";
    static final String FAILURE_ASSAY_IS_MEANINGLESS = "This failure was created with no meaningful information.";

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
        private String detail;
        private String template;

        private Builder()
        {
        }

        private Builder(FailureAssay initialData)
        {
            this.type = initialData.type;
            this.title = initialData.title;
            this.detail = initialData.detail;
            this.cause = initialData.cause;
        }

        public Builder type(FailureType type)
        {
            this.type = type;

            if (this.title == null) {
                this.title = type.getTitle();
            }

            if (this.template == null) {
                this.template = type.getTemplate();
            }

            return this;
        }

        public Builder title(String title)
        {
            this.title = title;
            return this;
        }

        public Builder detail(String detail)
        {
            this.detail = detail;
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

        public Builder args(Object... args)
        {
            this.args = args;
            return this;
        }

        public FailureAssay build()
        {
            if (isNull(this.title) && isNull(this.detail) && isNull(this.template)) {
                if (isNull(this.cause)) {
                    log.warn("Failure assay was created without a title, detail or template or caused that would allow us to provide information.");
                    this.title = FAILURE_ASSAY_IS_MEANINGLESS;
                    this.detail = "";
                }
                else {
                    log.warn("Failure assay was created with juist an exception and no other information.");
                    this.title = FAILURE_FROM_EXCEPTION;
                    this.detail = this.cause.getMessage();
                }
            }

            var finalDetail = nonNull(this.detail) ? this.detail : expandAsNecessary();
            return new FailureAssay(type, title, finalDetail, cause);
        }

        /**
         * Expands the template using provided arguments and logs any possible mismatches
         * between the needed and provided arguments
         */

        private String expandAsNecessary()
        {
            var expectedNumOfArgs = this.type.templateParameterCount();

            if (args.length != expectedNumOfArgs) {
                log.warn("Template for failure type {} may not be expanded correctly. It expects {} arguments and we provided {} arguments", ((Enum) this.type).name(), expectedNumOfArgs, args.length);
            }

            return MessageFormatter.basicArrayFormat(template, args);
        }
    }
}
