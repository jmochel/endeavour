package org.saltations.endeavour;// Import necessary libraries
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.Callable;

@Order(41)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ExceptionalCallableTest {

    public static final String SNOOPY_BIT_THE_MAILMAN = "Snoopy bit the mailman";

    @Test
    @Order(1)
    void whenNoExceptionThrownThenExceptionalCallableReturnsResult()
    {
        var exceptionalCallable = new ExceptionalCallable()
        {
            @Override
            public String callIt() throws Exception
            {
                return SNOOPY_BIT_THE_MAILMAN;
            }
        };

        var result = assertDoesNotThrow(exceptionalCallable::call);
        assertEquals(SNOOPY_BIT_THE_MAILMAN, result);
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionThrownThenExceptionalCallableThrowsUntouchedRuntimeException()
    {
        var exceptionalCallable = new ExceptionalCallable()
        {
            @Override
            public String callIt() throws Exception
            {
                throw new RuntimeException(SNOOPY_BIT_THE_MAILMAN);
            }
        };

        var exception = assertThrows(RuntimeException.class, exceptionalCallable::call);
        assertEquals(SNOOPY_BIT_THE_MAILMAN, exception.getMessage());
    }

    @Test
    @Order(3)
    void whenCheckedExceptionThrownThenExceptionalCallableThrowsWrappedRuntimeException()
    {
        var exceptionalCallable = new ExceptionalCallable()
        {
            @Override
            public String callIt() throws Exception
            {
                throw new Exception(SNOOPY_BIT_THE_MAILMAN);
            }
        };

        var exception = assertThrows(RuntimeException.class, exceptionalCallable::call);
        assertEquals("java.lang.Exception: " + SNOOPY_BIT_THE_MAILMAN, exception.getMessage());
    }
}



