package org.saltations.endeavour;

import java.util.function.Supplier;

/**
 * This is similar to the Java Supplier function type. It has a checked exception on it to allow it to
 * be used in lambda expressions on the outcome methods.
 *
 * @param <T> Type of the supplied value
 */

@FunctionalInterface
public interface CheckedSupplier<T>
{
    /**
     * Supplies a value of type {@code T}.
     * 
     * @return A value of type {@code T}.
     * 
     * @throws Exception if the supply operation fails.
     */
    
    T get() throws Exception;

}
