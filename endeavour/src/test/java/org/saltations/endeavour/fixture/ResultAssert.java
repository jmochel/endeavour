package org.saltations.endeavour.fixture;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.saltations.endeavour.Failure;
import org.saltations.endeavour.FailureType;
import org.saltations.endeavour.QualSuccess;
import org.saltations.endeavour.QuantSuccess;
import org.saltations.endeavour.Result;

/**
 * Custom AssertJ assertions for {@link Result} objects.
 * <p>
 * Provides fluent assertions for testing Result objects and their subtypes.
 * 
 * @param <T> The type of the result value
 */
public class ResultAssert<T> extends AbstractAssert<ResultAssert<T>, Result<T>> {

    public ResultAssert(Result<T> actual) {
        super(actual, ResultAssert.class);
    }

    /**
     * Creates a new instance of {@link ResultAssert}.
     *
     * @param <T> the type of the result value
     * @param actual the actual result to assert
     * @return a new instance of ResultAssert
     */
    public static <T> ResultAssert<T> assertThat(Result<T> actual) {
        return new ResultAssert<>(actual);
    }

    /**
     * Verifies that the result is a success (either QuantSuccess or QualSuccess).
     *
     * @return this assertion object
     */
    public ResultAssert<T> isSuccess() {
        isNotNull();
        if (!actual.hasPayload() && !(actual instanceof QualSuccess)) {
            failWithMessage("Expected result to be a success but was <%s>", actual.getClass().getSimpleName());
        }
        return this;
    }

    /**
     * Verifies that the result is a failure.
     *
     * @return this assertion object
     */
    public ResultAssert<T> isFailure() {
        isNotNull();
        if (!(actual instanceof Failure)) {
            failWithMessage("Expected result to be a failure but was <%s>", actual.getClass().getSimpleName());
        }
        return this;
    }

    /**
     * Verifies that the result is a QuantSuccess with a payload.
     *
     * @return this assertion object
     */
    public ResultAssert<T> isQuantSuccess() {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected result to be a QuantSuccess but was <%s>", actual.getClass().getSimpleName());
        }
        return this;
    }

    /**
     * Verifies that the result is a QualSuccess (no payload).
     *
     * @return this assertion object
     */
    public ResultAssert<T> isQualSuccess() {
        isNotNull();
        if (!(actual instanceof QualSuccess)) {
            failWithMessage("Expected result to be a QualSuccess but was <%s>", actual.getClass().getSimpleName());
        }
        return this;
    }

    /**
     * Verifies that the result has a payload (is a QuantSuccess).
     *
     * @return this assertion object
     */
    public ResultAssert<T> hasPayload() {
        isNotNull();
        if (!actual.hasPayload()) {
            failWithMessage("Expected result to have a payload but it doesn't");
        }
        return this;
    }

    /**
     * Verifies that the result does not have a payload (is a QualSuccess).
     *
     * @return this assertion object
     */
    public ResultAssert<T> hasNoPayload() {
        isNotNull();
        if (actual.hasPayload()) {
            failWithMessage("Expected result to not have a payload but it does");
        }
        return this;
    }

    /**
     * Verifies that the result's value equals the expected value.
     * Only works for QuantSuccess results.
     *
     * @param expectedValue the expected value
     * @return this assertion object
     */
    public ResultAssert<T> hasValue(T expectedValue) {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected result to be a QuantSuccess to check value, but was <%s>", actual.getClass().getSimpleName());
        }
        try {
            T actualValue = actual.get();
            Assertions.assertThat(actualValue).isEqualTo(expectedValue);
        } catch (Throwable e) {
            failWithMessage("Failed to get value from result: %s", e.getMessage());
        }
        return this;
    }

    /**
     * Verifies that the result's value satisfies the given condition.
     * Only works for QuantSuccess results.
     *
     * @param condition the condition to check
     * @return this assertion object
     */
    public ResultAssert<T> hasValueSatisfying(org.assertj.core.api.Condition<? super T> condition) {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected result to be a QuantSuccess to check value, but was <%s>", actual.getClass().getSimpleName());
        }
        try {
            T actualValue = actual.get();
            Assertions.assertThat(actualValue).is(condition);
        } catch (Throwable e) {
            failWithMessage("Failed to get value from result: %s", e.getMessage());
        }
        return this;
    }

    /**
     * Verifies that the result is a failure with the expected failure type.
     *
     * @param expectedType the expected failure type
     * @return this assertion object
     */
    public ResultAssert<T> hasFailureType(FailureType expectedType) {
        isNotNull();
        if (!(actual instanceof Failure)) {
            failWithMessage("Expected result to be a failure to check failure type, but was <%s>", actual.getClass().getSimpleName());
        }
        Failure<T> failure = (Failure<T>) actual;
        Assertions.assertThat(failure.getType()).isEqualTo(expectedType);
        return this;
    }

    /**
     * Verifies that the result is a failure with a cause.
     *
     * @return this assertion object
     */
    public ResultAssert<T> hasCause() {
        isNotNull();
        if (!(actual instanceof Failure)) {
            failWithMessage("Expected result to be a failure to check cause, but was <%s>", actual.getClass().getSimpleName());
        }
        Failure<T> failure = (Failure<T>) actual;
        if (!failure.description().hasCause()) {
            failWithMessage("Expected failure to have a cause but it doesn't");
        }
        return this;
    }

    /**
     * Verifies that the result is a failure without a cause.
     *
     * @return this assertion object
     */
    public ResultAssert<T> hasNoCause() {
        isNotNull();
        if (!(actual instanceof Failure)) {
            failWithMessage("Expected result to be a failure to check cause, but was <%s>", actual.getClass().getSimpleName());
        }
        Failure<T> failure = (Failure<T>) actual;
        if (failure.description().hasCause()) {
            failWithMessage("Expected failure to not have a cause but it does");
        }
        return this;
    }

    /**
     * Verifies that the result is a failure with the expected cause.
     *
     * @param expectedCause the expected cause
     * @return this assertion object
     */
    public ResultAssert<T> hasCause(Exception expectedCause) {
        isNotNull();
        if (!(actual instanceof Failure)) {
            failWithMessage("Expected result to be a failure to check cause, but was <%s>", actual.getClass().getSimpleName());
        }
        Failure<T> failure = (Failure<T>) actual;
        if (!failure.description().hasCause()) {
            failWithMessage("Expected failure to have a cause but it doesn't");
        }
        Assertions.assertThat(failure.getCause()).isEqualTo(expectedCause);
        return this;
    }

    /**
     * Verifies that the result is a failure with a cause of the expected type.
     *
     * @param expectedCauseType the expected cause type
     * @return this assertion object
     */
    public ResultAssert<T> hasCauseOfType(Class<? extends Exception> expectedCauseType) {
        isNotNull();
        if (!(actual instanceof Failure)) {
            failWithMessage("Expected result to be a failure to check cause type, but was <%s>", actual.getClass().getSimpleName());
        }
        Failure<T> failure = (Failure<T>) actual;
        if (!failure.description().hasCause()) {
            failWithMessage("Expected failure to have a cause but it doesn't");
        }
        Assertions.assertThat(failure.getCause()).isInstanceOf(expectedCauseType);
        return this;
    }

    /**
     * Verifies that the result is a failure with a cause containing the expected message.
     *
     * @param expectedMessage the expected message
     * @return this assertion object
     */
    public ResultAssert<T> hasCauseWithMessage(String expectedMessage) {
        isNotNull();
        if (!(actual instanceof Failure)) {
            failWithMessage("Expected result to be a failure to check cause message, but was <%s>", actual.getClass().getSimpleName());
        }
        Failure<T> failure = (Failure<T>) actual;
        if (!failure.description().hasCause()) {
            failWithMessage("Expected failure to have a cause but it doesn't");
        }
        Assertions.assertThat(failure.getCause().getMessage()).contains(expectedMessage);
        return this;
    }
}
