package org.saltations.endeavour;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.saltations.endeavour.fixture.ReplaceBDDCamelCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
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
public class FailureTest
{
    private final Result<Long> failure = Try.failure();

    @Test
    @Order(1)
    void meetsContract() throws Throwable
    {
        assertEquals(Failure.class, failure.getClass(), "Failure");
        assertThrows(IllegalStateException.class, () -> failure.get(), "Should throw exception");
        assertFalse(failure.hasPayload(), "Has Payload");
        assertThrows(IllegalStateException.class, () -> failure.opt(), "Should throw exception");
    }

    @Test
    @Order(30)
    void whenMappingPayloadToNewPayloadOnSuccessThenReturnsTheExistingFailure()
    {
        var outcome = failure.flatMap(x -> Try.success(x * 3));
        assertEquals(Failure.class, outcome.getClass(), "Failure");
        assertSame(outcome, failure, "Same failure");
    }

    @Test
    @Order(20)
    void whenSupplyingResultOnSuccessThenReturnsTheExistingFailure() throws Throwable
    {
        var outcome = failure.orElse(() -> Try.success(2222L));
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
