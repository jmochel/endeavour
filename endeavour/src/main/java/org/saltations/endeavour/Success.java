package org.saltations.endeavour;

public sealed interface Success<T> extends Result<T> permits Value, NoValue {


    default Result<T> ifSuccess(ExceptionalConsumer<Success<T>> action)
    {
        action.accept(this);
        return this;
    }

    default Result<T> ifFailure(ExceptionalConsumer<Failure<T>> action)
    {
        return this;
    }

    default Result<T> orElse(ExceptionalSupplier<Result<T>> supplier)
    {
        return supplier.get();
    }

    default Result<T> orElseGet(ExceptionalSupplier<Result<T>> supplier)
    {
        return this;
    }

    default Result<T> flatMap(ExceptionalFunction<T, Result<T>> transform)
    {
        return transform.apply(get());
    }



}

