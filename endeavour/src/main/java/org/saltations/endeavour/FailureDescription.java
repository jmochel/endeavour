package org.saltations.endeavour;

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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that describes a failure with details about what went wrong.
 * <p>
 * This class provides a fluent builder API for constructing failure descriptions with various attributes
 * such as type, title, detail, cause, and template arguments.
 * <p>
 * Example usage:
 * <pre>{@code
 * var failure = FailureAnalysis.of()
 *     .type(FailureType.GENERIC)
 *     .title("Operation failed")
 *     .detail("Failed to process request")
 *     .build();
 * }</pre>
 * <p>
 * For generic failures:
 * <pre>{@code
 * var genericFailure = FailureAnalysis.of()
 *     .type(FailureType.GENERIC)
 *     .build();
 * }</pre>
 */

@Slf4j
@Data
@Getter
@Setter
public class FailureDescription
{
    static final String FAILURE_FROM_EXCEPTION = "Failure from exception";
    static final String MEANINGLESS_FAILURE_ANALYSIS = "This failure was created with no meaningful information.";

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
     * Creates a new FailureAnalysis with the specified parameters.
     *
     * @param type   The type of failure. If null, defaults to {@link GenericFailureType#GENERIC}
     * @param title  The title of the failure
     * @param detail Additional details about the failure
     * @param cause  The underlying exception that caused this failure, if any
     */
    public FailureDescription(FailureType type, String title, String detail, Exception cause) {
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
        GENERIC("generic-failure", ""),
        GENERIC_EXCEPTION("generic-checked-exception-failure", ""),
        GENERIC_INTERRUPTED_EXCEPTION("generic-interrupted-exception-failure", ""),
        GENERIC_RUNTIME_EXCEPTION("generic-runtime-exception-failure", "")
        ;

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
     * Creates a new Builder instance for constructing FailureAnalysis objects.
     *
     * @return A new Builder instance
     */

    public static Builder of()
    {
        return new Builder();
    }

    /**
     * Builder class for constructing FailureAnalysis objects.
     * <p>
     * This inner class provides a fluent API for building FailureAnalysis instances.
     * It allows setting various attributes like type, title, detail, cause, and template arguments,
     * and provides a method to build the final FailureAnalysis object.
     */
    
    public static final class Builder
    {
        // Final values that get passed into the XFail.

        private FailureType type;
        private Exception cause;

        // Additional fields input for building

        private Object[] args = new Object[]{};
        private String title;
        private String detail;
        private String template;

        private Builder()
        {
        }

        public Builder(FailureDescription initialData)
        {
            requireNonNull(initialData, "Initial FailureAnalysis data cannot be null");
            this.type = initialData.type;
            this.title = initialData.title;
            this.detail = initialData.detail;
            this.cause = initialData.cause;
        }


        /**
         * Sets the type of the failure. 
         * <p>If no title or template is provided, the type's title and template will be used.
         * 
         * @param type The type of the failure.
         * 
         * @return The builder instance.
         */

        public Builder type(FailureType type)
        {
            this.type = type;
            return this;
        }

        /**
         * Sets the title of the failure. 
         * 
         * @param title The title of the failure.
         * 
         * @return The builder instance.
         */
        public Builder title(String title)
        {
            this.title = title;
            return this;
        }

        /**
         * Sets the detail of the failure.
         * 
         * @param detail The detail of the failure.
         * 
         * @return The builder instance.
         */

        public Builder detail(String detail)
        {
            this.detail = detail;
            return this;
        }

        /**
         * Sets the template of the failure message.
         * <p>Uses {@link MessageFormatter} to expand the template with the provided arguments  
         * .
         * @param template The template of the failure.
         * 
         * @return The builder instance.
         */

        public Builder template(String template)
        {
            this.template = template;
            return this;
        }

        /**
         * Sets the cause of the failure.
         * 
         * @param cause The cause of the failure.
         * 
         * @return The builder instance.
         */

        public Builder cause(Exception cause)
        {
            this.cause = cause;
            return this;
        }


        /**
         * Sets the arguments to be used in expanding the template.
         * 
         * @param args The arguments of the failure.
         * 
         * @return The builder instance.
         */
        public Builder args(Object... args)
        {
            this.args = args;
            return this;
        }

        public FailureDescription build()
        {
            // If args are not provided, we use an empty array

            Object[] tempArgs = nonNull(this.args) ? this.args : new Object[0];
            
           // determine what has been overridden to simplify the later decision code

            var typeProvided = nonNull(this.type);
            var titleProvided = nonNull(this.title);
            var templateProvided = nonNull(this.template);
            var detailProvided = nonNull(this.detail);
            var causeProvided = nonNull(this.cause);

            if (causeProvided && (!typeProvided && !titleProvided && !templateProvided && !detailProvided) ) {
                log.warn("Failure analysis was created with just an exception and no other information.");
            }

             
            // Type is either provided (always wins) or defaulted to GENERIC or GENERIC_EXCEPTION

            this.type = typeProvided ? this.type : (causeProvided ? GenericFailureType.GENERIC_EXCEPTION : GenericFailureType.GENERIC);

            // Template can be provided (always wins) or derived from Type

            this.template = templateProvided ? this.template : this.type.getTemplate();

            // Detail can be provided (always wins) or Derived from provided template (always wins) or the Cause Message

            if (!detailProvided) {

                if (templateProvided) 
                {
                    this.detail = expandAsNecessary(this.template, tempArgs);
                } 
                else 
                {
                    if (causeProvided) 
                    {
                        this.detail = nonNull(this.cause.getMessage()) ? this.cause.getMessage() : "";
                    } 
                    else {
                        this.detail = expandAsNecessary(this.template, tempArgs);
                    }
                }
            }

            // Title comes from the provided title, then from the type
            
            this.title = titleProvided ? this.title : this.type.getTitle();

            return new FailureDescription(this.type, this.title, this.detail, this.cause);
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

        private String expandAsNecessary(String inTemplate, Object[] inArgs)
        {
            if (isNull(inTemplate)) {
                return "";
            }

            // Initialize args to empty array if null

            if (isNull(inArgs)) {
                inArgs = new Object[0];
            }
            
            // Count the number of {} in the template
            var expectedNumOfArgs = countOccurrences(inTemplate, "{}");

            if (expectedNumOfArgs != inArgs.length) {
                log.warn("The number of arguments provided ({}) does not match the number of arguments expected ({})", inArgs.length, expectedNumOfArgs);
            }
            
            // Create a list from the array and pad with nulls if needed
            var augmentedArgs = new ArrayList<>(Arrays.asList(inArgs));
            while (augmentedArgs.size() < expectedNumOfArgs) {
                augmentedArgs.add("NotSupplied"); 
            }

            return MessageFormatter.basicArrayFormat(inTemplate, augmentedArgs.toArray());
        }
    }
}
