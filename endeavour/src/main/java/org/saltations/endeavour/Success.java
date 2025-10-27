package org.saltations.endeavour;

/**
 * Represents a successful operation result. This interface is implemented by
 * {@link QuantSuccess} (for operations that produced a value) and {@link QualSuccess}
 * (for operations that succeeded but produced no value).
 *
 * @param <T> the type of the successful result
 */
public sealed interface Success<T> extends Result<T> permits QuantSuccess, QualSuccess {


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

    default Result<T> orElse(CheckedSupplier<Result<T>> supplier)
    {
        if (supplier == null) {
            throw new NullPointerException("CheckedSupplier cannot be null");
        }
        
        try
        {
            return supplier.get();
        }
        catch (InterruptedException ex)
        {
            // restore the interrupted flag
            Thread.currentThread().interrupt();
            return new Failure<>(FailureDescription.of()
                .type(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
                .cause(ex)
                .build());
        }
        catch (Exception e)
        {
            return switch(e)
            {
                case RuntimeException ex -> new Failure<>(FailureDescription.of()
                    .type(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
                    .cause(ex)
                    .build());
                case Exception ex -> new Failure<>(FailureDescription.of()
                    .type(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
                    .cause(ex)
                    .build());
            };
        }
    }

    default Result<T> orElseGet(CheckedSupplier<Result<T>> supplier)
    {
        if (supplier == null) {
            throw new NullPointerException("CheckedSupplier cannot be null");
        }
        return this;
    }



}

