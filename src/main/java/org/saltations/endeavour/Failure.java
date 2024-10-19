package org.saltations.endeavour;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents the full failure of an operation. Carries information about the failure.
 *
 * @param <FV>
 * @param <SV>
 */

public record Failure<FV extends Fail, SV>(FV fail) implements Outcome<FV, SV>
{

    @Override
    public boolean hasSuccessValue()
    {
        return false;
    }

    @Override
    public boolean hasFailureValue()
    {
        return true;
    }

    @Override
    public SV get()
    {
        throw new IllegalStateException(fail.getTotalMessage());
    }

    public FailType getType()
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
    public Outcome<FV, SV> ifFailureTransform(Function<Outcome<FV, SV>, Outcome<FV, SV>> transform)
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

//    @Override
//    public <U> Outcome<FV, U> map(Function<SV, U> transform)
//    {
//        return new Failure<FV, U>(fail);
//    }

    @Override
    public <FV extends Fail, SV2> Outcome<FV, SV2> map(Function<SV, SV2> transform)
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
