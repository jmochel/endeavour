package org.saltations.endeavour;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record PartialSuccess<FV extends FailureAssay, SV>(FV failureValue, SV successValue) implements Outcome<FV, SV>
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
    public Outcome<FV, SV> ifSuccess(Supplier<Outcome<FV, SV>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Outcome<FV, SV> ifSuccess(Function<SV, Outcome<FV, SV>> transform)
    {
        return transform.apply(successValue);
    }

    @Override
    public void onSuccess(Consumer<Outcome<FV, SV>> action)
    {
        action.accept(this);
    }

    @Override
    public Outcome<FV, SV> ifFailure(Supplier<Outcome<FV, SV>> supplier)
    {
        return this;
    }

    @Override
    public Outcome<FV, SV> ifFailure(Function<Outcome<FV, SV>, Outcome<FV, SV>> transform)
    {
        return this;
    }

    @Override
    public void onFailure(Consumer<Outcome<FV, SV>> action)
    {
        // Do Nothing
    }

    @Override
    public void on(Consumer<Outcome<FV, SV>> successAction, Consumer<Outcome<FV, SV>> failureAction)
    {
        successAction.accept(this);
        failureAction.accept(this);
    }

    @Override
    public <FV extends FailureAssay, SV2> Outcome<FV, SV2> map(Function<SV, SV2> transform)
    {
        return new PartialSuccess<FV, SV2>((FV) failureValue, transform.apply(successValue));
    }

    @Override
    public <SV2> Outcome<FV, SV2> flatMap(Function<SV, Outcome<FV, SV2>> transform)
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
