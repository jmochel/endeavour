package org.saltations.endeavour;// Import necessary libraries

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(43)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ExceptionalPredicateTest
{

    // Test for normal execution
    @Test
    @Order(1)
    void whenNoExceptionIsThrownThenExceptionalPredicateReturnsTransformedValue()
    {
        var exceptionalPredicate = new ExceptionalPredicate<Integer>()
        {
            @Override
            public boolean testIt(Integer integer) throws Exception
            {
                return true;
            }
        };

        var result = exceptionalPredicate.test(5);
        assertTrue(result, "The predicate should return true");
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionIsThrownThenExceptionalPredicateThrowsUntouchedRuntimeException()
    {

        final String EXCEPTION_MESSAGE = "Oooops!";

        var exceptionalPredicate = new ExceptionalPredicate<Integer>()
        {
            @Override
            public boolean testIt(Integer integer) throws Exception
            {
               throw new RuntimeException(EXCEPTION_MESSAGE);
            }
        };

        var exception = assertThrows(RuntimeException.class, () -> exceptionalPredicate.test(5));
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage(), "Exception message should match");
    }

    @Test
    @Order(3)
    void whenCheckedExceptionIsThrownThenExceptionalPredicateThrowsWrappedRuntimeException()
    {

        final String EXCEPTION_MESSAGE = "Oooops!";

        var exceptionalPredicate = new ExceptionalPredicate<Integer>()
        {
            @Override
            public boolean testIt(Integer integer) throws Exception
            {
                throw new Exception(EXCEPTION_MESSAGE);
            }
        };

        var exception = assertThrows(RuntimeException.class, () -> exceptionalPredicate.test(5));
        assertEquals("java.lang.Exception: " + EXCEPTION_MESSAGE, exception.getMessage(), "Exception message should match");
    }
}
