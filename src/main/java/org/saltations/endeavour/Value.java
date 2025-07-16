package org.saltations.endeavour;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Optional;

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
    public Optional<T> opt() {
        return Optional.of(value);
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

    @Override
    public void act(Consumer<Result<T>> action)
    {
        action.accept(this);
    }

    @Override
    public Result<T> actOnSuccess(Consumer<Success<T>> action)
    {
        action.accept(this);
        return this;
    }

    @Override
    public Result<T> actOnFailure(Consumer<Failure<T>> action)
    {
        return this;
    }


    @Override
    public Result<T> onSuccess(Supplier<Result<T>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Result<T> onSuccess(Function<T, Result<T>> transform)
    {
        return transform.apply(get());
    }

    @Override
    public Result<T> onFailure(Supplier<Result<T>> supplier)
    {
        return this;
    }

    @Override
    public Result<T> onFailure(Function<Result<T>, Result<T>> transform)
    {
        return this;
    }


    public String toString()
    {
        return new StringBuffer("Success").append("[")
                                           .append(value)
                                           .append("]")
                                           .toString();
    }
}
