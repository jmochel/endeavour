package org.saltations.endeavour;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Order(42)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ExceptionalConsumerTest
{
    @Test
    @Order(1)
    void whenNoExceptionIsThrownThenExceptionalConsumerConsumes()
    {
        var exceptionalConsumer = new ExceptionalConsumer<String>()
        {
            @Override
            public void consumeIt(String s) throws Exception
            {
                return;
            }
        };

        assertDoesNotThrow(() -> exceptionalConsumer.accept("Hello"));
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionIsThrownThenExceptionalConsumerThrowsUntouchedRuntimeException()
    {
        var exceptionalConsumer = new ExceptionalConsumer<String>()
        {
            @Override
            public void consumeIt(String s) throws Exception
            {
                throw new Exception("Test exception");
            }
        };

        assertThrows(RuntimeException.class, () -> exceptionalConsumer.accept("Hello"));
    }

    @Test
    @Order(3)
    void whenCheckedExceptionIsThrownThenExceptionalConsumerThrowsWrappedRuntimeException()
    {
        var exceptionalConsumer = new ExceptionalConsumer<String>()
        {
            @Override
            public void consumeIt(String s) throws Exception
            {
                throw new RuntimeException("Test exception");
            }
        };

        var exception = assertThrows(Exception.class, () -> exceptionalConsumer.accept("Hello"));
        assertEquals("Test exception", exception.getMessage());
    }
}
