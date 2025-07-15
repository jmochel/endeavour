package org.saltations.endeavour;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record NoValue<V>(V value) implements Success<V> {
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
    public Result<V> onSuccess(Supplier<Result<V>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Result<V> onSuccess(Function<V, Result<V>> transform)
    {
        return transform.apply(get());
    }

    @Override
    public Result<V> consumeSuccess(Consumer<Result<V>> action)
    {
        action.accept(this);
        return this;
    }

    @Override
    public Result<V> onFailure(Supplier<Result<V>> supplier)
    {
        return this;
    }

    @Override
    public Result<V> onFailure(Function<Result<V>, Result<V>> transform)
    {
        return this;
    }

    @Override
    public Result<V> consumeFailure(Consumer<Failure<V>> action)
    {
        return this;
    }

    @Override
    public void consume(Consumer<Result<V>> successAction, Consumer<Result<V>> failureAction)
    {
        successAction.accept(this);
    }

    @Override
    public <V2> Result<V2> map(Function<V, V2> transform)
    {
        return new NoValue<V2>(transform.apply(value));
    }

    @Override
    public <U> Result<U> flatMap(Function<V, Result<U>> transform)
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
