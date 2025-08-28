package org.saltations.endeavour;

import static java.util.Objects.requireNonNull;

/**
 * A utility class for creating outcomes of operations.
 */

public class Outcomes
{
    /**
     * Construct a successful result with a Boolean value of true
     *
     * @param <SV> class of the contained success value
     *
     * @return An XSuccess result of {@code XResult<Boolean>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.succeed();
     * }
     * </pre>
     *
     */

    public static <SV> Outcome<SV> succeed()
    {
        return (Outcome<SV>) new Success<>(Boolean.TRUE);
    }

    /**
     * Construct a successful result with given value
     *
     * @param <SV> class of the contained success value
     *
     * @return An XSuccess result of {@code XResult<SV>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.succeed("Success!");
     * }
     * </pre>
     *
     */
    public static <SV> Outcome<SV> succeed(SV value)
    {
        requireNonNull(value, "Result must have a non-null value to return");

        return new Success(value);
    }



    /**
     * Construct a failed generic result
     *
     * @param <SV> class of the contained success value
     *
     * @return An Failure result of {@code Failure<SV>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.fail();
     * }
     * </pre>
     */
    public static <SV> Outcome<SV> fail()
    {
        var type = FailureDescription.GenericFailureType.GENERIC;

        return new Failure(FailureDescription.of()
                .type(type)
                .build());
    }


    /**
     * Construct a failed result with details from the given template and arguments
     *
     * @param template Message template composed using {@link org.slf4j.helpers.MessageFormatter} format strings
     * @param args arguments used to expand the given template
     *
     * @param <SV> class of the contained success value
     *
     * @return An Failure result of {@code Failure<SV>} with type {@code XFail.GenericFail.GENERIC} and a
     * detail message derived from the template and arguments
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.failWithDetails("Ouch ! That {}", "hurt a lot.");
     * }
     * </pre>
     */

    public static <SV> Outcome<SV> failWithDetails(String template, Object...args)
    {
        requireNonNull(template, "Failure needs a non-null template");

        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .template(template)
                .args(args)
                .build();

        return new Failure(fail);
    }


    /**
     * Construct a failed titled result.
     *
     * @param <SV> class of the contained success value
     *
     * @return An Failure result of {@code Failure<SV>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.titledFail("just-plain-dumb");
     * }
     * </pre>
     */
    public static <SV> Outcome<SV> titledFail(String title)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .title(title)
                .build();

        return new Failure(fail);
    }


    /**
     * Construct a failed result with title and expanded details
     *
     * @param <SV> class of the contained success value
     *
     * @return An Failure result of {@code Failure<SV>}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *   return XResults.fail();
     * }
     * </pre>
     */
    public static <SV> Outcome<SV> titledFailWithDetails(String title, String template, Object...args)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .title(title)
                .template(template)
                .args(args)
                .build();

        return new Failure(fail);
    }


    public static <SV> Outcome<SV> typedFail(FailureType failureType, Object...args)
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

        return new Failure(fail);
    }


    public static <SV> Outcome<SV> typedFailWithDetails(FailureType failureType, String template, Object...args)
    {
        requireNonNull(failureType, "Failure needs a non-null failure type");

        var fail = FailureDescription.of()
                .type(failureType)
                .template(template)
                .args(args)
                .build();

        return new Failure(fail);
    }

    public static <SV> Outcome<SV> causedFail(Exception cause)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .cause(cause)
                .build();

        return new Failure(fail);
    }

    public static <SV> Outcome<SV> causedFail(Exception cause, FailureType failureType, Object...args)
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

        return new Failure(fail);
    }

    public static <SV> Outcome<SV> causedFailWithDetails(Exception cause, String template, Object...args)
    {
        var failureType = FailureDescription.GenericFailureType.GENERIC;

        var fail = FailureDescription.of()
                .type(failureType)
                .cause(cause)
                .template(template)
                .args(args)
                .build();

        return new Failure(fail);
    }
}
