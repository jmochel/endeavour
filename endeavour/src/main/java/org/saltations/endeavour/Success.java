package org.saltations.endeavour;

import java.util.Objects;

/**
 * Represents a successful operation result. This interface is implemented by
 * {@link QuantSuccess} (for operations that produced a value) and {@link QualSuccess}
 * (for operations that succeeded but produced no value).
 *
 * @param <T> the type of the successful result
 */
public sealed interface Success<T> extends Result<T> permits QuantSuccess, QualSuccess {


    default Result<T> ifSuccess(CheckedConsumer<Success<T>> action) throws Exception
    {
        Objects.requireNonNull(action, "Action cannot be null");
        action.accept(this);
        return this;
    }

    default Result<T> ifFailure(CheckedConsumer<Failure<T>> action) throws Exception
    {
        Objects.requireNonNull(action, "Action cannot be null");
        return this;
    }

    default Result<T> orElse(Result<T> alternateResult)
    {
        Objects.requireNonNull(alternateResult, "Alternate result cannot be null");

        return alternateResult;
    }

    default Result<T> orElseGet(CheckedSupplier<Result<T>> supplier)
    {
        Objects.requireNonNull(supplier, "CheckedSupplier cannot be null");

        return this;
    }

    @Override
    default void act(CheckedConsumer<T> action) throws Exception
    {
        Objects.requireNonNull(action, "Action cannot be null");
        
        action.accept(get());
    }



}

