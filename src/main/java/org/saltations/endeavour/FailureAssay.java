package org.saltations.endeavour;

import java.util.Enumeration;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * Represents the breakdown of a specific failure for an operation.
 * <p>
 * This class provides a detailed breakdown of a failure that occurred during an operation. This breakdown includes:
 * <ul>
 *  <li>The type or category of the failure</li>
 *  <li>The title of the failure</li>
 *  <li>Any additional details</li>
 *  <li>The root cause exception (if any)</li>
 * </ul>
 * <p>
 * The failure type is a required field that categorizes the failure. If a null failure type is provided, either through
 * the constructor or the builder, it will automatically default to {@link GenericFailureType#GENERIC}. This ensures
 * that a failure always has a valid type, even when not explicitly specified.
 * <p>
 * Example usage:
 * <pre>
 * // Creating with explicit type
 * var failure = FailureAssay.of()
 *                          .type(MyFailureType.VALIDATION_ERROR)
 *                          .detail("Invalid input")
 *                          .build();
 * 
 * // Creating with null type (defaults to GenericFailureType.GENERIC)
 * var genericFailure = FailureAssay.of()
 *                                 .type(null)
 *                                 .detail("Something went wrong")
 *                                 .build();
 * </pre>
 */

@Slf4j
@Data
@Getter
@Setter
public class FailureAssay
{
    static final String FAILURE_FROM_EXCEPTION = "Failure from exception";
    static final String FAILURE_ASSAY_IS_MEANINGLESS = "This failure was created with no meaningful information.";

    /**
     * The type or category of the failure.
     * <p>
     * While marked as {@link NonNull}, if a null value is provided, it will be automatically
     * converted to {@link GenericFailureType#GENERIC}. This ensures that a failure always
     * has a valid type, even when not explicitly specified.
     */
    @NonNull
    private final FailureType type;
    private String title;
    private String detail;
    private Exception cause;

    /**
     * Creates a new FailureAssay with the specified parameters.
     *
     * @param type   The type of failure. If null, defaults to {@link GenericFailureType#GENERIC}
     * @param title  The title of the failure
     * @param detail Additional details about the failure
     * @param cause  The underlying exception that caused this failure, if any
     */
    public FailureAssay(FailureType type, String title, String detail, Exception cause) {
        this.type = nonNull(type) ? type : GenericFailureType.GENERIC;
        this.title = title;
        this.detail = detail;
        this.cause = cause;
    }

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

    /**
     * Returns a combined message of the title and detail.
     *
     * @return A string containing the title and detail
     */

    public String getTotalMessage()
    {
        return title + "-" + detail;
    }

    /**
     * Creates a new Builder instance for constructing FailureAssay objects.
     *
     * @return A new Builder instance
     */

    public static Builder of()
    {
        return new Builder();
    }

    /**
     * Builder class for constructing FailureAssay objects.
     * <p>
     * This inner class provides a fluent API for building FailureAssay instances.
     * It allows for setting the type, title, detail, template, and cause,
     * and provides a method to build the final FailureAssay object.
     */
    
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

        public Builder(FailureAssay initialData)
        {
            requireNonNull(initialData, "Initial FailureAssay data cannot be null");
            this.type = nonNull(initialData.type) ? initialData.type : GenericFailureType.GENERIC;
            this.title = initialData.title;
            this.detail = initialData.detail;
            this.cause = initialData.cause;
        }

        public Builder type(FailureType type)
        {
            this.type = nonNull(type) ? type : GenericFailureType.GENERIC;

            if (this.title == null) {
                this.title = this.type.getTitle();
            }

            if (this.template == null) {
                this.template = this.type.getTemplate();
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
            requireNonNull(this.type, "FailureType cannot be null");

            if (nonNull(this.detail) && nonNull(this.template)) {
                log.warn("Failure assay was created with both a detail and a template. The detail will be used and the template will be ignored.");
            }

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
         * Counts the number of occurrences of a substring in a string.
         *
         * @param text The string to search in
         * @param sub The substring to count
         * @return The number of occurrences of the substring
         */

        public static int countOccurrences(String text, String sub) {
            int count = 0;
            int index = 0;
    
            while ((index = text.indexOf(sub, index)) != -1) {
                count++;
                index += sub.length();
            }
            return count;
        }


        /**
         * Expands the template using provided arguments and logs any possible mismatches
         * between the needed and provided arguments
         */

        private String expandAsNecessary()
        {
            if (isNull(template)) {
                return "";
            }
            
            // If the template is not provided, use the type's template
            var templateToUse = nonNull(this.template) ? this.template : this.type.getTemplate();

            // Count the number of {} in the template

            var expectedNumOfArgs = countOccurrences(templateToUse, "{}");

            // Initialize args to empty array if null
            if (isNull(args)) {
                args = new Object[0];
            }

            // Pad missing arguments with "NotSupplied"
            if (args.length < expectedNumOfArgs) {
                var paddedArgs = new Object[expectedNumOfArgs];
                System.arraycopy(args, 0, paddedArgs, 0, args.length);
                for (int i = args.length; i < expectedNumOfArgs; i++) {
                    paddedArgs[i] = "NotSupplied";
                }
                args = paddedArgs;
            }

            return MessageFormatter.basicArrayFormat(template, args);
        }

    }
}
