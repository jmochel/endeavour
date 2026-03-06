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
class CheckedPredicateTest
{

    // Test for normal execution
    @Test
    @Order(1)
    void whenNoExceptionIsThrownThenCheckedPredicateReturnsTransformedValue()
    {
        var checkedPredicate = new CheckedPredicate<Integer>()
        {
            @Override
            public boolean testIt(Integer integer) throws Exception
            {
                return true;
            }
        };

        var result = checkedPredicate.test(5);
        assertTrue(result, "The predicate should return true");
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionIsThrownThenCheckedPredicateThrowsUntouchedRuntimeException()
    {

        final String EXCEPTION_MESSAGE = "Oooops!";

        var checkedPredicate = new CheckedPredicate<Integer>()
        {
            @Override
            public boolean testIt(Integer integer) throws Exception
            {
               throw new RuntimeException(EXCEPTION_MESSAGE);
            }
        };

        var exception = assertThrows(RuntimeException.class, () -> checkedPredicate.test(5));
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage(), "Exception message should match");
    }

    @Test
    @Order(3)
    void whenCheckedExceptionIsThrownThenCheckedPredicateThrowsWrappedRuntimeException()
    {

        final String EXCEPTION_MESSAGE = "Oooops!";

        var checkedPredicate = new CheckedPredicate<Integer>()
        {
            @Override
            public boolean testIt(Integer integer) throws Exception
            {
                throw new Exception(EXCEPTION_MESSAGE);
            }
        };

        var exception = assertThrows(RuntimeException.class, () -> checkedPredicate.test(5));
        assertEquals("java.lang.Exception: " + EXCEPTION_MESSAGE, exception.getMessage(), "Exception message should match");
    }
}
