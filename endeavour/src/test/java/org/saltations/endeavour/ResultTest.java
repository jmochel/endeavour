package org.saltations.endeavour;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenQuantitativeSuccess {

        private final Result<Long> value = Try.success(1111L);

        @Test
        @Order(1)
        void whenCallingGetThenReturnsPayload() throws Throwable
        {
            assertEquals(1111L, value.get());
        }

        @Test
        @Order(10)
        void whenGettingValueThenReturnsPayload() throws Throwable
        {
            assertThat(value)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue(1111L);
        }

        @Test
        @Order(20)
        void whenSupplyingResultOnSuccessThenReturnsSuppliedPayload() throws Throwable
        {
            var outcome = value.orElse(Try.success(2222L));
            assertThat(outcome)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue(2222L);
        }

        @Test
        @Order(30)
        void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewSuccess() throws Throwable
        {
            var outcome = value.flatMap(x -> Try.success(x * 3));
            assertThat(outcome)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue(3333L);
        }

        @Test
        @Order(32)
        void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewFailure() throws Throwable
        {
            var outcome = value.flatMap(x -> Try.failure());
            assertThat(outcome)
                .isFailure();
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenTakesAction() throws Throwable
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            value.ifSuccess(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }


        @Test
        @Order(60)
        void whenTransformingResultOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = value.reduce(
                success -> value,  // Return original result for success cases
                failure -> Try.success(failure.get() * 3)
            );
            assertSame(outcome, value, "Existing Success");
        }


        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesNoAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            value.ifFailure(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(70)
        void whenTakingActionOnBothThenTakesSuccessAction()
        {
            final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
            final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

            value.act(x -> {
                switch (x)
                {
                    case Failure<Long> out -> appliedForFailure.getAndSet(true);
                    case Success<Long> out -> appliedForSuccess.getAndSet(true);
                }
            });

            assertTrue(appliedForSuccess.get(), "Success Action taken");
            assertFalse(appliedForFailure.get(), "Failure Action taken");
        }

        @Test
        @Order(80)
        void whenMappingThenTakesSuccessAction()
        {
            var outcome = value.map(x -> x * 3);

            assertThat(outcome)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue(3333L);
        }

        @Test
        @Order(90)
        void whenFlatMappingThenTakesSuccessAction()
        {
            var outcome = value.flatMap(x -> Try.success(x * 3));

            assertThat(outcome)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue(3333L);
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = value.reduce(
                v -> "Success with value",
                f -> "Failure"
            );

            assertEquals("Success with value", result, "Transformed to 'Success with value'");
        }

        @Test
        @Order(110)
        void optReturnsOptionalWithValueForSuccess() {
            var opt = value.opt();
            assertTrue(opt.isPresent(), "Optional should be present for Success");
            assertEquals(1111L, opt.get(), "Optional value should match Success value");
        }

        String outcomeToString(Result<Long> outcome)
        {
            return switch (outcome)
            {
                case QuantSuccess<Long> out -> "Success with value";
                case QualSuccess<Long> out -> "Success with no value";
                case Failure<Long> out -> "Failure";
            };
        }

    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenFailure {

        private final Result<Long> failure = Try.failure();

        @Test
        @Order(10)
        void whenGettingPayloadThenThrowsException() throws Throwable
        {
            assertThat(failure)
                .isFailure()
                .hasNoPayload();
            assertThrows(IllegalStateException.class, () -> failure.get(),"Must throw exception");
        }

        @Test
        @Order(30)
        void whenTransformingResultOnSuccessThenReturnsTheExistingFailure()
        {
            var outcome = failure.flatMap(x -> Try.success(x * 3));
            assertSame(outcome, failure, "Same failure");
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenDoesNotTakeAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.ifSuccess(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(60)
        void whenTransformingResultOnFailureThenReturnsNewResult() throws Throwable
        {
            var outcome = failure.reduce(
                success -> Try.success(success),
                failure -> Try.failure()
            );
            assertNotSame(outcome, failure, "New Result");
        }

        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.ifFailure(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }

        @Test
        @Order(72)
        void whenTakingActionOnFailureThenTakesActionThatThrowsException()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            assertThrows(IllegalArgumentException.class, () -> failure.ifFailure(x -> {throw new IllegalArgumentException("Test"); }));
        }

        @Test
        @Order(80)
        void whenTakingActionOnBothThenTakesFailureAction()
        {
            final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
            final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

            failure.act(x -> {
              switch (x)
              {
                  case Failure<Long> out -> appliedForFailure.getAndSet(true);
                  case Success<Long> out -> appliedForSuccess.getAndSet(true);
              }
            });

            assertFalse(appliedForSuccess.get(), "Success Action taken");
            assertTrue(appliedForFailure.get(), "Failure Action taken");
        }

        @Test
        @Order(80)
        void whenMappingThenTakesSuccessAction()
        {
            var outcome = failure.map(x -> x * 3);

            assertThat(outcome)
                .isFailure()
                .hasNoPayload();
        }

        @Test
        @Order(90)
        void whenFlatMappingThenTakesSuccessAction()
        {
            Function<Long,Result<Long>> mapping = (x) -> { return Try.success( 3L); };

            var outcome = failure.flatMap(mapping);
            assertThat(outcome)
                .isFailure()
                .hasNoPayload();
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = failure.reduce(
                v -> "Success with value",
                f -> "Failure"
            );

            assertEquals("Failure", result, "Transformed to 'Failure'");
        }

        String outcomeToString(Result<Long> outcome)
        {
            return switch (outcome)
            {
                case QuantSuccess<Long> out -> "Success with value";
                case QualSuccess<Long> out -> "Success with no value";
                case Failure<Long> out -> "Failure";
            };
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

    @Nested
    @Order(9)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CheckedSupplierMethodsTest {
        
        @Nested
        @Order(1)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class givenSuccessWithCheckedSupplier {
            
            private final Result<Long> success = Try.success(1111L);
            
            @Test
            @Order(10)
            void whenOrElseWithResultThenReturnsSuppliedResult() {
                // Given a success and an alternate result
                Result<Long> alternateResult = Try.success(2222L);
                
                // When calling orElse with the alternate result
                var result = success.orElse(alternateResult);
                
                // Then the result should be the supplied result
                assertThat(result)
                    .isSuccess()
                    .isQuantSuccess()
                    .hasPayload()
                    .hasValue(2222L);
            }
            
            @Test
            @Order(20)
            void whenOrElseWithNullResultThenThrowsNullPointerException() {
                // Given a success and a null alternate result
                
                // When calling orElse with null
                // Then it should throw NullPointerException
                assertThrows(NullPointerException.class, () -> success.orElse((Result<Long>) null));
            }
            
            @Test
            @Order(30)
            void whenOrElseGetWithCheckedSupplierThenReturnsOriginalSuccess() {
                // Given a success and a CheckedSupplier
                CheckedSupplier<Result<Long>> supplier = () -> Try.success(2222L);
                
                // When calling orElseGet with the CheckedSupplier
                var result = success.orElseGet((CheckedSupplier<Result<Long>>) supplier);
                
                // Then the result should be the original success (supplier not called)
                assertSame(result, success);
                assertThat(result)
                    .isSuccess()
                    .isQuantSuccess()
                    .hasPayload()
                    .hasValue(1111L);
            }
            
            @Test
            @Order(40)
            void whenOrElseWithNullResultThenThrowsException() {
                // Given a success and a null result
                
                // When calling orElse with null result
                // Then it should throw NullPointerException
                assertThrows(NullPointerException.class, () -> success.orElse((Result<Long>) null));
            }
            
            @Test
            @Order(50)
            void whenOrElseGetWithNullCheckedSupplierThenThrowsException() {
                // Given a success and a null CheckedSupplier
                CheckedSupplier<Result<Long>> supplier = null;
                
                // When calling orElseGet with null supplier
                // Then it should throw NullPointerException
                assertThrows(NullPointerException.class, () -> success.orElseGet(supplier));
            }
        }
        
        @Nested
        @Order(2)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class GivenQualSuccessWithCheckedSupplier {
            
            private final Result<Long> qualSuccess = Try.success(null);
            
            @Test
            @Order(10)
            void whenOrElseWithResultThenReturnsSuppliedResult() {
                // Given a QualSuccess and an alternate result
                Result<Long> alternateResult = Try.success(2222L);
                
                // When calling orElse with the alternate result
                var result = qualSuccess.orElse(alternateResult);
                
                // Then the result should be the supplied result
                assertThat(result)
                    .isSuccess()
                    .isQuantSuccess()
                    .hasPayload()
                    .hasValue(2222L);
            }
            
            @Test
            @Order(20)
            void whenOrElseGetWithCheckedSupplierThenReturnsOriginalQualSuccess() {
                // Given a QualSuccess and a CheckedSupplier
                CheckedSupplier<Result<Long>> supplier = () -> Try.success(2222L);
                
                // When calling orElseGet with the CheckedSupplier
                var result = qualSuccess.orElseGet(supplier);
                
                // Then the result should be the original QualSuccess (supplier not called)
                assertSame(result, qualSuccess);
                assertThat(result)
                    .isSuccess()
                    .isQualSuccess()
                    .hasNoPayload();
            }
        }
        
        @Nested
        @Order(3)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class GivenFailureWithCheckedSupplier {
            
            private final Result<Long> failure = Try.failure();


            
            @Test
            @Order(20)
            void whenOrElseGetWithCheckedSupplierThenReturnsSuppliedResult() {
                // Given a failure and a CheckedSupplier that returns a new result
                CheckedSupplier<Result<Long>> supplier = () -> Try.success(2222L);
                
                // When calling orElseGet with the CheckedSupplier
                var result = failure.orElseGet(supplier);
                
                // Then the result should be the supplied result
                assertThat(result)
                    .isSuccess()
                    .isQuantSuccess()
                    .hasPayload()
                    .hasValue(2222L);
            }
            
            @Test
            @Order(30)
            void whenOrElseGetWithCheckedSupplierThatThrowsThenReturnsFailure() {
                // Given a failure and a CheckedSupplier that throws an exception
                CheckedSupplier<Result<Long>> supplier = () -> {
                    throw new Exception("CheckedSupplier failed");
                };
                
                // When calling orElseGet with the CheckedSupplier
                var result = failure.orElseGet(supplier);
                
                // Then the result should be a failure
                assertThat(result)
                    .isFailure()
                    .hasNoPayload();
            }
            
            @Test
            @Order(40)
            void whenOrElseWithNullResultThenThrowsException() {
                // Given a failure and a null result
                
                // When calling orElse with null result
                // Then it should throw NullPointerException
                assertThrows(NullPointerException.class, () -> failure.orElse((Result<Long>) null));
            }
            
            @Test
            @Order(50)
            void whenOrElseGetWithNullCheckedSupplierThenThrowsException() {
                // Given a failure and a null CheckedSupplier
                CheckedSupplier<Result<Long>> supplier = null;
                
                // When calling orElseGet with null supplier
                // Then it should throw NullPointerException
                assertThrows(NullPointerException.class, () -> failure.orElseGet(supplier));
            }
        }
        
        @Nested
        @Order(4)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class OrElseWithResult {
            
            @Test
            @Order(10)
            void whenOrElseWithSuccessResultThenReturnsSuppliedResult() {
                // Given a success and an alternate success result
                var success = Try.success(1111L);
                var alternateResult = Try.success(2222L);
                
                // When calling orElse with the alternate result
                var result = success.orElse(alternateResult);
                
                // Then the result should be the alternate result
                assertThat(result)
                    .isSuccess()
                    .isQuantSuccess()
                    .hasPayload()
                    .hasValue(2222L);
            }
            
            @Test
            @Order(20)
            void whenOrElseWithFailureResultThenReturnsSuppliedFailure() {
                // Given a success and an alternate failure result
                var success = Try.success(1111L);
                var alternateResult = Try.<Long>failure();
                
                // When calling orElse with the alternate failure
                var result = success.orElse(alternateResult);
                
                // Then the result should be the alternate failure
                assertThat(result)
                    .isFailure()
                    .hasNoPayload();
            }
            
            @Test
            @Order(30)
            void whenOrElseWithNullResultThenThrowsNullPointerException() {
                // Given a success and a null alternate result
                var success = Try.success(1111L);
                
                // When calling orElse with null
                // Then it should throw NullPointerException
                assertThrows(NullPointerException.class, () -> success.orElse((Result<Long>) null));
            }
        }
    }

}
