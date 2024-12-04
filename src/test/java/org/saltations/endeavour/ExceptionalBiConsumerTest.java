package org.saltations.endeavour;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@Order(40)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ExceptionalBiConsumerTest
{
    @Test
    @Order(1)
    void whenNoExceptionIsThrownThenExceptionalBiConsumerConsumes()
    {
        var exceptionalBiConsumer = new ExceptionalBiConsumer<String, String>() {

            @Override
            public void consumeIt(String s, String s2) throws Exception
            {
                return;
            }
        };

        assertDoesNotThrow(() -> exceptionalBiConsumer.accept("Hello", "World"));
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionIsThrownThenExceptionalBiConsumerThrowsUntouchedRuntimeException()
    {
        var exceptionalBiConsumer = new ExceptionalBiConsumer<String, String>() {
            @Override
            public void consumeIt(String s, String s2) throws Exception
            {
                throw new Exception("Test exception");
            }
        };

        assertThrows(RuntimeException.class, () -> exceptionalBiConsumer.accept("Hello", "World"));
    }

    @Test
    @Order(3)
    void whenCheckedExceptionIsThrownThenExceptionalBiConsumerThrowsWrappedRuntimeException()
    {
        var exceptionalBiConsumer = new ExceptionalBiConsumer<String, String>() {
            @Override
            public void consumeIt(String s, String s2) throws Exception
            {
                throw new RuntimeException("Test exception");
            }
        };

        var exception = assertThrows(Exception.class, () -> exceptionalBiConsumer.accept("Hello", "World"));
        assertEquals("Test exception", exception.getMessage());
    }
}
