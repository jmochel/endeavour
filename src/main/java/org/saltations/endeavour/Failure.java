package org.saltations.endeavour;


import java.util.function.Function;

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
        throw new IllegalStateException("Cannot get value from a failure: " + fail.getTitle() + " - " + fail.getDetail());
    }

    @Override
    public <U> Result<U> map(Function<T, U> mapping)
    {
        return new Failure<U>(fail);
    }

    @Override
    public <U> Result<U> flatMap(Function<T, Result<U>> mapping)
    {
        return new Failure<U>(fail);
    }

    @Override
    public Result<T> actOnSuccess(ExceptionalConsumer<Success<T>> action)
    {
        // Do Nothing
        return this;    
    }

    @Override
    public Result<T> actOnFailure(ExceptionalConsumer<Failure<T>> action)
    {
        action.accept(this);
        return this;
    }

    @Override
    public Result<T> supplyOnSuccess(ExceptionalSupplier<Result<T>> supplier)
    {
        return this;
    }

    @Override
    public Result<T> supplyOnFailure(ExceptionalSupplier<Result<T>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Result<T> mapOnSuccess(ExceptionalFunction<T, Result<T>> transform)
    {
        return this;
    }

    @Override
    public Result<T> mapOnFailure(ExceptionalFunction<Result<T>, Result<T>> transform)
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
