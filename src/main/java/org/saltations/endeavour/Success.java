package org.saltations.endeavour;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record Success<SV>(SV value) implements Outcome<SV>
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
    public SV get()
    {
        return value;
    }

    @Override
    public Optional<SV> opt() {
        return Optional.ofNullable(value);
    }

    @Override
    public Outcome<SV> ifSuccess(Supplier<Outcome<SV>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Outcome<SV> ifSuccess(Function<SV, Outcome<SV>> transform)
    {
        return transform.apply(get());
    }

    @Override
    public void onSuccess(Consumer<Outcome<SV>> action)
    {
        action.accept(this);
    }

    @Override
    public Outcome<SV> ifFailure(Supplier<Outcome<SV>> supplier)
    {
        return this;
    }

    @Override
    public Outcome<SV> ifFailure(Function<Outcome<SV>, Outcome<SV>> transform)
    {
        return this;
    }

    @Override
    public Outcome<SV> onFailure(Consumer<Failure<SV>> action)
    {
        return this;
    }

    @Override
    public void on(Consumer<Outcome<SV>> successAction, Consumer<Outcome<SV>> failureAction)
    {
        successAction.accept(this);
    }

    @Override
    public <SV2> Outcome<SV2> map(Function<SV, SV2> transform)
    {
        return new Success<SV2>(transform.apply(value));
    }

    @Override
    public <U> Outcome<U> flatMap(Function<SV, Outcome<U>> transform)
    {
        return transform.apply(value);
    }

    public String toString()
    {
        return new StringBuffer("Success").append("[")
                                           .append(value)
                                           .append("]")
                                           .toString();
    }
}
