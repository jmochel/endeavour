package org.saltations.endeavour;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Optional;

public record Success<V>(V value) implements Outcome<V>
{
    @Override
    public boolean hasSuccessPayload()
    {
        return true;
    }

    @Override
    public boolean hasFailurePayload()
    {
        return false;
    }

    @Override
    public V get()
    {
        return value;
    }

    @Override
    public Outcome<V> onSuccess(Supplier<Outcome<V>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Outcome<V> onSuccess(Function<V, Outcome<V>> transform)
    {
        return transform.apply(get());
    }

    @Override
    public Outcome<V> consumeSuccess(Consumer<Outcome<V>> action)
    {
        action.accept(this);
        return this;
    }

    @Override
    public Outcome<V> onFailure(Supplier<Outcome<V>> supplier)
    {
        return this;
    }

    @Override
    public Outcome<V> onFailure(Function<Outcome<V>, Outcome<V>> transform)
    {
        return this;
    }

    @Override
    public Outcome<V> consumeFailure(Consumer<Failure<V>> action)
    {
        return this;
    }

    @Override
    public void consume(Consumer<Outcome<V>> successAction, Consumer<Outcome<V>> failureAction)
    {
        successAction.accept(this);
    }

    @Override
    public <V2> Outcome<V2> map(Function<V, V2> transform)
    {
        return new Success<V2>(transform.apply(value));
    }

    @Override
    public <U> Outcome<U> flatMap(Function<V, Outcome<U>> transform)
    {
        return transform.apply(value);
    }

    @Override
    public Optional<V> opt() {
        return Optional.ofNullable(value);
    }

    public String toString()
    {
        return new StringBuffer("Success").append("[")
                                           .append(value)
                                           .append("]")
                                           .toString();
    }
}
