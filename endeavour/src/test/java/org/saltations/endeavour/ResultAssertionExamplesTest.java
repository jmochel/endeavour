package org.saltations.endeavour;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.ClassOrderer;
import org.saltations.endeavour.fixture.ReplaceBDDCamelCase;
import org.saltations.endeavour.fixture.ResultAssert;
import org.saltations.endeavour.fixture.SuccessAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class demonstrating the usage of custom AssertJ assertions for Result objects.
 */
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ResultAssertionExamplesTest {

    @Test
    @Order(1)
    void demonstrateQuantSuccessAssertions() {
        // Given a successful result with a value
        Result<Long> result = Try.success(42L);

        // Then we can use fluent assertions
        ResultAssert.assertThat(result)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(42L);
    }

    @Test
    @Order(2)
    void demonstrateQualSuccessAssertions() {
        // Given a successful result without a value
        Result<Long> result = Try.attempt(() -> null);

        // Then we can use fluent assertions
        ResultAssert.assertThat(result)
            .isSuccess()
            .isQualSuccess()
            .hasNoPayload();
    }

    @Test
    @Order(3)
    void demonstrateFailureAssertions() {
        // Given a failed result
        Result<Long> result = Try.attempt(() -> {throw new RuntimeException("Test error");});

        // Then we can use fluent assertions
        ResultAssert.assertThat(result)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_RUNTIME_EXCEPTION)
            .hasCause()
            .hasCauseOfType(RuntimeException.class)
            .hasCauseWithMessage("Test error");
    }

    @Test
    @Order(4)
    void demonstrateSuccessSpecificAssertions() {
        // Given a successful result
        Success<Long> success = new QuantSuccess<>(100L);

        // Then we can use success-specific assertions
        SuccessAssert.assertThat(success)
            .isQuantSuccess()
            .hasPayload()
            .hasValue(100L)
            .hasNonNullValue();
    }

    @Test
    @Order(5)
    void demonstrateFailureSpecificAssertions() {
        // Given a failed result
        Result<Long> result = Try.attempt(() -> {throw new IllegalArgumentException("Invalid input");});

        // Then we can use failure-specific assertions
        ResultAssert.assertThat(result)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_RUNTIME_EXCEPTION)
            .hasCause()
            .hasCauseOfType(IllegalArgumentException.class)
            .hasCauseWithMessage("Invalid input");
    }

    @Test
    @Order(6)
    void demonstrateInterruptedExceptionHandling() {
        // Given a result that throws InterruptedException
        Result<Long> result = Try.attempt(() -> {throw new InterruptedException("Interrupted");});

        // Then we can verify the interrupt handling
        ResultAssert.assertThat(result)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_INTERRUPTED_EXCEPTION)
            .hasCause()
            .hasCauseOfType(InterruptedException.class)
            .hasCauseWithMessage("Interrupted");

        // Verify interrupt flag was restored
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
        
        // Clear interrupt flag for other tests
        Thread.interrupted();
    }
}
