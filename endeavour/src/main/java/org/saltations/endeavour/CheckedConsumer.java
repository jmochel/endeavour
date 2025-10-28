package org.saltations.endeavour;

/**
 * A Java Consumer function that can throw a checked exception.
 *
 * @param <T> Type of the consumed value
 * 
 * @throws Exception if the consume operation fails.
 * 
 * @author Jim Mochel
 */

@FunctionalInterface
public interface CheckedConsumer<T>
{
    /**
     * Consumes a value of type {@code T}.
     * 
     * @param t The value to consume.
     * 
     * @throws Exception if the consume operation fails.
     */
    
    void accept(T t) throws Exception;

}
