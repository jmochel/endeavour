package org.saltations.endeavour;

import java.util.function.BiConsumer;

/**
 * This is similar to the Java Supplier function type. It has a checked exception on it to allow it to
 * be used in lambda expressions on the outcome methods.
 *
 * @param <T> Type of the supplied value
 */

@FunctionalInterface
public interface ExceptionalBiConsumer<T,U> extends BiConsumer<T,U>
{
    void consume(T t, U u) throws Exception;

    default void accept(T t, U u)
    {
        try
        {
            consume(t, u);
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
