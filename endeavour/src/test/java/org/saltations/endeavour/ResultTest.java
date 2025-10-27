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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    class Attempting {

        @Test
        @Order(1)
        void whenAttemptProvidesValueThenReturnsValue()
        {
            var result = Try.attempt(() -> 1111L);

            assertEquals(QuantSuccess.class, result.getClass());
            assertEquals(1111L, result.get(), "Success Value");
        }

        @Test
        @Order(2)
        void whenAttemptProvidesNullThenReturnsNoValue()
        {
            var result = Try.attempt(() -> null);

            assertEquals(QualSuccess.class, result.getClass());
            assertFalse(result.hasPayload());
        }

        @Test
        @Order(3)
        void whenAttemptThrowsExceptionThenReturnsFailure()
        {
            var result = Try.attempt(() -> {throw new Exception("Test");});
            assertFalse(result.hasPayload());
        }

        @Test
        @Order(4)
        void whenAttemptIsNullThenReturnsFailure()
        {
            assertThrows(NullPointerException.class, () -> Try.attempt(null));
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenValue {

        private final Result<Long> value = Try.success(1111L);

        @Test
        @Order(10)
        void whenGettingValueThenReturnsPayload() throws Throwable
        {
            var value = this.value.get();
            assertEquals(1111L, value, "Success Value");
        }

        @Test
        @Order(20)
        void whenSupplyingResultOnSuccessThenReturnsSuppliedPayload() throws Throwable
        {
            var outcome = value.orElse(() -> Try.success(2222L));
            assertEquals(2222L, outcome.get(), "Success Value");
        }

        @Test
        @Order(30)
        void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewSuccess() throws Throwable
        {
            var outcome = value.flatMap(x -> Try.success(x * 3));
            assertEquals(3333L, outcome.get(), "Transformed Result");
        }

        @Test
        @Order(32)
        void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewFailure() throws Throwable
        {
            var outcome = value.flatMap(x -> Try.failure());
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
        @Order(50)
        void whenSupplyingResultOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = value.orElseGet(() -> Try.success(2222L));
            assertSame(outcome, value, "Existing Success");
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

            assertEquals(3333L, outcome.get(), "Mapped Result");
        }

        @Test
        @Order(90)
        void whenFlatMappingThenTakesSuccessAction()
        {
            var outcome = value.flatMap(x -> Try.success(x * 3));

            assertEquals(3333L, outcome.get(), "Mapped Result");
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
            assertEquals(Failure.class, failure.getClass(),"Must be a Failure");
            assertFalse(failure.hasPayload(),"Must not have a payload");
            assertThrows(IllegalStateException.class, () -> failure.get(),"Must throw exception");
        }

        @Test
        @Order(20)
        void whenSupplyingResultOnSuccessThenReturnsTheExistingFailure() throws Throwable
        {
            var outcome = failure.orElse(() -> Try.success(2222L));
            assertSame(outcome, failure, "Same failure");
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
        @Order(50)
        void whenSupplyingValueOnFailureThenReturnsNewResult() throws Throwable
        {
            var outcome = failure.orElseGet(() -> Try.success(2222L));
            assertEquals(2222L, outcome.get(),"New Result");
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

            assertFalse(outcome.hasPayload(), "Has Success");
        }

        @Test
        @Order(90)
        void whenFlatMappingThenTakesSuccessAction()
        {
            Function<Long,Result<Long>> mapping = (x) -> { return Try.success( 3L); };

            var outcome = failure.flatMap(mapping);
            assertFalse(outcome.hasPayload(), "Should not have payload for failure");
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

        @Test
        @Order(110)
        void optReturnsEmptyOptionalForFailure() {
            assertThrows(IllegalStateException.class, () -> failure.opt(), "Should throw exception when trying to get optional from failure");
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

}
