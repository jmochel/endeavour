package org.saltations.endeavour;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record PartialSuccess<SV>(FailureAssay failureValue, SV successValue) implements Outcome<SV>
{
    @Override
    public boolean hasSuccessPayload()
    {
        return true;
    }

    @Override
    public boolean hasFailurePayload()
    {
        return true;
    }

    @Override
    public SV get()
    {
        return successValue;
    }

    @Override
    public Optional<SV> opt() {
        return Optional.ofNullable(successValue);
    }

    @Override
    public Outcome<SV> ifSuccess(Supplier<Outcome<SV>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Outcome<SV> ifSuccess(Function<SV, Outcome<SV>> transform)
    {
        return transform.apply(successValue);
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
        failureAction.accept(this);
    }

    @Override
    public <SV2> Outcome<SV2> map(Function<SV, SV2> transform)
    {
        return new PartialSuccess<SV2>(failureValue, transform.apply(successValue));
    }

    @Override
    public <SV2> Outcome<SV2> flatMap(Function<SV, Outcome<SV2>> transform)
    {
        return transform.apply(successValue);
    }

    public String toString()
    {
        return new StringBuffer("XPartialSuccess").append("[")
                                                  .append(successValue)
                                                  .append("]")
                                                  .append("[")
                                                  .append(failureValue)
                                                  .append("]")
                                                  .toString();
    }
}
