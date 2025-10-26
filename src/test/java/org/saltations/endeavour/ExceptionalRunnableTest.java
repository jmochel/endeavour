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

@Order(44)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ExceptionalRunnableTest
{
    public static final String SNOOPY_BIT_THE_MAILMAN = "Snoopy bit the mailman";

    @Test
    @Order(1)
    void whenNoExceptionThrownThenExceptionalRunnableReturnsResult()
    {
        var runnable = new ExceptionalRunnable()
        {
            @Override
            public void runIt() throws Exception
            {
                return;
            }
        };

        assertDoesNotThrow(runnable::runIt);
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionThrownThenExceptionalRunnableThrowsUntouchedRuntimeException()
    {
        var runnable = new ExceptionalRunnable()
        {
            @Override
            public void runIt() throws Exception
            {
                throw new RuntimeException(SNOOPY_BIT_THE_MAILMAN);
            }
        };

        var exception = assertThrows(RuntimeException.class, runnable::run);
        assertEquals(SNOOPY_BIT_THE_MAILMAN, exception.getMessage());
    }

    @Test
    @Order(3)
    void whenCheckedExceptionThrownThenExceptionalRunnableThrowsWrappedRuntimeException()
    {
        var runnable = new ExceptionalRunnable()
        {
            @Override
            public void runIt() throws Exception
            {
                throw new Exception(SNOOPY_BIT_THE_MAILMAN);
            }
        };

        var exception = assertThrows(RuntimeException.class, runnable::run);
        assertEquals("java.lang.Exception: " + SNOOPY_BIT_THE_MAILMAN, exception.getMessage());
    }
}



