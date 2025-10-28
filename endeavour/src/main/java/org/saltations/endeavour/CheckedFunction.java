package org.saltations.endeavour;

/**
 * A Java Function that can throw a checked exception.
 *
 * @param <T> Type of the input value
 * @param <R> Type of the output value
 * 
 * @throws Exception if the function operation fails.
 * 
 * @author Jim Mochel
 */

@FunctionalInterface
public interface CheckedFunction<T, R>
{
    /**
     * Applies a function to the input value.
     * 
     * @param t The input value.
     * @return The function result.
     * 
     * @throws Exception if the function operation fails.
     */
    
    R apply(T t) throws Exception;

}
