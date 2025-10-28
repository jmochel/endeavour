package org.saltations.endeavour;

import java.util.function.Supplier;

/**
 * A Java Supplier function that can throw a checked exception.
 *
 * @param <T> Type of the supplied value
 * 
 * @throws Exception if the supply operation fails.
 * 
 * @author Jim Mochel
 */

@FunctionalInterface
public interface CheckedSupplier<T>
{
    /**
     * Supplies a value of type {@code T}.
     * 
     * @return A value of type {@code T} (including null).
     * 
     * @throws Exception if the supply operation fails.
     */
    
    T get() throws Exception;

}
