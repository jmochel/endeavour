package org.saltations.endeavour;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Optional;

/**
 * Represe of an operationnts an full failure outcome.
 *
 * @param <V> The class of the unrealized Success value.
 */

public record Failure<V>(FailureDescription fail) implements Result<V>
{

    @Override
    public boolean hasSuccessPayload()
    {
        return false;
    }

    @Override
    public boolean hasFailurePayload()
    {
        return true;
    }

    @Override
    public V get()
    {
        throw new IllegalStateException(fail.getTotalMessage());
    }

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
    public Result<V> consumeSuccess(Consumer<Result<V>> successConsumer)
    {
        // Do Nothing
        return this;    
    }

    @Override
    public Result<V> consumeFailure(Consumer<Failure<V>> failureConsumer)
    {
        failureConsumer.accept(this);

        return this;
    }

    @Override
    public void consume(Consumer<Result<V>> successConsumer, Consumer<Result<V>> failureConsumer)
    {
        failureConsumer.accept(this);
    }

    @Override
    public Result<V> onSuccess(Supplier<Result<V>> supplier)
    {
        return this;
    }

    @Override
    public Result<V> onFailure(Supplier<Result<V>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Result<V> onSuccess(Function<V, Result<V>> transform)
    {
        return this;
    }

    @Override
    public Result<V> onFailure(Function<Result<V>, Result<V>> transform)
    {
        return transform.apply(this);
    }

    @Override
    public <V2> Result<V2> map(Function<V, V2> transform)
    {
        return new Failure<V2>(fail);
    }

    @Override
    public <U> Result<U> flatMap(Function<V, Result<U>> transform)
    {
        return new Failure<U>(fail);
    }

    @Override
    public Optional<V> opt() {
        return Optional.empty();
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
