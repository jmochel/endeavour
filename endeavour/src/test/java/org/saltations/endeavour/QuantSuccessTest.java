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
    void whenBindingThenReturnsResultOfMappingFunction()
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
    void whenMappingPayloadToNewPayloadOnSuccessThenReturnsNewValue()
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
    void whenMappingPayloadOnSuccessToNullThenReturnsNoValue()
    {
        var outcome = value.flatMap((CheckedFunction<Long, Result<Object>>) x -> Try.failure());
        assertThat(outcome)
            .isFailure();
    }

    @Test
    @Order(40)
    void whenTakingActionOnSuccessThenTakesAction() throws Exception
    {
        final StringBuilder resultBuilder = new StringBuilder();
        
        var result = value.ifSuccess(x -> {
            resultBuilder.append("Processed: ").append(x.get());
            return x; // Return the value to satisfy CheckedConsumer contract
        });
        
        assertEquals("Processed: 1111", resultBuilder.toString(), "Action taken");
        assertSame(value, result, "Should return same result");
    }

    @Test
    @Order(41)
    void whenIfSuccessActionThrowsExceptionThenReturnsFailure()
    {
        Exception testException = new Exception("Test exception");
        
        Result<Long> result = value.ifSuccess(x -> {
            throw testException;
        });
        
        assertThat(result)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
            .hasCause()
            .hasCauseOfType(Exception.class)
            .hasCauseWithMessage("Test exception");
    }

    @Test
    @Order(42)
    void whenIfSuccessActionThrowsRuntimeExceptionThenReturnsFailure()
    {
        RuntimeException testException = new RuntimeException("Test runtime exception");
        
        Result<Long> result = value.ifSuccess(x -> {
            throw testException;
        });
        
        assertThat(result)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
            .hasCause()
            .hasCauseOfType(RuntimeException.class)
            .hasCauseWithMessage("Test runtime exception");
    }

    @Test
    @Order(43)
    void whenIfSuccessActionThrowsInterruptedExceptionThenReturnsFailure()
    {
        InterruptedException testException = new InterruptedException("Test interrupted");
        
        Result<Long> result = value.ifSuccess(x -> {
            throw testException;
        });
        
        assertThat(result)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
            .hasCause()
            .hasCauseOfType(InterruptedException.class)
            .hasCauseWithMessage("Test interrupted");
    }

    @Test
    @Order(44)
    void whenIfSuccessWithNullActionThenThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () -> {
            value.ifSuccess(null);
        });
    }

    @Test
    @Order(45)
    void whenIfSuccessReturnsDifferentResultThenUsesReturnedResult()
    {
        // CheckedConsumer<Success<Long>>.accept returns Success<Long>
        // We need to return a Success<Long>, which Try.success() provides
        // Since Success<Long> extends Result<Long>, the method can return it
        Result<Long> result = value.ifSuccess(x -> {
            // Return a different Success instance - Try.success returns Result but we know it's Success
            Success<Long> newSuccess = (Success<Long>) Try.success(9999L);
            return newSuccess;
        });
        
        assertThat(result)
            .isSuccess()
            .isQuantSuccess()
            .hasValue(9999L);
    }

    @Test
    @Order(60)
    void whenTransformingResultOnFailureThenReturnsExistingSuccess()
    {
        var outcome = value.reduce(
            success -> value,  // Return original result for success cases
            failure -> Try.success(failure.get() * 3)
        );
        assertSame(outcome.get(), value, "Existing Success");
    }


    @Test
    @Order(70)
    void whenTakingActionOnFailureThenTakesNoAction() throws Exception
    {
        final StringBuilder resultBuilder = new StringBuilder();
        
        var result = value.ifFailure(x -> {
            resultBuilder.append("Processed: ").append(x.getTitle());
            return x; // Return the value to satisfy CheckedConsumer contract
        });
        
        assertEquals("", resultBuilder.toString(), "Action not taken");
        assertSame(value, result, "Should return same result");
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
    void whenFlatMappingThenTakesSuccessAction()
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

        assertEquals("Success with value", result.get(), "Transformed to 'Success with value'");
    }

    @Test
    @Order(101)
    void whenReduceSuccessFunctionThrowsExceptionThenReturnsEmptyOptional()
    {
        Exception testException = new Exception("Test exception");
        
        Optional<String> result = value.reduce(
            v -> { throw testException; },
            f -> "Failure"
        );
        
        assertTrue(result.isEmpty(), "Should return empty Optional on exception");
    }

    @Test
    @Order(102)
    void whenReduceReturnsNullThenReturnsEmptyOptional()
    {
        Optional<String> result = value.reduce(
            v -> null,
            f -> "Failure"
        );
        
        assertTrue(result.isEmpty(), "Should return empty Optional when function returns null");
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

    @Test
    @Order(100)
    void whenOrElseGetThenReturnsExistingSuccessWithoutCallingSupplier() throws Exception
    {
        final AtomicBoolean supplierCalled = new AtomicBoolean(false);
        
        CheckedSupplier<Result<Long>> supplier = () -> {
            supplierCalled.set(true);
            return Try.success(9999L);
        };

        var outcome = value.orElseGet(supplier);

        // Should return the existing success without calling the supplier
        assertThat(outcome)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(1111L); // Original value, not supplier value

        assertFalse(supplierCalled.get(), "Supplier should not be called for success");
    }

    @Test
    @Order(101)
    void whenOrElseGetWithNullSupplierThenThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () -> {
            value.orElseGet(null);
        });
    }

}
