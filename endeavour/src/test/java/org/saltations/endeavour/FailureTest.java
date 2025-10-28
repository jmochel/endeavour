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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.saltations.endeavour.fixture.ResultAssert.assertThat;

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
        assertThat(failure)
            .isFailure()
            .hasNoPayload()
            ;
        
        assertEquals(Optional.empty(), failure.opt(), "Empty Optional");
        assertThrows(IllegalStateException.class, () -> failure.get(), "Must throw exception");
    }

    @Test
    @Order(20)
    void whenMappingThenReturnsThenNewFailureInstance()
    {
        var result = failure.map(x -> "999");

        // Then we get the same failure instance and mapping not invoked
        assertThat(result)
            .isFailure()
            .hasNoPayload()
            .hasFailureType(((Failure<Long>)failure).getType())
            ;
    }

    @Test
    @Order(30)
    void whenMappingPayloadThenReturnsNewFailureInstance()
    {
        var outcome = failure.flatMap(x -> Try.success(x * 3));

        assertThat(outcome)
            .isFailure()
            .hasNoPayload()
            .hasFailureType(((Failure<Long>)failure).getType())
            ;

    }

    @Test
    @Order(40)
    void whenReducingFailureThenCallsFailureFunction() throws Throwable
    {
        String result = failure.reduce(
            success -> {
                return "Success path";
            },
            fail -> {
                return "Failure path";
            }
        );

        assertEquals("Failure path", result, "Reduced to 'Failure path'");
    }


    @Test
    @Order(50)
    void whenActInvokedOnFailureThenExecutesFailureAction() {
        final AtomicBoolean failureActionCalled = new AtomicBoolean(false);

        failure.act(res -> {
            if (res instanceof Failure) {
                failureActionCalled.set(true);
            }
        });

        assertTrue(failureActionCalled.get(), "Action for Failure should be called");
    }


    @Test
    @Order(60)
    void whenIfSuccessThenDoesNotTakeAction() {
        final AtomicBoolean applied = new AtomicBoolean(false);
        failure.ifSuccess(x -> applied.getAndSet(true));
        assertFalse(applied.get(), "Action taken");
    }

    @Test
    @Order(61)
    void whenIfFailureThenTakesAction() {
        final AtomicBoolean applied = new AtomicBoolean(false);
        failure.ifFailure(x -> applied.getAndSet(true));
        assertTrue(applied.get(), "Action taken");
    }


    @Test
    @Order(70)
    void whenSupplyingResultThenReturnsSuppliedResult() throws Throwable
    {
        var result = failure.orElse(Try.success(2222L));
        assertThat(result)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(2222L);
    }

    @Test
    @Order(71)
    void whenSupplyingResultOnFailureThenReturnsNewResult() throws Throwable
    {
        var outcome = failure.orElseGet((CheckedSupplier<Result<Long>>) () -> Try.success(2222L));
        assertThat(outcome)
            .isSuccess()
            .isQuantSuccess()
            .hasPayload()
            .hasValue(2222L);
    }

  
}
