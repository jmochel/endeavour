package org.saltations.endeavour;

public sealed interface Success<T> extends Result<T> permits Value, NoValue {


    default Result<T> actOnSuccess(ExceptionalConsumer<Success<T>> action)
    {
        action.accept(this);
        return this;
    }

    default Result<T> actOnFailure(ExceptionalConsumer<Failure<T>> action)
    {
        return this;
    }

    default Result<T> supplyOnSuccess(ExceptionalSupplier<Result<T>> supplier)
    {
        return supplier.get();
    }

    default Result<T> supplyOnFailure(ExceptionalSupplier<Result<T>> supplier)
    {
        return this;
    }

    default Result<T> mapOnSuccess(ExceptionalFunction<T, Result<T>> transform)
    {
        return transform.apply(get());
    }

    default Result<T> mapOnFailure(ExceptionalFunction<Result<T>, Result<T>> transform)
    {
        return this;
    }


}

