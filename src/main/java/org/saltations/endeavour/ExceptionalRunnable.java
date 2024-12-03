package org.saltations.endeavour;

/**
 * This is similar to the Java Supplier function type. It has a checked exception on it to allow it to
 * be used in lambda expressions on the outcome methods.
 *
 * @param <T> Type of the supplied value
 */

@FunctionalInterface
public interface ExceptionalRunnable extends Runnable
{
    void apply() throws Exception;

    default void run()
    {
        try
        {
            apply();
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
