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
 * Validates the functionality of the QualSuccess class and how it is used
 */

@Order(10)
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class QualSuccessTest
{
    private final Result<Long> qualSuccess = Try.success(null);

    @Test
    @Order(1)
    void meetsContract() throws Throwable
    {
        assertThat(qualSuccess)
            .isSuccess()
            .isQualSuccess()
            .hasNoPayload();

        assertEquals(Optional.empty(), qualSuccess.opt(), "Empty Optional");
    }

    @Test
    @Order(20)
    void whenMappedToNullThenReturnsQualSuccess() throws Exception
    {
        var result = qualSuccess.map(x -> null);

        assertThat(result)
            .isSuccess()
            .isQualSuccess()
            .hasNoPayload();
        
        assertNull(result.get(), "Should return null");
    }

    @Test
    @Order(21)
    void whenPayloadMappedToNonNullThenReturnsQuantSuccess() throws Exception
    {
        // QualSuccess.map() with non-null result should return QuantSuccess
        var result = qualSuccess.map(x -> 2222L);

        assertThat(result)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(2222L)
        ;
    }

    @Test
    @Order(30)
    void whenBindingThenReturnsResultOfMappingFunction()
    {
        var result1 = qualSuccess.flatMap((CheckedFunction<Long, Result<Long>>) x -> Try.success(x));
        assertThat(result1)
            .isSuccess()
            .isQualSuccess()
            .hasNoPayload()
        ;
        
        var result2 = qualSuccess.flatMap((CheckedFunction<Long, Result<Object>>) x -> Try.failure());
        assertThat(result2)
            .isFailure()
            .hasNoPayload()
        ;
    }

    @Test
    @Order(40)
    void whenReducingThenReturnsSuccessValue()
    {
        var result = qualSuccess.reduce(
            success -> "Eureka",
            failure -> "DangIt"
        );

        assertSame(result.get(), "Eureka", "Success value");
    }

    @Test
    @Order(60)
    void whenTakingActionIfSuccessThenTakesAction() throws Exception
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        qualSuccess.ifSuccess(x -> applied.getAndSet(true));
        assertTrue(applied.get(), "Action taken");
    }


    @Test
    @Order(61)
    void whenTakingActionIfFailureThenTakesNoAction() throws Exception
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        qualSuccess.ifFailure(x -> applied.getAndSet(true));
        assertFalse(applied.get(), "Action taken");
    }



    @Test
    @Order(70)
    void whenOrElseThenReturnsSuppliedResult() throws Throwable
    {
        var result = qualSuccess.orElse(Try.success(2222L));
        assertThat(result)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(2222L);
    }

    @Test
    @Order(70)
    void whenTakingActionOnFailureThenTakesNoAction() throws Exception
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        qualSuccess.ifFailure(x -> applied.getAndSet(true));
        assertFalse(applied.get(), "Action taken");
    }




    @Test
    @Order(100)
    void whenCallingToStringThenReturnsExpectedString()
    {
        assertEquals("Success[No value]", qualSuccess.toString());
    }

    @Test
    @Order(101)
    void whenOrElseGetThenReturnsExistingSuccessWithoutCallingSupplier() throws Exception
    {
        final AtomicBoolean supplierCalled = new AtomicBoolean(false);
        
        CheckedSupplier<Result<Long>> supplier = () -> {
            supplierCalled.set(true);
            return Try.success(9999L);
        };

        var outcome = qualSuccess.orElseGet(supplier);

        // Should return the existing success without calling the supplier
        assertThat(outcome)
            .isSuccess()
            .isQualSuccess()
            .hasNoPayload();

        assertFalse(supplierCalled.get(), "Supplier should not be called for success");
    }

    @Test
    @Order(102)
    void whenOrElseGetWithNullSupplierThenThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () -> {
            qualSuccess.orElseGet(null);
        });
    }

}
