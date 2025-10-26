package org.saltations.endeavour;

import java.util.function.BiConsumer;

/**
 * This is a special {@code BiConsumer} function that is similar to the Java {@code BiConsumer} functional interface.
 * It has a {@code consumeIt} method that can throw a checked exception and is wrapped within the {@code accept} method.
 * <p>
 * The accept method is a wrapper around the consumeIt method that handles the checked exception and rethrows it as a runtime exception.
 * This is useful for lambda expressions on the outcome methods.
 *
 * @param <T> Type of the first supplied value
 * @param <U> Type of the second supplied value
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
