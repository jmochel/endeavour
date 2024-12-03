package org.saltations.endeavour;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionalBiConsumerTest
{
    @Test
    void biConsumerWithoutException()
    {
        ExceptionalBiConsumer<String, String> biConsumer = (t, u) -> {
            // Implementation that does not throw an exception
        };

        assertDoesNotThrow(() -> biConsumer.accept("Hello", "World"));
    }

    @Test
    void biConsumerWithGenericExceptionCastsItToRuntimeException()
    {
        ExceptionalBiConsumer<String, String> biConsumer = (t, u) -> {
            throw new Exception("Test exception");
        };

        assertThrows(RuntimeException.class, () -> biConsumer.accept("Hello", "World"));
    }

    @Test
    void biConsumerWithRuntimeExceptionLeavesItAsRuntimeException()
    {
        ExceptionalBiConsumer<String, String> biConsumer = (t, u) -> {
            throw new RuntimeException("Test exception");
        };

        var exception = assertThrows(Exception.class, () -> biConsumer.accept("Hello", "World"));
        assertEquals("Test exception", exception.getMessage());
    }
}
