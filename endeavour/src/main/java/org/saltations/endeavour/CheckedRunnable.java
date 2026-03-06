package org.saltations.endeavour;

/**
 * This is similar to the Java Runnable interface. It has a checked exception on it to allow it to
 * be used in lambda expressions on the outcome methods.
 */

@FunctionalInterface
public interface CheckedRunnable extends Runnable
{
    void runIt() throws Exception;

    default void run()
    {
        try
        {
            runIt();
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
