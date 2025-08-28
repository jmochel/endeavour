package org.saltations.endeavour;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an full failure Outcome of an operation.
 *
 * @param <SV> The class of the unrealized Success value.
 */

public record Failure<SV>(FailureDescription fail) implements Outcome<SV>
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
    public SV get()
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
    public Outcome<SV> ifSuccess(Supplier<Outcome<SV>> supplier)
    {
        return this;
    }

    @Override
    public Outcome<SV> ifSuccess(Function<SV, Outcome<SV>> transform)
    {
        return this;
    }

    @Override
    public void onSuccess(Consumer<Outcome<SV>> action)
    {
        // Do Nothing
    }

    @Override
    public Outcome<SV> ifFailure(Supplier<Outcome<SV>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Outcome<SV> ifFailure(Function<Outcome<SV>, Outcome<SV>> transform)
    {
        return transform.apply(this);
    }

    @Override
    public Outcome<SV> onFailure(Consumer<Failure<SV>> action)
    {
        action.accept(this);

        return this;
    }

    @Override
    public void on(Consumer<Outcome<SV>> successAction, Consumer<Outcome<SV>> failureAction)
    {
        failureAction.accept(this);
    }

    @Override
    public <SV2> Outcome<SV2> map(Function<SV, SV2> transform)
    {
        return new Failure<SV2>(fail);
    }

    @Override
    public <U> Outcome<U> flatMap(Function<SV, Outcome<U>> transform)
    {
        return new Failure<U>(fail);
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
