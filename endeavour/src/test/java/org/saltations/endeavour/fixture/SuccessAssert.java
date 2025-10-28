package org.saltations.endeavour.fixture;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.saltations.endeavour.QualSuccess;
import org.saltations.endeavour.QuantSuccess;
import org.saltations.endeavour.Success;

/**
 * Custom AssertJ assertions for {@link Success} objects.
 * <p>
 * Provides fluent assertions for testing Success objects (both QuantSuccess and QualSuccess).
 * 
 * @param <T> The type of the success value
 */
public class SuccessAssert<T> extends AbstractAssert<SuccessAssert<T>, Success<T>> {

    public SuccessAssert(Success<T> actual) {
        super(actual, SuccessAssert.class);
    }

    /**
     * Creates a new instance of {@link SuccessAssert}.
     *
     * @param <T> the type of the success value
     * @param actual the actual success to assert
     * @return a new instance of SuccessAssert
     */
    public static <T> SuccessAssert<T> assertThat(Success<T> actual) {
        return new SuccessAssert<>(actual);
    }

    /**
     * Verifies that the success is a QuantSuccess with a payload.
     *
     * @return this assertion object
     */
    public SuccessAssert<T> isQuantSuccess() {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected success to be a QuantSuccess but was <%s>", actual.getClass().getSimpleName());
        }
        return this;
    }

    /**
     * Verifies that the success is a QualSuccess (no payload).
     *
     * @return this assertion object
     */
    public SuccessAssert<T> isQualSuccess() {
        isNotNull();
        if (!(actual instanceof QualSuccess)) {
            failWithMessage("Expected success to be a QualSuccess but was <%s>", actual.getClass().getSimpleName());
        }
        return this;
    }

    /**
     * Verifies that the success has a payload (is a QuantSuccess).
     *
     * @return this assertion object
     */
    public SuccessAssert<T> hasPayload() {
        isNotNull();
        if (!actual.hasPayload()) {
            failWithMessage("Expected success to have a payload but it doesn't");
        }
        return this;
    }

    /**
     * Verifies that the success does not have a payload (is a QualSuccess).
     *
     * @return this assertion object
     */
    public SuccessAssert<T> hasNoPayload() {
        isNotNull();
        if (actual.hasPayload()) {
            failWithMessage("Expected success to not have a payload but it does");
        }
        return this;
    }

    /**
     * Verifies that the success's value equals the expected value.
     * Only works for QuantSuccess results.
     *
     * @param expectedValue the expected value
     * @return this assertion object
     */
    public SuccessAssert<T> hasValue(T expectedValue) {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected success to be a QuantSuccess to check value, but was <%s>", actual.getClass().getSimpleName());
        }
        try {
            T actualValue = actual.get();
            Assertions.assertThat(actualValue).isEqualTo(expectedValue);
        } catch (Throwable e) {
            failWithMessage("Failed to get value from success: %s", e.getMessage());
        }
        return this;
    }

    /**
     * Verifies that the success's value satisfies the given condition.
     * Only works for QuantSuccess results.
     *
     * @param condition the condition to check
     * @return this assertion object
     */
    public SuccessAssert<T> hasValueSatisfying(org.assertj.core.api.Condition<? super T> condition) {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected success to be a QuantSuccess to check value, but was <%s>", actual.getClass().getSimpleName());
        }
        try {
            T actualValue = actual.get();
            Assertions.assertThat(actualValue).is(condition);
        } catch (Throwable e) {
            failWithMessage("Failed to get value from success: %s", e.getMessage());
        }
        return this;
    }

    /**
     * Verifies that the success's value is not null.
     * Only works for QuantSuccess results.
     *
     * @return this assertion object
     */
    public SuccessAssert<T> hasNonNullValue() {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected success to be a QuantSuccess to check value, but was <%s>", actual.getClass().getSimpleName());
        }
        try {
            T actualValue = actual.get();
            Assertions.assertThat(actualValue).isNotNull();
        } catch (Throwable e) {
            failWithMessage("Failed to get value from success: %s", e.getMessage());
        }
        return this;
    }

    /**
     * Verifies that the success's value is null.
     * Only works for QuantSuccess results.
     *
     * @return this assertion object
     */
    public SuccessAssert<T> hasNullValue() {
        isNotNull();
        if (!(actual instanceof QuantSuccess)) {
            failWithMessage("Expected success to be a QuantSuccess to check value, but was <%s>", actual.getClass().getSimpleName());
        }
        try {
            T actualValue = actual.get();
            Assertions.assertThat(actualValue).isNull();
        } catch (Throwable e) {
            failWithMessage("Failed to get value from success: %s", e.getMessage());
        }
        return this;
    }
}
