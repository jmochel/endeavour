package org.saltations.endeavour;// Import necessary libraries

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Order(45)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ExceptionalSupplierTest
{

    public static final String SNOOPY_BIT_THE_MAILMAN = "Snoopy bit the mailman";

    @Test
    @Order(1)
    void whenNoExceptionThrownThenExceptionalSupplierReturnsResult()
    {
        var exceptionalSupplier = new ExceptionalSupplier()
        {
            @Override
            public String supplyIt() throws Exception
            {
                return SNOOPY_BIT_THE_MAILMAN;
            }
        };

        var result = assertDoesNotThrow(exceptionalSupplier::get);
        assertEquals(SNOOPY_BIT_THE_MAILMAN, result);
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionThrownThenExceptionalSupplierThrowsUntouchedRuntimeException()
    {
        var exceptionalSupplier = new ExceptionalSupplier()
        {
            @Override
            public String supplyIt() throws Exception
            {
                throw new RuntimeException(SNOOPY_BIT_THE_MAILMAN);
            }
        };

        var exception = assertThrows(RuntimeException.class, exceptionalSupplier::get);
        assertEquals(SNOOPY_BIT_THE_MAILMAN, exception.getMessage());
    }

    @Test
    @Order(3)
    void whenCheckedExceptionThrownThenExceptionalSupplierThrowsWrappedRuntimeException()
    {
        var exceptionalSupplier = new ExceptionalSupplier()
        {
            @Override
            public String supplyIt() throws Exception
            {
                throw new Exception(SNOOPY_BIT_THE_MAILMAN);
            }
        };

        var exception = assertThrows(RuntimeException.class, exceptionalSupplier::get);
        assertEquals("java.lang.Exception: " + SNOOPY_BIT_THE_MAILMAN, exception.getMessage());
    }
}



