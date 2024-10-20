package org.saltations.endeavour;

import java.util.function.Supplier;

/**
 * This is similar to the Java Supplier function type. It has a checked exception on it to allow it to
 * be used in lambda expressions on the outcome methods.
 *
 * @param <T> Type of the supplied value
 */

@FunctionalInterface
public interface ExceptionalSupplier<T> extends Supplier<T>
{
    T supply() throws Exception;

    default T get()
    {
        try
        {
            return supply();
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
            {
                throw (RuntimeException) e;
            }

            throw new RuntimeException(e);
        }
    }


}
