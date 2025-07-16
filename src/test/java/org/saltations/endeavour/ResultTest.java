package org.saltations.endeavour;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

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
        void whenAttemptingWithSuccessThenReturnsSuccess()
        {
            var result = Result.attempt(() -> 1111L);
            assertTrue(result.hasPayload());
            assertEquals(1111L, result.get(), "Success Value");
        }

        @Test
        @Order(2)
        void whenAttemptingWithExceptionThenReturnsFailure()
        {
            var result = Result.attempt(() -> {throw new Exception("Test");});
            assertFalse(result.hasPayload());
        }

    }


    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenValue {

        private final Result<Long> value = Try.succeed(1111L);

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
            var outcome = value.onSuccess(() -> Try.succeed(2222L));
            assertEquals(2222L, outcome.get(), "Success Value");
        }

        @Test
        @Order(30)
        void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewSuccess() throws Throwable
        {
            var outcome = value.onSuccess(x -> Try.succeed(x * 3));
            assertEquals(3333L, outcome.get(), "Transformed Result");
        }

        @Test
        @Order(32)
        void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewFailure() throws Throwable
        {
            var outcome = value.onSuccess(x -> Try.fail());
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenTakesAction() throws Throwable
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            value.actOnSuccess(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }


        @Test
        @Order(50)
        void whenSupplyingResultOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = value.onFailure(() -> Try.succeed(2222L));
            assertSame(outcome, value, "Existing Success");
        }

        @Test
        @Order(60)
        void whenTransformingResultOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = value.onFailure(x -> Try.succeed(x.get() * 3));
            assertSame(outcome, value, "Existing Success");
        }


        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesNoAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            value.actOnFailure(x -> applied.getAndSet(true));
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
                    case Failure out -> appliedForFailure.getAndSet(true);
                    case Success out -> appliedForSuccess.getAndSet(true);
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
            var outcome = value.flatMap(x -> Try.succeed(x * 3));

            assertEquals(3333L, outcome.get(), "Mapped Result");
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = value.transform(this::outcomeToString);

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
                case Value out -> "Success with value";
                case NoValue out -> "Success with no value";
                case Failure out -> "Failure";
            };
        }

    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenFailure {

        private final Result<Long> failure = Try.fail();

        @Test
        @Order(10)
        void whenGettingValueThenThrowsException() throws Throwable
        {
            assertThrows(Exception.class, () -> failure.get(), "Cannot get value from a failure");
        }

        @Test
        @Order(20)
        void whenSupplyingResultOnSuccessThenReturnsTheExistingFailure() throws Throwable
        {
            var outcome = failure.onSuccess(() -> Try.succeed(2222L));
            assertSame(outcome, failure, "Same failure");
        }

        @Test
        @Order(30)
        void whenTransformingResultOnSuccessThenReturnsTheExistingFailure()
        {
            var outcome = failure.onSuccess(x -> Try.succeed(x * 3));
            assertSame(outcome, failure, "Same failure");
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenDoesNotTakeAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.actOnSuccess(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(50)
        void whenSupplyingValueOnFailureThenReturnsNewResult() throws Throwable
        {
            var outcome = failure.onFailure(() -> Try.succeed(2222L));
            assertEquals(2222L, outcome.get(),"New Result");
        }

        @Test
        @Order(60)
        void whenTransformingResultOnFailureThenReturnsNewResult() throws Throwable
        {
            var outcome = failure.onFailure(x -> Try.fail());
            assertNotSame(outcome, failure, "New Result");
        }

        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.actOnFailure(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }

        @Test
        @Order(72)
        void whenTakingActionOnFailureThenTakesActionThatThrowsException()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            assertThrows(IllegalArgumentException.class, () -> failure.actOnFailure(x -> {throw new IllegalArgumentException("Test"); }));
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
                  case Failure out -> appliedForFailure.getAndSet(true);
                  case Success out -> appliedForSuccess.getAndSet(true);
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
            Function<Long,Result<Long>> mapping = (x) -> { return Try.succeed( 3L); };

            var outcome = failure.flatMap(mapping);
            assertTrue(outcome.hasPayload(), "Has Success");
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = failure.transform(this::outcomeToString);

            assertEquals("Failure", result, "Transformed to 'Success'");
        }

        @Test
        @Order(110)
        void optReturnsEmptyOptionalForFailure() {
            var opt = failure.opt();
            assertTrue(opt.isEmpty(), "Optional should be empty for Failure");
        }

        String outcomeToString(Result<Long> outcome)
        {
            return switch (outcome)
            {
                case Value out -> "Success with value";
                case NoValue out -> "Success with no value";
                case Failure out -> "Failure";
            };
        }
    }

    @Nested
    @Order(7)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TransmuteTests {
        private final Result<Long> success = Try.succeed(1111L);
        private final Result<Long> failure = Try.fail();

        @Test
        @Order(10)
        void whenTransmutingSuccessThenReturnsTransformedValue() {
            // Given a success outcome and a transform function
            var result = success.transform(outcome -> {
                if (outcome.hasPayload()) {
                    return outcome.get() * 2;
                }
                return 0L;
            });

            // Then the result should be the transformed value
            assertEquals(2222L, result);
        }

        @Test
        @Order(20)
        void whenTransmutingFailureThenReturnsTransformedValue() {
            // Given a failure outcome and a transform function
            var result = failure.transform(outcome -> {

                return switch (outcome)
                {
                    case Value out -> "Success";
                    case NoValue out -> "Success";
                    case Failure out -> "Failed";
                };
            });

            // Then the result should be the transformed value
            assertEquals("Failed", result);
        }

        @Test
        @Order(30)
        void whenTransmutingWithNullTransformThenThrowsException() {
            // Given a success outcome and a null transform function
            assertThrows(NullPointerException.class, () -> {
                success.transform(null);
            });
        }

        @Test
        @Order(50)
        void whenTransmutingWithThrowingTransformThenThrowsException() {
            // Given a success outcome and a transform function that throws
            assertThrows(RuntimeException.class, () -> {
                success.transform(outcome -> {
                    throw new RuntimeException("Transform failed");
                });
            });
        }

        @Test
        @Order(60)
        void whenTransmutingWithComplexTransformThenReturnsExpectedResult() {
            // Given a success outcome and a complex transform function
            var result = success.transform(outcome -> {
                if (outcome.hasPayload()) {
                    var value = outcome.get();
                    return new ComplexResult(value, value * 2, value * 3);
                }
                return new ComplexResult(0L, 0L, 0L);
            });

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
