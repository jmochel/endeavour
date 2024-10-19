package org.saltations.endeavour;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is similar to the Java Function type excepts that it allows for throwing exceptions and errors which allows it to be used in Result monads
 *
 * @param <T> Type of the input
 * @param <R> Type of the output
 */

@FunctionalInterface
public interface ExceptionalFunction<T,R>
{
    R transform(T t) throws Exception;

    default R apply(T t)
    {
        try {
            return transform(t);
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    default <V> ExceptionalFunction<V, R> compose(ExceptionalFunction<? super V, ? extends T> before) throws Throwable {
        checkNotNull(before);

        return (V v) -> transform(before.transform(v));
    }

    default <V> ExceptionalFunction<T, V> andThen(ExceptionalFunction<? super R, ? extends V> after) throws Throwable {
        checkNotNull(after);

        return (T t) -> after.transform(transform(t));
    }

    static <T> ExceptionalFunction<T, T> identity() {
        return t -> t;
    }

}
