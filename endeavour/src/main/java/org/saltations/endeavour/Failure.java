package org.saltations.endeavour;

import java.util.function.Function;
import java.util.Optional;
import java.util.Objects;

/**
 * Represents a failed result.
 *
 * @param <T> The class of the unrealized Success payload value.
 */

public record Failure<T>(FailureDescription description) implements Result<T>
{
    public FailureType getType()
    {
        return description.getType();
    }

    public String getDetail()
    {
        return description.getDetail();
    }

    public String getTitle()
    {
        return description.getTitle();
    }

    public Exception getCause()
    {
        return description.getCause();
    }

    @Override
    public boolean hasPayload()
    {
        return false;
    }

    @Override
    public T get()
    {
        throw new IllegalStateException("Cannot get value from a failure: " + description.getTitle() + " - " + description.getDetail());
    }

    @Override
    public Optional<T> opt()
    {
        return Optional.empty();
    }

    @Override
    public <U> Result<U> map(CheckedFunction<T, U> mapping) throws Exception
    {
        Objects.requireNonNull(mapping, "Mapping function cannot be null");
        
        return new Failure<U>(description);
    }

    @Override
    public <U> Result<U> flatMap(CheckedFunction<T, Result<U>> mapping) throws Exception
    {
        Objects.requireNonNull(mapping, "Mapping function cannot be null");
        return new Failure<U>(description);
    }

    @Override
    public void act(CheckedConsumer<T> action) throws Exception
    {
        Objects.requireNonNull(action, "Action cannot be null");
        // Do Nothing
    }

    @Override
    public Result<T> ifSuccess(CheckedConsumer<Success<T>> action) throws Exception
    {
        Objects.requireNonNull(action, "Action cannot be null");
        // Do Nothing
        return this;    
    }

    @Override
    public Result<T> ifFailure(CheckedConsumer<Failure<T>> action) throws Exception
    {
        Objects.requireNonNull(action, "Action cannot be null");
        action.accept(this);
        return this;
    }

    @Override
    public Result<T> orElse(Result<T> alternateResult)
    {
        Objects.requireNonNull(alternateResult, "Alternate result cannot be null");

        return alternateResult;
    }

    @Override
    public Result<T> orElseGet(CheckedSupplier<Result<T>> supplier)
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
