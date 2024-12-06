package org.saltations.endeavour;// Import necessary libraries
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;
import java.util.function.Function;

@Order(43)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ExceptionalFunctionTest {

    // Test for normal execution
    @Test
    @Order(1)
    void whenNoExceptionIsThrownThenExceptionalFunctionReturnsTransformedValue()
    {
        var exceptionalFunction = new ExceptionalFunction<Integer, String>()
        {
            @Override
            public String transformIt(Integer source) throws Exception
            {
                return String.valueOf(source);
            }
        };

        var result = exceptionalFunction.apply(5);
        assertEquals("5", result, "The function should return '5'");
    }

    @Test
    @Order(2)
    void whenRuntimeExceptionIsThrownThenExceptionalFunctionThrowsUntouchedRuntimeException()
    {

        final String EXCEPTION_MESSAGE = "Oooops!";

        var exceptionalFunction = new ExceptionalFunction<Integer, String>()
        {
            @Override
            public String transformIt(Integer source) throws Exception
            {
                throw new RuntimeException(EXCEPTION_MESSAGE);
            }
        };

        var exception = assertThrows(RuntimeException.class, () -> exceptionalFunction.apply(5));
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage(), "Exception message should match");
    }

    @Test
    @Order(3)
    void whenCheckedExceptionIsThrownThenExceptionalFunctionThrowsWrappedRuntimeException()
    {

        final String EXCEPTION_MESSAGE = "Oooops!";

        var exceptionalFunction = new ExceptionalFunction<Integer, String>()
        {
            @Override
            public String transformIt(Integer source) throws Exception
            {
                throw new Exception(EXCEPTION_MESSAGE);
            }
        };

        var exception = assertThrows(RuntimeException.class, () -> exceptionalFunction.apply(5));
        assertEquals("java.lang.Exception: " + EXCEPTION_MESSAGE, exception.getMessage(), "Exception message should match");
    }

    @Test
    @Order(4)
    void whenComposedAndChangedThenExceptionalFunctionReturnsTransformedValue() throws Throwable
    {
        final String EXCEPTION_MESSAGE = "Oooops!";

        var initialFunction = new ExceptionalFunction<Integer, Integer>()
        {
            @Override
            public Integer transformIt(Integer source) throws Exception
            {
                return 2 * source;
            }
        };

        var multiplyBy10 = new ExceptionalFunction<Integer, Integer>() {
            @Override
            public Integer transformIt(Integer source) throws Exception
            {
                return source * 10;
            }
        };

        var first = initialFunction.apply(2);
        assertEquals(4, first.intValue(), "The function should return '4'");

        var second = initialFunction.compose(multiplyBy10).apply(2);
        assertEquals(40, second.intValue(), "The function should return '4'");

        var third = initialFunction.andThen(multiplyBy10).apply(2);
        assertEquals(40, second.intValue(), "The function should return '4'");
    }
}
