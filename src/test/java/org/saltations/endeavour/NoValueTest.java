package org.saltations.endeavour;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates the functionality of the individual outcome classes and how they are used
 */

@Order(10)
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class NoValueTest
{
    private final Result<Long> noValue = Try.success(null);

    @Test
    @Order(1)
    void meetsContract() throws Throwable
    {
        assertEquals(NoValue.class, noValue.getClass(), "NoValue");
        assertEquals(null, noValue.get(), "Payload");
        assertFalse(noValue.hasPayload(), "Has Payload");
        assertEquals(Optional.empty(), noValue.opt(), "Empty Optional");
    }

    @Test
    @Order(20)
    void whenSupplyingResultOnSuccessThenReturnsSuppliedResult() throws Throwable
    {
        var outcome = noValue.supplyOnSuccess(() -> Try.success(2222L));
        assertEquals(2222L, outcome.get(), "Success Value");
    }

    @Test
    @Order(30)
    void whenMappingPayloadOnSuccessToNonNullThenReturnsValueWithNewPayload() throws Throwable
    {
        var outcome = noValue.mapOnSuccess(x -> Try.success(777L));
        assertEquals(Value.class, outcome.getClass(), "Value");
        assertEquals(777L, outcome.get(), "Transformed Result");
    }

    @Test
    @Order(31)
    void whenMappingPayloadOnSuccessToNullThenReturnsNoValue() throws Throwable
    {
        var outcome = noValue.mapOnSuccess(x -> Try.success(null));

        assertEquals(NoValue.class, outcome.getClass(), "NoValue");
        assertEquals(null, outcome.get(), "Transformed Result");
    }

    @Test
    @Order(31)
    void whenMappingPayloadOnSuccessThrowExceptionThenReturnsFailure() throws Throwable
    {
        var outcome = noValue.mapOnSuccess(x -> { throw new RuntimeException("Test Exception"); });
        
        assertEquals(NoValue.class, outcome.getClass(), "NoValue");
        assertEquals(null, outcome.get(), "Transformed Result");
    }


    @Test
    @Order(32)
    void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewFailure() throws Throwable
    {
        var outcome = noValue.mapOnSuccess(x -> Try.failure());

        assertEquals(Failure.class, outcome.getClass(), "Failure");
        assertEquals(null, outcome.get(), "Transformed Result");
    }

    @Test
    @Order(40)
    void whenTakingActionOnSuccessThenTakesAction() throws Throwable
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        noValue.actOnSuccess(x -> applied.getAndSet(true));
        assertTrue(applied.get(), "Action taken");
    }


    @Test
    @Order(50)
    void whenSupplyingResultOnFailureThenReturnsExistingSuccess() throws Throwable
    {
        var outcome = noValue.supplyOnFailure(() -> Try.success(2222L));
        assertSame(outcome, noValue, "Existing Success");
    }

    @Test
    @Order(60)
    void whenTransformingResultOnFailureThenReturnsExistingSuccess() throws Throwable
    {
        var outcome = noValue.mapOnFailure(x -> Try.success(x.get() * 3));
        assertSame(outcome, noValue, "Existing Success");
    }


    @Test
    @Order(70)
    void whenTakingActionOnFailureThenTakesNoAction()
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        noValue.actOnFailure(x -> applied.getAndSet(true));
        assertFalse(applied.get(), "Action taken");
    }

    @Test
    @Order(70)
    void whenTakingActionOnBothThenTakesSuccessAction()
    {
        final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
        final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

        noValue.act(x -> {
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
        var outcome = noValue.map(x -> x * 3);

        assertEquals(3333L, outcome.get(), "Mapped Result");
    }

    @Test
    @Order(90)
    void whenFlatMappingThenTakesSuccessAction()
    {
        var outcome = noValue.flatMap(x -> Try.success(x * 3));

        assertEquals(3333L, outcome.get(), "Mapped Result");
    }

    @Test
    @Order(100)
    void whenTransformingThenGivesTransformedResult()
    {
        var result = noValue.reduce(this::outcomeToString);

        assertEquals("Success with value", result, "Transformed to 'Success with value'");
    }


    String outcomeToString(Result<Long> outcome)
    {
        return switch (outcome)
        {
            case Value<Long> out -> "Success with value";
            case NoValue<Long> out -> "Success with no value";
            case Failure<Long> out -> "Failure";
        };
    }

}
