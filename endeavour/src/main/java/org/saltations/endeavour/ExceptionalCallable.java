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
    T callIt() throws Exception;

    default T call()
    {
        try
        {
            return callIt();
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
