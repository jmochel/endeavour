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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class ValueTest
{
    private final Result<Long> value = Try.success(1111L);
 

    @Test
    @Order(1)
    void meetsContract() throws Throwable
    {
        assertEquals(Value.class, value.getClass(), "Value");
        assertEquals(1111L, value.get(), "Payload");
        assertTrue(value.hasPayload(), "Has Payload");
        assertEquals(Optional.of(1111L), value.opt(), "Optional");
    }

    @Test
    @Order(25)
    void whenMappingToNullThenReturnsNoValue() throws Throwable
    {
        // Value.map() with null result should return NoValue
        var outcome = value.map(x -> null);
        assertEquals(NoValue.class, outcome.getClass(), "Should return NoValue");
        assertNull(outcome.get(), "Should return null");
    }

    @Test
    @Order(20)
    void whenSupplyingResultOnSuccessThenReturnsSuppliersPayload() throws Throwable
    {
        var outcome = value.orElse(() -> Try.success(2222L));
        assertEquals(2222L, outcome.get(), "Success Value");
    }

    @Test
    @Order(35)
    void whenFlatMappingThenCallsMappingFunctionWithValue() throws Throwable
    {
        // Value.flatMap() calls mapping.apply(value) and returns the result
        var outcome1 = value.flatMap(x -> Try.success(x * 2));
        assertEquals(Value.class, outcome1.getClass(), "Should return Value");
        assertEquals(2222L, outcome1.get(), "Should return doubled value");
        
        var outcome2 = value.flatMap(x -> Try.failure());
        assertEquals(Failure.class, outcome2.getClass(), "Should return Failure");
    }

    @Test
    @Order(30)
    void whenMappingPayloadToNewPayloadOnSuccessThenReturnsNewValue() throws Throwable
    {
        var outcome = value.flatMap(x -> Try.success(x * 3));

        assertEquals(Value.class, outcome.getClass(), "Must be a Value");
        assertEquals(3333L, outcome.get(), "Transformed Result");
    }
   
    @Test
    @Order(32)
    void whenMappingPayloadOnSuccessToNullThenReturnsNoValue() throws Throwable
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

    @Test
    @Order(100)
    void whenCallingToStringThenReturnsExpectedString()
    {
        assertEquals("Success[1111]", value.toString());
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
