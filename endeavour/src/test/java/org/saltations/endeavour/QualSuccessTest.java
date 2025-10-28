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
    void whenMappedToNullThenReturnsQualSuccess() throws Throwable
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
    void whenPayloadMappedToNonNullThenReturnsQuantSuccess() throws Throwable
    {
        // QualSuccess.map() with null result should return QualSuccess
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
    void whenBindingThenReturnsResultOfMappingFunction() throws Throwable
    {
        var result1 = qualSuccess.flatMap(x -> Try.success(x));
        assertThat(result1)
            .isSuccess()
            .isQualSuccess()
            .hasNoPayload()
        ;
        
        var result2 = qualSuccess.flatMap(x -> Try.failure());
        assertThat(result2)
            .isFailure()
            .hasNoPayload()
            ;
    }

    @Test
    @Order(40)
    void whenReducingThenReturnsSuccessValue() throws Throwable
    {
        var result = qualSuccess.reduce(
            success -> "Eureka",
            failure -> "DangIt"
        );

        assertSame(result, "Eureka", "Success value");
    }

    @Test
    @Order(50)
    void whenTakingActionThenTakesSuccessAction()
    {
        final AtomicBoolean appliedForFailure = new AtomicBoolean(false);
        final AtomicBoolean appliedForSuccess = new AtomicBoolean(false);

        qualSuccess.act(x -> {
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
    @Order(60)
    void whenTakingActionIfSuccessThenTakesAction() throws Throwable
    {
        final AtomicBoolean applied = new AtomicBoolean(false);

        qualSuccess.ifSuccess(x -> applied.getAndSet(true));
        assertTrue(applied.get(), "Action taken");
    }


    @Test
    @Order(61)
    void whenTakingActionIfFailureThenTakesNoAction() throws Throwable
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
    void whenTakingActionOnFailureThenTakesNoAction()
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

}
