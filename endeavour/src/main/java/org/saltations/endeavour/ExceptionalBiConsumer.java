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
    void consumeIt(T t, U u) throws Exception;

    default void accept(T t, U u)
    {
        try
        {
            consumeIt(t, u);
        }
        catch (Exception e)
        {
            throw switch(e)
            {
                case RuntimeException ex -> ex;
                case Exception ex -> new RuntimeException(ex);
            };
        }
    }


}
