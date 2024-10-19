package org.saltations.endeavour;

import java.util.concurrent.Callable;

/**
 * This is similar to the Java Supplier function type. It has a checked exception on it to allow it to
 * be used in lambda expressions on the outcome methods.
 *
 * @param <T> Type of the supplied value
 */

@FunctionalInterface
public interface ExceptionalCallable<T> extends Callable<T>
{
    T apply() throws Exception;

    default T call()
    {
        try
        {
            return apply();
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
