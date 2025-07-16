package org.saltations.endeavour;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Represents a special case of Success that is used to represent a successful outcome with no value.
 *
 * @param <T> The type of the value that would have been present if the operation had succeeded.
 */
 
public record NoValue<T>() implements Success<T> {

    @Override
    public boolean hasPayload()
    {
        return true;
    }

    @Override
    public T get()
    {
        return null;
    }

    @Override
    public Optional<T> opt() {
        return Optional.empty();
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
        return new StringBuffer("Success").append("[No value]")
                                           .toString();
    }
}
