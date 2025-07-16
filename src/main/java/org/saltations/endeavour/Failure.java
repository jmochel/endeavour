package org.saltations.endeavour;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Optional;

/**
 * Represents a failed result.
 *
 * @param <T> The class of the unrealized Success payload value.
 */

public record Failure<T>(FailureDescription fail) implements Result<T>
{
    public FailureType getType()
    {
        return fail.getType();
    }

    public String getDetail()
    {
        return fail.getDetail();
    }

    public String getTitle()
    {
        return fail.getTitle();
    }

    public Exception getCause()
    {
        return fail.getCause();
    }

    @Override
    public boolean hasPayload()
    {
        return false;
    }

    @Override
    public T get()
    {
        throw new IllegalStateException(fail.getTotalMessage());
    }

    @Override
    public Optional<T> opt() {
        return Optional.empty();
    }

    @Override
    public <U> Result<U> map(Function<T, U> mapping)
    {
        return new Failure<U>(fail);
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
        // Do Nothing
        return this;    
    }

    @Override
    public Result<T> actOnFailure(Consumer<Failure<T>> action)
    {
        action.accept(this);

        return this;
    }


    @Override
    public Result<T> onSuccess(Supplier<Result<T>> supplier)
    {
        return this;
    }

    @Override
    public Result<T> onFailure(Supplier<Result<T>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Result<T> onSuccess(Function<T, Result<T>> transform)
    {
        return this;
    }

    @Override
    public Result<T> onFailure(Function<Result<T>, Result<T>> transform)
    {
        return transform.apply(this);
    }


    @Override
    public String toString()
    {
        return new StringBuffer("Failure").append("[")
                                           .append(getType().toString())
                                           .append(":")
                                           .append(getTitle())
                                           .append(":")
                                           .append(getDetail())
                                           .append("]")
                                           .toString();
    }

}
