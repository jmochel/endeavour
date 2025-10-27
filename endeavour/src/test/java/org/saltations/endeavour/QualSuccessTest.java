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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates the functionality of the QualSuccess class and how it is used
 */

@Order(10)
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class QualSuccessTest
{
    private final Result<Long> noValue = Try.success(null);

    @Test
    @Order(1)
    void meetsContract() throws Throwable
    {
        assertEquals(QualSuccess.class, noValue.getClass(), "QualSuccess");
        assertEquals(null, noValue.get(), "Payload");
        assertFalse(noValue.hasPayload(), "Has Payload");
        assertEquals(Optional.empty(), noValue.opt(), "Empty Optional");
    }

    @Test
    @Order(25)
    void whenMappingToNullThenReturnsNoValue() throws Throwable
    {
        // QualSuccess.map() with null result should return QualSuccess
        var outcome = noValue.map(x -> null);
        assertEquals(QualSuccess.class, outcome.getClass(), "Should return QualSuccess");
        assertNull(outcome.get(), "Should return null");
    }

    @Test
    @Order(20)
    void whenSupplyingResultOnSuccessThenReturnsSuppliedResult() throws Throwable
    {
        var outcome = noValue.orElse((ExceptionalSupplier<Result<Long>>) () -> Try.success(2222L));
        assertEquals(2222L, outcome.get(), "Success Value");
    }

    @Test
    @Order(35)
    void whenFlatMappingThenCallsMappingFunctionWithNull() throws Throwable
    {
        // QualSuccess.flatMap() calls the mapping function with null (since QualSuccess.get() returns null)
        // The mapping function can ignore the null parameter and return whatever it wants
        var outcome1 = noValue.flatMap(x -> Try.success(777L));
        assertEquals(QuantSuccess.class, outcome1.getClass(), "Should return QuantSuccess");
        assertEquals(777L, outcome1.get(), "Should return the mapped value");
        
        var outcome2 = noValue.flatMap(x -> Try.failure());
        assertEquals(Failure.class, outcome2.getClass(), "Should return Failure");
    }

    @Test
    @Order(30)
    void whenMappingPayloadOnSuccessToNonNullThenReturnsValueWithNewPayload() throws Throwable
    {
        var outcome = noValue.flatMap(x -> Try.success(777L));
        assertEquals(QuantSuccess.class, outcome.getClass(), "QuantSuccess");
        assertEquals(777L, outcome.get(), "Transformed Result");
    }

    @Test
    @Order(31)
    void whenMappingPayloadOnSuccessToNullThenReturnsNoValue() throws Throwable
    {
        var outcome = noValue.flatMap(x -> Try.success(null));

        assertEquals(QualSuccess.class, outcome.getClass(), "QualSuccess");
        assertEquals(null, outcome.get(), "Transformed Result");
    }

    @Test
    @Order(31)
    void whenMappingPayloadOnSuccessThrowExceptionThenReturnsFailure() throws Throwable
    {
        assertThrows(RuntimeException.class, () -> {
            noValue.flatMap(x -> { throw new RuntimeException("Test Exception"); });
        });
    }


    @Test
    @Order(32)
    void whenTransformingResultOnSuccessThenReturnsTransformedResultToNewFailure() throws Throwable
    {
        var outcome = noValue.flatMap(x -> Try.failure());

        assertEquals(Failure.class, outcome.getClass(), "Failure");
        assertThrows(IllegalStateException.class, () -> outcome.get(), "Should throw exception");
    }

    @Test
    @Order(40)
    void whenTakingActionOnSuccessThenTakesAction() throws Throwable
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        noValue.ifSuccess(x -> applied.getAndSet(true));
        assertTrue(applied.get(), "Action taken");
    }


    @Test
    @Order(50)
    void whenSupplyingResultOnFailureThenReturnsExistingSuccess() throws Throwable
    {
        var outcome = noValue.orElseGet((ExceptionalSupplier<Result<Long>>) () -> Try.success(2222L));
        assertSame(outcome, noValue, "Existing Success");
    }

    @Test
    @Order(60)
    void whenTransformingResultOnFailureThenReturnsExistingSuccess() throws Throwable
    {
        var outcome = noValue.reduce(
            success -> noValue,  // Return original result for success cases
            failure -> Try.success(failure.get() * 3)
        );
        assertSame(outcome, noValue, "Existing Success");
    }


    @Test
    @Order(70)
    void whenTakingActionOnFailureThenTakesNoAction()
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        noValue.ifFailure(x -> applied.getAndSet(true));
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
        var outcome = noValue.map(x -> x == null ? 0L : x * 3);

        assertEquals(0L, outcome.get(), "Mapped Result");
    }

    @Test
    @Order(90)
    void whenFlatMappingThenTakesSuccessAction()
    {
        var outcome = noValue.flatMap(x -> Try.success(null));

        assertEquals(null, outcome.get(), "Mapped Result should be null for QualSuccess");
    }

    @Test
    @Order(100)
    void whenTransformingThenGivesTransformedResult()
    {
        var result = noValue.reduce(
            v -> "Success with value",
            f -> "Failure"
        );

        assertEquals("Success with value", result, "Transformed to 'Success with value'");
    }


    @Test
    @Order(100)
    void whenCallingToStringThenReturnsExpectedString()
    {
        assertEquals("Success[No value]", noValue.toString());
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
