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

public record Failure<V>(FailureDescription fail) implements Outcome<V>
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
    public Outcome<V> consumeSuccess(Consumer<Outcome<V>> successConsumer)
    {
        // Do Nothing
        return this;    
    }

    @Override
    public Outcome<V> consumeFailure(Consumer<Failure<V>> failureConsumer)
    {
        failureConsumer.accept(this);

        return this;
    }

    @Override
    public void consume(Consumer<Outcome<V>> successConsumer, Consumer<Outcome<V>> failureConsumer)
    {
        failureConsumer.accept(this);
    }

    @Override
    public Outcome<V> onSuccess(Supplier<Outcome<V>> supplier)
    {
        return this;
    }

    @Override
    public Outcome<V> onFailure(Supplier<Outcome<V>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Outcome<V> onSuccess(Function<V, Outcome<V>> transform)
    {
        return this;
    }

    @Override
    public Outcome<V> onFailure(Function<Outcome<V>, Outcome<V>> transform)
    {
        return transform.apply(this);
    }

    @Override
    public <V2> Outcome<V2> map(Function<V, V2> transform)
    {
        return new Failure<V2>(fail);
    }

    @Override
    public <U> Outcome<U> flatMap(Function<V, Outcome<U>> transform)
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
