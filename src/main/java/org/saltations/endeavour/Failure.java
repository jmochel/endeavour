package org.saltations.endeavour;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an Outcome that is a full failure of an operation. Carries information about the failure.
 *
 * @param <FV> The class of Failure value that is being carried.
 * @param <SV> The class of the unrealized Success value.
 */

public record Failure<FV extends FailureAssay, SV>(FV fail) implements Outcome<FV, SV>
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
    public Outcome<FV, SV> ifSuccess(Supplier<Outcome<FV, SV>> supplier)
    {
        return this;
    }

    @Override
    public Outcome<FV, SV> ifSuccess(Function<SV, Outcome<FV, SV>> transform)
    {
        return this;
    }

    @Override
    public void onSuccess(Consumer<Outcome<FV, SV>> action)
    {
        // Do Nothing
    }

    @Override
    public Outcome<FV, SV> ifFailure(Supplier<Outcome<FV, SV>> supplier)
    {
        return supplier.get();
    }

    @Override
    public Outcome<FV, SV> ifFailure(Function<Outcome<FV, SV>, Outcome<FV, SV>> transform)
    {
        return transform.apply(this);
    }

    @Override
    public void onFailure(Consumer<Outcome<FV, SV>> action)
    {
        action.accept(this);
    }

    @Override
    public void on(Consumer<Outcome<FV, SV>> successAction, Consumer<Outcome<FV, SV>> failureAction)
    {
        failureAction.accept(this);
    }

    @Override
    public <FV extends FailureAssay, SV2> Outcome<FV, SV2> map(Function<SV, SV2> transform)
    {
        return new Failure<FV, SV2>((FV) fail);
    }

    @Override
    public <U> Outcome<FV, U> flatMap(Function<SV, Outcome<FV, U>> transform)
    {
        return new Failure<FV, U>(fail);
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
