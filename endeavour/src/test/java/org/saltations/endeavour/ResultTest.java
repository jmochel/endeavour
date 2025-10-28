package org.saltations.endeavour;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.saltations.endeavour.fixture.ReplaceBDDCamelCase;
import org.saltations.endeavour.fixture.ResultAssert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.saltations.endeavour.fixture.ResultAssert.assertThat;

/**
 * Validates the functionality of the individual outcome classes and how they are used
 */

@Order(10)
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ResultTest
{
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Attempt {

        @Test
        @Order(1)
        void whenValueProvidedThenReturnsQuantSuccessWithThatValue()
        {
            var result = Try.attempt(() -> 1111L);

            // Using new assertion classes
            ResultAssert.assertThat(result)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue(1111L);
        }

        @Test
        @Order(2)
        void whenSupplierProvidesNullThenReturnsQualSuccess()
        {
            var result = Try.attempt(() -> null);

            // Using new assertion classes
            ResultAssert.assertThat(result)
                .isSuccess()
                .isQualSuccess()
                .hasNoPayload();
        }

        @Test
        @Order(3)
        void whenSupplierThrowsCheckedExceptionThenReturnsFailure()
        {
            var result = Try.attempt(() -> {throw new Exception("Test");});
            
            // Using new assertion classes
            ResultAssert.assertThat(result)
                .isFailure()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
                .hasCause()
                .hasCauseOfType(Exception.class)
                .hasCauseWithMessage("Test");
        }

        @Test
        @Order(4)
        void whenSupplierThrowsRuntimeExceptionThenReturnsFailure()
        {
            var result = Try.attempt(() -> {throw new RuntimeException("Test");});
            
            // Using new assertion classes
            assertThat(result)
                .isFailure()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC_RUNTIME_EXCEPTION)
                .hasCause()
                .hasCauseOfType(RuntimeException.class)
                .hasCauseWithMessage("Test");
        }

        @Test
        @Order(5)
        void whenSupplierThrowsInterruptedExceptionThenReturnsFailureAndRestoresInterrupt()
        {
            var result = Try.attempt(() -> {throw new InterruptedException("Interrupted");});
            
            // Using new assertion classes
            assertThat(result)
                .isFailure()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC_INTERRUPTED_EXCEPTION)
                .hasCause()
                .hasCauseOfType(InterruptedException.class)
                .hasCauseWithMessage("Interrupted");
            
            // Verify interrupt flag was restored
            assertTrue(Thread.currentThread().isInterrupted());
            
            // Clear interrupt flag for other tests
            Thread.interrupted();
        }

        @Test
        @Order(6)
        void whenCheckedSupplierIsNullThenThrowsNullPointerException()
        {
            assertThrows(NullPointerException.class, () -> Try.attempt(null));
        }
    }

    @Nested
    @Order(7)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TransmuteTests {
        private final Result<Long> success = Try.success(1111L);
        private final Result<Long> failure = Try.failure();

        @Test
        @Order(10)
        void whenTransmutingSuccessThenReturnsTransformedValue() {
            // Given a success outcome and a transform function
            var result = success.reduce(
                v -> v * 2,
                f -> 0L
            );

            // Then the result should be the transformed value
            assertEquals(2222L, result);
        }

        @Test
        @Order(20)
        void whenTransmutingFailureThenReturnsTransformedValue() {
            // Given a failure outcome and a transform function
            var result = failure.reduce(
                v -> "Success",
                f -> "Failed"
            );

            // Then the result should be the transformed value
            assertEquals("Failed", result);
        }

        @Test
        @Order(30)
        void whenTransmutingWithNullTransformThenThrowsException() {
            // Given a success outcome and a null transform function
            assertThrows(NullPointerException.class, () -> {
                success.reduce(null, f -> "Failed");
            });
        }

        @Test
        @Order(50)
        void whenTransmutingWithThrowingTransformThenThrowsException() {
            // Given a success outcome and a transform function that throws
            assertThrows(RuntimeException.class, () -> {
                success.reduce(
                    v -> { throw new RuntimeException("Transform failed"); },
                    f -> "Failed"
                );
            });
        }

        @Test
        @Order(60)
        void whenTransmutingWithComplexTransformThenReturnsExpectedResult() {
            // Given a success outcome and a complex transform function
            var result = success.reduce(
                v -> new ComplexResult(v, v * 2, v * 3),
                f -> new ComplexResult(0L, 0L, 0L)
            );

            // Then the result should be the complex transformed value
            assertEquals(1111L, result.value1);
            assertEquals(2222L, result.value2);
            assertEquals(3333L, result.value3);
        }

        private static class ComplexResult {
            final long value1;
            final long value2;
            final long value3;

            ComplexResult(long value1, long value2, long value3) {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
            }
        }
    }

    @Nested
    @Order(8)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ResultsConstructorTest {
        @Test
        @Order(10)
        void whenCreatingNewInstanceThenSucceeds() {
            new Try();
        }
    }
  
}
