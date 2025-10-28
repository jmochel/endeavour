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
import static org.saltations.endeavour.fixture.ResultAssert.assertThat;

/**
 * Validates the functionality of the QuantSuccess class and how it is used
 */

@Order(10)
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class QuantSuccessTest
{
    private final Result<Long> value = Try.success(1111L);
 

    @Test
    @Order(1)
    void meetsContract() throws Throwable
    {
        assertThat(value)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(1111L);

        assertEquals(Optional.of(1111L), value.opt(), "Optional");
    }

    @Test
    @Order(20)
    void whenMappedToNullThenReturnsQualSuccess() throws Exception
    {
        // QuantSuccess.map() with null result should return QualSuccess
        var result = value.map(x -> null);

        assertThat(result)
            .isSuccess()
            .isQualSuccess()
            .hasNoPayload();

        assertEquals(QualSuccess.class, result.getClass(), "Should return QualSuccess");
        assertNull(result.get(), "Should return null");
    }

    @Test
    @Order(21)
    void whenMappedThenReturnsQuantSuccess() throws Exception
    {
        // QuantSuccess.map() with non-null result should return QuantSuccess
        var result = value.map(x -> 2 * x);

        assertThat(result)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(2222L);
    }

    @Test
    @Order(30)
    void whenBindingThenReturnsResultOfMappingFunction() throws Exception
    {
        // QuantSuccess.flatMap() calls mapping.apply(value) and returns the result
        var result1 = value.flatMap((CheckedFunction<Long, Result<Long>>) x -> Try.success(x * 2));
        assertThat(result1)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(2222L);
        
        var outcome2 = value.flatMap((CheckedFunction<Long, Result<Object>>) x -> Try.failure());
        assertThat(outcome2)
            .isFailure();
    }


    @Test
    @Order(20)
    void whenSupplyingResultOnSuccessThenReturnsSuppliersPayload() throws Throwable
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
    void whenMappingPayloadToNewPayloadOnSuccessThenReturnsNewValue() throws Exception
    {
        var outcome = value.flatMap((CheckedFunction<Long, Result<Long>>) x -> Try.success(x * 3));

        assertThat(outcome)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(3333L);
    }
   
    @Test
    @Order(32)
    void whenMappingPayloadOnSuccessToNullThenReturnsNoValue() throws Exception
    {
        var outcome = value.flatMap((CheckedFunction<Long, Result<Object>>) x -> Try.failure());
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
    void whenTakingActionOnBothThenTakesSuccessAction() throws Exception
    {
        final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
        final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

        value.act(payload -> {
            // For QuantSuccess, the payload is the actual value
            appliedForSuccess.getAndSet(true);
        });

        assertTrue(appliedForSuccess.get(), "Success Action taken");
        assertFalse(appliedForFailure.get(), "Failure Action taken");
    }

    @Test
    @Order(80)
    void whenMappingThenTakesSuccessAction() throws Exception
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
    void whenFlatMappingThenTakesSuccessAction() throws Exception
    {
        var outcome = value.flatMap((CheckedFunction<Long, Result<Long>>) x -> Try.success(x * 3));

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
            case QuantSuccess<Long> out -> "Success with value";
            case QualSuccess<Long> out -> "Success with no value";
            case Failure<Long> out -> "Failure";
        };
    }

}
