package org.saltations.endeavour;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionalConsumerTest
{
    @Test
    void consumerWithoutException()
    {
        ExceptionalConsumer<String> consumer = (t) -> {
            // Implementation that does not throw an exception
        };

        assertDoesNotThrow(() -> consumer.accept("Hello"));
    }

    @Test
    void consumerWithGenericExceptionCastsItToRuntimeException()
    {
        ExceptionalConsumer<String> consumer = (t) -> {
            throw new Exception("Test exception");
        };

        assertThrows(RuntimeException.class, () -> consumer.accept("Hello"));
    }

    @Test
    void consumerWithRuntimeExceptionLeavesItAsRuntimeException()
    {
        ExceptionalBiConsumer<String, String> biConsumer = (t, u) -> {
            throw new RuntimeException("Test exception");
        };

        var exception = assertThrows(Exception.class, () -> biConsumer.accept("Hello", "World"));
        assertEquals("Test exception", exception.getMessage());
    }
}
