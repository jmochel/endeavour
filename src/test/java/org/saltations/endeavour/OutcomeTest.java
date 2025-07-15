package org.saltations.endeavour;

import java.util.concurrent.atomic.AtomicBoolean;

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
public class OutcomeTest
{
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Attempting {

        @Test
        @Order(1)
        void whenAttemptingWithSuccessThenReturnsSuccess()
        {
            var result = Outcome.attempt(() -> 1111L);
            assertTrue(result.hasSuccessPayload());
            assertEquals(1111L, result.get(), "Success Value");
        }

        @Test
        @Order(2)
        void whenAttemptingWithExceptionThenReturnsFailure()
        {
            var result = Outcome.attempt(() -> {throw new Exception("Test");});
            assertFalse(result.hasSuccessPayload());
            assertTrue(result.hasFailurePayload());
        }

    }


    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenSuccess {

        private final Outcome<Long> success = Try.succeed(1111L);

        @Test
        @Order(10)
        void whenGettingValueThenReturnsSuccessValue() throws Throwable
        {
            var value = success.get();
            assertEquals(1111L, value, "Success Value");
        }


        @Test
        @Order(20)
        void whenSupplyingOutcomeOnSuccessThenReturnsSuppliedValue() throws Throwable
        {
            var outcome = success.onSuccess(() -> Try.succeed(2222L));
            assertEquals(2222L, outcome.get(), "Success Value");
        }

        @Test
        @Order(30)
        void whenTransformingOutcomeOnSuccessThenReturnsTransformedOutcomeToNewSuccess() throws Throwable
        {
            var outcome = success.onSuccess(x -> Try.succeed(x * 3));
            assertEquals(3333L, outcome.get(), "Transformed Outcome");
        }

        @Test
        @Order(32)
        void whenTransformingOutcomeOnSuccessThenReturnsTransformedOutcomeToNewFailure() throws Throwable
        {
            var outcome = success.onSuccess(x -> Try.fail());
            assertTrue(outcome.hasFailurePayload(), "Now a Failure");
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenTakesAction() throws Throwable
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            success.consumeSuccess(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }


        @Test
        @Order(50)
        void whenSupplyingOutcomeOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = success.onFailure(() -> Try.succeed(2222L));
            assertSame(outcome, success, "Existing Success");
        }

        @Test
        @Order(60)
        void whenTransformingOutcomeOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = success.onFailure(x -> Try.succeed(x.get() * 3));
            assertSame(outcome, success, "Existing Success");
        }


        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesNoAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            success.consumeFailure(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(70)
        void whenTakingActionOnBothThenTakesSuccessAction()
        {
            final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
            final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

            success.consume(x -> appliedForSuccess.getAndSet(true), x -> appliedForFailure.getAndSet(true));

            assertTrue(appliedForSuccess.get(), "Success Action taken");
            assertFalse(appliedForFailure.get(), "Failure Action taken");
        }

        @Test
        @Order(80)
        void whenMappingThenTakesSuccessAction()
        {
            var outcome = success.map(x -> x * 3);

            assertEquals(3333L, outcome.get(), "Mapped Outcome");
        }

        @Test
        @Order(90)
        void whenFlatMappingThenTakesSuccessAction()
        {
            var outcome = success.flatMap(x -> Try.succeed(x * 3));

            assertEquals(3333L, outcome.get(), "Mapped Outcome");
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = success.transform(this::outcomeToString);

            assertEquals("Success", result, "Transformed to 'Success'");
        }

        @Test
        @Order(110)
        void optReturnsOptionalWithValueForSuccess() {
            var opt = success.opt();
            assertTrue(opt.isPresent(), "Optional should be present for Success");
            assertEquals(1111L, opt.get(), "Optional value should match Success value");
        }

        String outcomeToString(Outcome<Long> outcome)
        {
            return switch (outcome)
            {
                case Success out -> "Success";
                case Failure out -> "Failure";
            };
        }

    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenFailure {

        private final Outcome<Long> failure = Try.fail();

        @Test
        @Order(10)
        void whenGettingValueThenThrowsException() throws Throwable
        {
            assertThrows(Exception.class, () -> failure.get(), "Cannot get value from a failure");
        }

        @Test
        @Order(20)
        void whenSupplyingOutcomeOnSuccessThenReturnsTheExistingFailure() throws Throwable
        {
            var outcome = failure.onSuccess(() -> Try.succeed(2222L));
            assertSame(outcome, failure, "Same failure");
        }

        @Test
        @Order(30)
        void whenTransformingOutcomeOnSuccessThenReturnsTheExistingFailure()
        {
            var outcome = failure.onSuccess(x -> Try.succeed(x * 3));
            assertSame(outcome, failure, "Same failure");
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenDoesNotTakeAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.consumeSuccess(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(50)
        void whenSupplyingValueOnFailureThenReturnsNewOutcome() throws Throwable
        {
            var outcome = failure.onFailure(() -> Try.succeed(2222L));
            assertEquals(2222L, outcome.get(),"New Outcome");
        }

        @Test
        @Order(60)
        void whenTransformingOutcomeOnFailureThenReturnsNewOutcome() throws Throwable
        {
            var outcome = failure.onFailure(x -> Try.fail());
            assertNotSame(outcome, failure, "New Outcome");
        }

        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.consumeFailure(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }

        @Test
        @Order(72)
        void whenTakingActionOnFailureThenTakesActionThatThrowsException()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            assertThrows(IllegalArgumentException.class, () -> failure.consumeFailure(x -> {throw new IllegalArgumentException("Test"); }));
        }

        @Test
        @Order(80)
        void whenTakingActionOnBothThenTakesFailureAction()
        {
            final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
            final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

            failure.consume(x -> appliedForSuccess.getAndSet(true), x -> appliedForFailure.getAndSet(true));

            assertFalse(appliedForSuccess.get(), "Success Action taken");
            assertTrue(appliedForFailure.get(), "Failure Action taken");
        }

        @Test
        @Order(80)
        void whenMappingThenTakesSuccessAction()
        {
            var outcome = failure.map(x -> x * 3);

            assertFalse(outcome.hasSuccessPayload(), "Has Success");
            assertTrue(outcome.hasFailurePayload(), "Has Failure");
        }

        @Test
        @Order(90)
        void whenFlatMappingThenTakesSuccessAction()
        {
            var outcome = failure.flatMap(x -> Try.succeed(x * 3));

            assertFalse(outcome.hasSuccessPayload(), "Has Success");
            assertTrue(outcome.hasFailurePayload(), "Has Failure");
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

        String outcomeToString(Outcome<Long> outcome)
        {
            return switch (outcome)
            {
                case Success out -> "Success";
                case Failure out -> "Failure";
            };
        }
    }

    @Nested
    @Order(7)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TransmuteTests {
        private final Outcome<Long> success = Try.succeed(1111L);
        private final Outcome<Long> failure = Try.fail();

        @Test
        @Order(10)
        void whenTransmutingSuccessThenReturnsTransformedValue() {
            // Given a success outcome and a transform function
            var result = success.transform(outcome -> {
                if (outcome.hasSuccessPayload()) {
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
                if (outcome.hasFailurePayload()) {
                    return "Failed";
                }
                return "Success";
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
                if (outcome.hasSuccessPayload()) {
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
    class OutcomesConstructorTest {
        @Test
        @Order(10)
        void whenCreatingNewInstanceThenSucceeds() {
            new Try();
        }
    }

}
