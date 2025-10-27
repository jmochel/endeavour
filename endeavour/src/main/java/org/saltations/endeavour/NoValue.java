package org.saltations.endeavour;

import java.util.Objects;
import java.util.function.Function;


/**
 * Represents a special case of Success that is used to represent a successful outcome with no value.
 *
 * @param <T> The type of the value that would have been present if the operation had succeeded.
 */
 
public record NoValue<T>() implements Success<T> {

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
        // If the mapping transforms a null into a payload, we are returning a Value of an appropriate type,
        // otherwise we are returning NoValue

        var newPayload = mapping.apply(null);

        return Objects.isNull(newPayload) ? new NoValue<U>() : new Value<U>(newPayload);
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
