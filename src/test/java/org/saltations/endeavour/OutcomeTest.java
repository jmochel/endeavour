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
//@DisplayNameGeneration(ReplaceBDDCamelCase.class)
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

        private final Outcome<FailureAnalysis, Long> success = Outcomes.succeed(1111L);

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
            var outcome = success.ifSuccess(() -> Outcomes.succeed(2222L));
            assertEquals(2222L, outcome.get(), "Success Value");
        }

        @Test
        @Order(30)
        void whenTransformingOutcomeOnSuccessThenReturnsTransformedOutcomeToNewSuccess() throws Throwable
        {
            var outcome = success.ifSuccess(x -> Outcomes.succeed(x * 3));
            assertEquals(3333L, outcome.get(), "Transformed Outcome");
        }

        @Test
        @Order(32)
        void whenTransformingOutcomeOnSuccessThenReturnsTransformedOutcomeToNewFailure() throws Throwable
        {
            var outcome = success.ifSuccess(x -> Outcomes.fail());
            assertTrue(outcome.hasFailurePayload(), "Now a Failure");
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenTakesAction() throws Throwable
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            success.onSuccess(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }


        @Test
        @Order(50)
        void whenSupplyingOutcomeOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = success.ifFailure(() -> Outcomes.succeed(2222L));
            assertSame(outcome, success, "Existing Success");
        }

        @Test
        @Order(60)
        void whenTransformingOutcomeOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var outcome = success.ifFailure(x -> Outcomes.succeed(x.get() * 3));
            assertSame(outcome, success, "Existing Success");
        }


        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesNoAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);

            success.onFailure(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(70)
        void whenTakingActionOnBothThenTakesSuccessAction()
        {
            final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
            final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

            success.on(x -> appliedForSuccess.getAndSet(true), x -> appliedForFailure.getAndSet(true));

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
            var outcome = success.flatMap(x -> Outcomes.succeed(x * 3));

            assertEquals(3333L, outcome.get(), "Mapped Outcome");
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = success.transmute(this::outcomeToString);

            assertEquals("Success", result, "Transformed to 'Success'");
        }

        String outcomeToString(Outcome<FailureAnalysis, Long> outcome)
        {
            return switch (outcome)
            {
                case Success out -> "Success";
                case PartialSuccess out -> "Partial Success";
                case Failure out -> "Failure";
            };
        }

    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenPartialSuccess {

        private final Outcome<FailureAnalysis, Long> partialSuccess = Outcomes.partialSucceed(FailureAnalysis.of().build(),1111L);

        @Test
        @Order(10)
        void whenGettingValueThenReturnsSuccessValue() throws Throwable
        {
            var value = partialSuccess.get();
            assertEquals(1111L, value, "Success Value");
        }

        @Test
        @Order(20)
        void whenSupplyingOutcomeOnSuccessThenReturnsSuppliedValue() throws Throwable
        {
            var outcome = partialSuccess.ifSuccess(() -> Outcomes.succeed(2222L));
            assertEquals(2222L, outcome.get(), "Success Value");
        }

        @Test
        @Order(30)
        void whenTransformingOutcomeOnSuccessThenReturnsTransformedOutcomeToNewSuccess() throws Throwable
        {
            var outcome = partialSuccess.ifSuccess(x -> Outcomes.succeed(x * 3));
            assertEquals(3333L, outcome.get(), "Transformed Outcome");
        }

        @Test
        @Order(32)
        void whenTransformingOutcomeOnSuccessThenReturnsTransformedOutcomeToNewFailure() throws Throwable
        {
            var outcome = partialSuccess.ifSuccess(x -> Outcomes.fail());
            assertTrue(outcome.hasFailurePayload(), "Now a Failure");
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenTakesAction() throws Throwable
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            partialSuccess.onSuccess(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }

        @Test
        @Order(50)
        void whenSupplyingOutcomeOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var value = partialSuccess.ifFailure(() -> Outcomes.succeed(2222L));
            assertSame(value, partialSuccess, "Existing Success");
        }

        @Test
        @Order(60)
        void whenTransformingOutcomeOnFailureThenReturnsExistingSuccess() throws Throwable
        {
            var value = partialSuccess.ifFailure(x -> Outcomes.succeed(x.get() * 3));
            assertSame(value, partialSuccess, "Existing Success");
        }


        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesNoAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            partialSuccess.onFailure(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(80)
        void whenTakingActionOnBothThenTakesBothActions()
        {
            final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
            final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

            partialSuccess.on(x -> appliedForSuccess.getAndSet(true), x -> appliedForFailure.getAndSet(true));

            assertTrue(appliedForSuccess.get(), "Success Action taken");
            assertTrue(appliedForFailure.get(), "Failure Action taken");
        }

        @Test
        @Order(80)
        void whenMappingThenTakesSuccessAction()
        {
            var outcome = partialSuccess.map(x -> x * 3);

            assertTrue(outcome.hasSuccessPayload(), "Has Success");
            assertTrue(outcome.hasFailurePayload(), "Has Failure");
            assertEquals(3333L, outcome.get(), "Mapped Outcome");
        }

        @Test
        @Order(90)
        void whenFlatMappingThenTakesSuccessAction()
        {
            var outcome = partialSuccess.flatMap(x -> Outcomes.succeed(x * 3));

            assertTrue(outcome.hasSuccessPayload(), "Has Success");
            assertFalse(outcome.hasFailurePayload(), "Has Failure");
            assertEquals(3333L, outcome.get(), "Mapped Outcome");
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = partialSuccess.transmute(this::outcomeToString);

            assertEquals("Partial Success", result, "Transformed to 'Success'");
        }

        String outcomeToString(Outcome<FailureAnalysis, Long> outcome)
        {
            return switch (outcome)
            {
                case Success out -> "Success";
                case PartialSuccess out -> "Partial Success";
                case Failure out -> "Failure";
            };
        }
    }

    @Nested
    @Order(6)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenFailure {

        private final Outcome<FailureAnalysis, Long> failure = Outcomes.fail();

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
            var outcome = failure.ifSuccess(() -> Outcomes.succeed(2222L));
            assertSame(outcome, failure, "Same failure");
        }

        @Test
        @Order(30)
        void whenTransformingOutcomeOnSuccessThenReturnsTheExistingFailure()
        {
            var outcome = failure.ifSuccess(x -> Outcomes.succeed(x * 3));
            assertSame(outcome, failure, "Same failure");
        }

        @Test
        @Order(40)
        void whenTakingActionOnSuccessThenDoesNotTakeAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.onSuccess(x -> applied.getAndSet(true));
            assertFalse(applied.get(), "Action taken");
        }

        @Test
        @Order(50)
        void whenSupplyingValueOnFailureThenReturnsNewOutcome() throws Throwable
        {
            var outcome = failure.ifFailure(() -> Outcomes.succeed(2222L));
            assertEquals(2222L, outcome.get(),"New Outcome");
        }

        @Test
        @Order(60)
        void whenTransformingOutcomeOnFailureThenReturnsNewOutcome() throws Throwable
        {
            var outcome = failure.ifFailure(x -> Outcomes.fail());
            assertNotSame(outcome, failure, "New Outcome");
        }

        @Test
        @Order(70)
        void whenTakingActionOnFailureThenTakesAction()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            failure.onFailure(x -> applied.getAndSet(true));
            assertTrue(applied.get(), "Action taken");
        }

        @Test
        @Order(72)
        void whenTakingActionOnFailureThenTakesActionThatThrowsException()
        {
            final AtomicBoolean applied = new AtomicBoolean(false);
            assertThrows(IllegalArgumentException.class, () -> failure.onFailure(x -> {throw new IllegalArgumentException("Test"); }));
        }

        @Test
        @Order(80)
        void whenTakingActionOnBothThenTakesFailureAction()
        {
            final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
            final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

            failure.on(x -> appliedForSuccess.getAndSet(true), x -> appliedForFailure.getAndSet(true));

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
            var outcome = failure.flatMap(x -> Outcomes.succeed(x * 3));

            assertFalse(outcome.hasSuccessPayload(), "Has Success");
            assertTrue(outcome.hasFailurePayload(), "Has Failure");
        }

        @Test
        @Order(100)
        void whenTransformingThenGivesTransformedResult()
        {
            var result = failure.transmute(this::outcomeToString);

            assertEquals("Failure", result, "Transformed to 'Success'");
        }

        String outcomeToString(Outcome<FailureAnalysis, Long> outcome)
        {
            return switch (outcome)
            {
                case Success out -> "Success";
                case PartialSuccess out -> "Partial Success";
                case Failure out -> "Failure";
            };
        }
    }

}
