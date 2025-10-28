package org.saltations.endeavour;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.NonNull;

/**
 *
 */

public class Try
{
    /**
     * Attempt to execute the given checked supplier operation and return the result.
     * This method handles checked exceptions by converting them to appropriate failures.
     *
     * @param supplier function that supplies a new value and may throw checked exceptions. <b>Not null</b>.
     *
     * @return populated {@code QuantSuccess} if supplier provides a non null value, {@code QualSuccess} 
     * if supplier provides a null value, or {@code Failure} if supplier throws an exception.
     * InterruptedException is handled specially by restoring the interrupt flag.
     *
     * @param <T> Type of the supplied value
     */

     public static <U> Result<U> attempt(@NonNull CheckedSupplier<U> supplier)
     {
         checkNotNull(supplier, "Supplier function cannot be null");
 
         try
         {
             var value = supplier.get();
             
             return Objects.isNull(value) ? new QualSuccess<>() : new QuantSuccess<>(value);
         }
         catch (InterruptedException ex)
         {
             // restore the interrupted flag
             Thread.currentThread().interrupt();
             return new Failure<>(FailureDescription.of()
                     .type(FailureDescription.GenericFailureType.GENERIC_INTERRUPTED_EXCEPTION)
                     .cause(ex)
                     .build());
         }
         catch (Exception e)
         {
             return switch(e)
             {
                 case RuntimeException ex -> new Failure<>(FailureDescription.of()
                     .type(FailureDescription.GenericFailureType.GENERIC_RUNTIME_EXCEPTION)
                     .cause(ex)
                     .build());
                 case Exception ex -> new Failure<>(FailureDescription.of()
                     .type(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
                     .cause(ex)
                     .build());
             };
         }
     }

    /**
     * Construct a successful result with a Boolean value of true
     *
     * @return An XSuccess result of {@code XResult<XFail,Boolean>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.succeed();
     * }
     * </pre>
     *
     */

    public static Result<Boolean> success()
    {
        return new QuantSuccess<>(Boolean.TRUE);
    }

    /**
     * Construct a successful result with given value
     *
     * @param value the value payload
     * 
     * @param <T> class of the contained success value
     *
     * @return a {@code QuantSuccess} with payload or {@code QualSuccess} if the value is {@code null}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return Try.succeed("Success!");
     * }
     * </pre>
     *
     */

    public static <T> Result<T> success(T value)
    {
        return Objects.nonNull(value) ? new QuantSuccess<>(value) : new QualSuccess<>();
    }

    /**
     * Construct a failed generic result
     *
     * @param <T> class of the contained success value
     *
     * @return a generic {@code Failure} 
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return Try.fail();
     * }
     * </pre>
     */

    public static <T> Result<T> failure()
    {
        var type = FailureDescription.GenericFailureType.GENERIC;

        return new Failure<>(FailureDescription.of()
                .type(type)
                .build());
    }


    /**
     * Construct a failed result with details from the given template and arguments
     *
     * @param template Message template composed using {@link org.slf4j.helpers.MessageFormatter} format strings
     * @param args arguments used to expand the given template
     *
     * @param <T> class of the contained success value
     *
     * @return An Failure result of {@code Failure<XFail,T>} with type {@code XFail.GenericFail.GENERIC} and a
     * detail message derived from the template and arguments
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.failWithDetails("Ouch ! That {}", "hurt a lot.");
     * }
     * </pre>
     */

    public static <T> Result<T> failureWithDetails(String template, Object...args)
    {
        requireNonNull(template, "Failure needs a non-null template");

        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .template(template)
                .args(args)
                .build();

        return new Failure<>(fail);
    }


    /**
     * Construct a failed titled result.
     *
     * @param <T> class of the contained success value
     *
     * @return An Failure result of {@code Failure<XFail,T>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.titledFail("just-plain-dumb");
     * }
     * </pre>
     */
    public static <T> Result<T> titledFailure(String title)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .title(title)
                .build();

        return new Failure<>(fail);
    }

    /**
     * Construct a failed result with title and expanded details
     *
     * @param <T> class of the contained success value
     *
     * @return An Failure result of {@code Failure<XFail,T>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.fail();
     * }
     * </pre>
     */
    public static <T> Result<T> titledFailureWithDetails(String title, String template, Object...args)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .title(title)
                .template(template)
                .args(args)
                .build();

        return new Failure<>(fail);
    }


    public static <T> Result<T> typedFailure(FailureType failureType, Object...args)
    {
        requireNonNull(failureType, "Failure needs a non-null failure type");

        var builder = FailureDescription.of().type(failureType);

        if (failureType.templateParameterCount() == 0 && args.length == 1)
        {
            builder.template((String)args[0]);
        }
        else {
            builder.args(args);
        }

        var fail = builder.build();

        return new Failure<>(fail);
    }


    public static <T> Result<T> typedFailureWithDetails(FailureType failureType, String template, Object...args)
    {
        requireNonNull(failureType, "Failure needs a non-null failure type");

        var fail = FailureDescription.of()
                .type(failureType)
                .template(template)
                .args(args)
                .build();

        return new Failure<>(fail);
    }

    public static <T> Result<T> causedFailure(Exception cause)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .cause(cause)
                .build();

        return new Failure<>(fail);
    }

    public static <T> Result<T> causedFailure(Exception cause, FailureType failureType, Object...args)
    {
        requireNonNull(failureType, "Failure needs a non-null failure type");

        var builder = FailureDescription.of().type(failureType).cause(cause);

        if (failureType.templateParameterCount() == 0 && args.length == 1)
        {
            builder.template((String)args[0]);
        }
        else {
            builder.args(args);
        }

        var fail = builder.build();

        return new Failure<>(fail);
    }

    public static <T> Result<T> causedFailureWithDetails(Exception cause, String template, Object...args)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .cause(cause)
                .template(template)
                .args(args)
                .build();

        return new Failure<>(fail);
    }

}
