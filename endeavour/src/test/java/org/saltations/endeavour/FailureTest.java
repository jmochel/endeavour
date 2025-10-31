package org.saltations.endeavour;

import java.util.Optional;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.saltations.endeavour.fixture.ReplaceBDDCamelCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void whenMappingThenReturnsThenNewFailureInstance() throws Exception
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
        var outcome = failure.flatMap((CheckedFunction<Long, Result<Long>>) x -> Try.success(x * 3));

        assertThat(outcome)
            .isFailure()
            .hasNoPayload()
            .hasFailureType(((Failure<Long>)failure).getType())
            ;

    }

    @Test
    @Order(40)
    void whenReducingFailureThenCallsFailureFunction()
    {
        Optional<String> result = failure.reduce(
            success -> {
                return "Success path";
            },
            fail -> {
                return "Failure path";
            }
        );

        assertEquals("Failure path", result.get(), "Reduced to 'Failure path'");
    }

    @Test
    @Order(60)
    void whenIfSuccessThenDoesNotTakeAction() throws Exception {
        final StringBuilder resultBuilder = new StringBuilder();
        
        var result = failure.ifSuccess(x -> {
            resultBuilder.append("Processed: ").append(x.get());
            return x; // Return the value to satisfy CheckedConsumer contract
        });
        
        assertEquals("", resultBuilder.toString(), "Action not taken");
        assertSame(failure, result, "Should return same result");
    }

    @Test
    @Order(61)
    void whenIfFailureThenTakesAction() throws Exception {
        final StringBuilder resultBuilder = new StringBuilder();
        
        var result = failure.ifFailure(x -> {
            resultBuilder.append("Processed: ").append(x.getTitle());
            return x; // Return the value to satisfy CheckedConsumer contract
        });
        
        assertTrue(resultBuilder.length() > 0, "Action taken");
        assertSame(failure, result, "Should return same result");
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

    @Test
    @Order(72)
    void whenOrElseGetSupplierThrowsInterruptedExceptionThenReturnsFailureWithInterruptedFlagRestored() throws Exception
    {
        // Create a supplier that throws InterruptedException
        CheckedSupplier<Result<Long>> throwingSupplier = () -> {
            Thread.currentThread().interrupt(); // Simulate interruption
            throw new InterruptedException("Test interruption");
        };

        var outcome = failure.orElseGet(throwingSupplier);

        assertThat(outcome)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
            .hasCause()
            .hasCauseOfType(InterruptedException.class)
            .hasCauseWithMessage("Test interruption");

        // Verify that the interrupted flag was restored
        assertTrue(Thread.interrupted(), "Interrupted flag should be restored");
    }

    @Test
    @Order(73)
    void whenOrElseGetSupplierThrowsRuntimeExceptionThenReturnsFailureWithCause() throws Exception
    {
        RuntimeException testException = new RuntimeException("Test runtime exception");
        CheckedSupplier<Result<Long>> throwingSupplier = () -> {
            throw testException;
        };

        var outcome = failure.orElseGet(throwingSupplier);

        assertThat(outcome)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
            .hasCause()
            .hasCauseOfType(RuntimeException.class)
            .hasCauseWithMessage("Test runtime exception");
    }

    @Test
    @Order(74)
    void whenOrElseGetSupplierThrowsCheckedExceptionThenReturnsFailureWithCause() throws Exception
    {
        Exception testException = new Exception("Test checked exception");
        CheckedSupplier<Result<Long>> throwingSupplier = () -> {
            throw testException;
        };

        var outcome = failure.orElseGet(throwingSupplier);

        assertThat(outcome)
            .isFailure()
            .hasFailureType(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
            .hasCause()
            .hasCauseOfType(Exception.class)
            .hasCauseWithMessage("Test checked exception");
    }

    @Test
    @Order(75)
    void whenOrElseGetSupplierIsNullThenThrowsNullPointerException()
    {
        assertThrows(NullPointerException.class, () -> {
            failure.orElseGet(null);
        });
    }

  
}
