package org.saltations.endeavour;

import java.util.function.Consumer;

/**
 * This is similar to the Java Supplier function type. It has a checked exception on it to allow it to
 * be used in lambda expressions on the outcome methods.
 *
 * @param <T> Type of the supplied value
 */

@FunctionalInterface
public interface ExceptionalConsumer<T> extends Consumer<T>
{
    void consumeIt(T t) throws Exception;

    default void accept(T t)
    {
        try
        {
            consumeIt(t);
        }
        catch (Exception e)
        {
            var toBeThrown = switch(e)
            {
                case RuntimeException ex -> ex;
                case Exception ex -> new RuntimeException(ex);
            };

            throw toBeThrown;
        }
    }


}
