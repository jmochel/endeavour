package org.saltations.endeavour;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Glue class for functional operations.
 * <p>
 * Includes methods to
 * <ol>
 *     <li>Cast Exception Functions to their unexceptional counterparts</li>
 * </ol>.
 */

public class Functional
{
    /**
     * Wraps an {@code ExceptionalSupplier} in a {@code Supplier}.
     */

    public static <R> Supplier<R> toSupplier(ExceptionalSupplier<R> exceptionalSupplier)
    {
        return exceptionalSupplier;
    }

    /**
     * Casts an {@code ExceptionalConsumer} to a {@code Consumer}.
     */

    public static <R> Consumer<R> toConsumer(ExceptionalConsumer<R> exceptionalConsumer) {
        return exceptionalConsumer;
    }

    /**
     * Casts an {@code ExceptionalBiConsumer} to a {@code BiConsumer}.
     */

    public static <T, R> BiConsumer<T,R> toConsumer(ExceptionalBiConsumer<T,R> exceptionalBiConsumer) {
        return exceptionalBiConsumer;
    }

    /**
     * Casts an {@code ExceptionalFunction} to a {@code Function}.
     */

    public static <T, R> Function<T, R> toFunction(ExceptionalFunction<T, R> exceptionalFxn)
    {
        return exceptionalFxn;
    }

    /**
     * Casts an {@code ExceptionalRunnable} to a {@code Runnable}.
     */

    public static Runnable toRunnable(ExceptionalRunnable exceptionalRunnable)
    {
        return exceptionalRunnable;
    }

    /**
     * Casts an {@code ExceptionalCallable} to a {@code Callable}.
     */

    public static <R> Callable<R> toCallable(ExceptionalCallable<R> exceptionalCallable)
    {
        return exceptionalCallable;
    }
}
