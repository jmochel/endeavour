package org.saltations.endeavour;

import java.util.function.Predicate;

/**
 * This is similar to the Java Function type excepts that it allows for throwing exceptions and errors which allows it to be used in Result monads
 *
 * @param <T> Type of the input
 * @param <R> Type of the output
 */

@FunctionalInterface
public interface ExceptionalPredicate<T> extends Predicate<T>
{
    boolean apply(T t) throws Exception;

    default boolean test(T t)
    {
        try
        {
            return apply(t);
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
