package org.saltations.endeavour;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a successful operation that produced a qualified result with no value.
 * This is the concrete implementation of {@link Success} for cases where
 * the operation succeeded but produced no meaningful value (e.g., void operations,
 * deletions, updates).
 *
 * @param <T> The type of the value that would have been present if the operation had produced a value.
 */
 
public record QualSuccess<T>() implements Success<T> {

    @Override
    public boolean hasPayload()
    {
        return false;
    }

    @Override
    public T get()
    {
        return null;
    }

    @Override
    public <U> Result<U> map(Function<T, U> mapping)
    {
        // If the mapping transforms a null into a payload, we are returning a QuantSuccess of an appropriate type,
        // otherwise we are returning QualSuccess

        var newPayload = mapping.apply(null);

        return Objects.isNull(newPayload) ? new QualSuccess<U>() : new QuantSuccess<U>(newPayload);
    }

    @Override
    public <U> Result<U> flatMap(Function<T, Result<U>> mapping)
    {
        return mapping.apply(null);
    }

    public String toString()
    {
        return new StringBuilder("Success").append("[No value]")
                                          .toString();
    }
}
