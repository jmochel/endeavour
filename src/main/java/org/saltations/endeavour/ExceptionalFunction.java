package org.saltations.endeavour;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is similar to the Java Function type excepts that it allows for throwing exceptions and errors which allows it to be used in Result monads
 *
 * @param <T> Type of the input
 * @param <R> Type of the output
 */

@FunctionalInterface
public interface ExceptionalFunction<T, R> extends Function<T, R>
{
    R transformIt(T t) throws Exception;

    default R apply(T t)
    {
        try {
            return transformIt(t);
        }
        catch (Exception e) {
            var toBeThrown = switch (e) {
                case RuntimeException ex -> ex;
                case Exception ex -> new RuntimeException(ex);
            };

            throw toBeThrown;
        }
    }

    default <V> ExceptionalFunction<V, R> compose(ExceptionalFunction<? super V, ? extends T> before) throws Throwable
    {
        checkNotNull(before);

        return (V v) -> transformIt(before.transformIt(v));
    }

    default <V> ExceptionalFunction<T, V> andThen(ExceptionalFunction<? super R, ? extends V> after) throws Throwable
    {
        checkNotNull(after);

        return (T t) -> after.transformIt(transformIt(t));
    }

    static <T> ExceptionalFunction<T, T> identity()
    {
        return t -> t;
    }

}
