package org.saltations.endeavour;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Provide example code for the various common scenarios involving operational results.
 * <p>
 * Includes
 * <ol>
 *     <li>Success result converted to REST Response</li>
 *     <li>Partial Success result converted to REST Response</li>
 *     <li>Failure result (no cause) converted to REST Response</li>
 *     <li>Failure result (with standard java exception cause) converted to REST Response</li>
 *     <li>Failure result (with custom exception cause) converted to REST Response</li>
 * </ol>
 */
@Order(30)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class OutcomeExemplars
{
    @Test
    @Order(10)
    public void successResultConvertedToReturnValue()
    {
        //
        // Demonstrate the use of a functional to convert a success result to a REST response
        //

        var success = new Success<>(21);
        var result = success.get();
    }

}
