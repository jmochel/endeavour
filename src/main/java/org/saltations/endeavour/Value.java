package org.saltations.endeavour;

import java.util.Objects;
import java.util.function.Function;

public record Value<T>(T value) implements Success<T> {

    @Override
    public boolean hasPayload()
    {
        return true;
    }

    @Override
    public T get()
    {
        return value;
    }

    @Override
    public <U> Result<U> map(Function<T, U> mapping)
    {
        // If the mapping transforms a payload into a null, we are returning a NoValue of an appropriate type,
        // otherwise we are returning a Value with the new payload

        var newValue = mapping.apply(get());
        return Objects.isNull(newValue) ? new NoValue<U>() : new Value<U>(newValue);
    }

    @Override
    public <U> Result<U> flatMap(Function<T, Result<U>> mapping)
    {
        return mapping.apply(value);
    }

    public String toString()
    {
        return new StringBuffer("Success").append("[")
                                           .append(value)
                                           .append("]")
                                           .toString();
    }
}
