package org.saltations.endeavour;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;

/**
 * A generic interface for outcomes of operations.
 * <p>
 * An <code>Outcome</code> is a container (AKA Monad) that represents the result of an operation.
 * It is a container that allows us to handle the effects that are outside the function's scope, so that
 * we don't mess up the functions handling the effects, thus keeping the function code clean.
 * Often explained as a "box with a special way to open it", it can be used to chain operations
 * together while isolating potential complications within the computation.
 * <p>
 * An Outcome can represent success or failure. As a result, it can contain typed payloads for success and failure.
 * Failure payloads are of type {@code FailureDescription} and success payloads are of type {@code <SV>}.
 *
 * <h4>Success</h4>
 * A <code>Success</code> represents the successful completion of an operation. It may contain a <code>value</code> of type {@code <SV>} that is the computed
 * result of the operation. If a success is not guaranteed to have a <code>value</code> when the operation is successful I would recommend
 * having {@code <SV>} be an {@code Optional<VT>} where {@code VT} is the type of the value. A <code>Success</code> will not have any a failure payload.
 *
 * <h4>Failure</h4>
 * A <code>Failure</code> represents a wholly unsuccessful completion of an operation. It <em>will</em> contain a failure payload of type {@code FailureDescription}
 * that describes the failure. It will not contain a success payload.
 *
 * @param <SV> Success payload class. Accessible in successes
 *
 * @see Success
 * @see Failure
 *
 * @author Jim Mochel
 */

public sealed interface Outcome<SV> permits Failure, Success
{
    /**
     * Returns <em>true</em> if this outcome has a success payload.
     * <p>
     * Having a success payload is distinct from being a success.
     * A success will <b>always</b>  have a success payload.
     * A failure will <b>never</b> have a success payload.
     *
     * @return <em>true</em> if this outcome has a success payload,<em>false</em> otherwise.
     */

    boolean hasSuccessPayload();

    /**
     * Returns <em>true</em> if this outcome has a failure payload.
     * <p>
     * Having a failure payload is distinct from being a failure.
     * A failure will <b>always</b> have a failure payload.
     * A success will <b>never</b> have a failure payload.
     *
     * @return <em>true</em> if this outcome has a failure payload,<em>false</em> otherwise.
     */

    boolean hasFailurePayload();

    /**
     * Returns the success payload if this outcome has one.
     *
     * @return payload associated with the success. FIXIT May be null?
     *
     * @throws UnsupportedOperationException if there is no success payload available
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *      return outcome.get();
     * }
     * </pre>
     */

    SV get();

    /**
     * Return supplied outcome if this outcome is a success, otherwise return the existing outcome
     *
     * @param supplier function that supplies a new outcome. Not null.
     *
     * @return populated Outcome.
     *
     * <b>Example 1</b>
     * {@snippet :
     *   // Return a new Success with value 21 if the current outcome is a Success
     *   var newOutcome = outcome.ifSuccess(() -> Outcomes.succeed(21));
     * }
     */

    Outcome<SV> ifSuccess(Supplier<Outcome<SV>> supplier);

    /**
     * If this outcome is a success transform to a new outcome
     *
     * @param transform function that supplies a new outcome from an existing outcome. Not null
     *
     * @return transformed Success if success, Failure if failure.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newOutcome = outcome.ifSuccessApply(this::outcomeTransform);
     * }
     *
     */

    Outcome<SV> ifSuccess(Function<SV, Outcome<SV>> transform);

    /**
     * Executes action if this outcome is a success, takes no action otherwise.
     *
     * @param action the function that takes action based on success. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newOutcome = outcome.onSuccess(x -> log.info("{}", x.get()));
     * }
     */

    void onSuccess(Consumer<Outcome<SV>> action);

    /**
     * Returns the supplied outcome if this outcome is a failure, otherwise returns the existing outcome.
     *
     * @param supplier the function that supplies a new outcome. Not null.
     *
     * @return the existing outcome if success, new Outcome if failure.
     *
     * <h4>Example:</h4>
     * {@snippet :
     *   var newOutcome = outcome.ifFailure(() -> Outcomes.succeed(21));
     * }
     *
     */

    Outcome<SV> ifFailure(Supplier<Outcome<SV>> supplier);

    /**
     * Returns a transformed outcome if this outcome is a failure
     *
     * @param transform the function that creates a new outcome from the existing outcome. Not null.
     *
     * @return the existing outcome if not a failure, a new outcome otherwise.
     *
     * <p><h4>Example:</h4>
     * {@snippet :
     *   var newOutcome = outcome.ifFailureTransform(this::outcomeTransform);
     * }
     *
     */

    Outcome<SV> ifFailure(@NonNull Function<Outcome<SV>, Outcome<SV>> transform);

    /**
     * Executes action if this outcome is a failure, takes no action otherwise.
     *
     * @param action the function that takes action based on failure. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newOutcome = outcome.onFailure(x -> log.info("{}", x.get()));
     * }
     *
     */

    Outcome<SV> onFailure(@NonNull Consumer<Failure<SV>> action);

    /**
          * If this outcome is a failure execute the failure action, if success execute the success action.
 *
 * @param successAction the action to execute if this is a success
 * @param failureAction the action to execute if this is a failure
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newOutcome = outcome.on(x -> log.info("{}", x.get()), y -> log.error("Nope!"));
     * }
     */

    void on(Consumer<Outcome<SV>> successAction, Consumer<Outcome<SV>> failureAction);

    <SV2> Outcome<SV2> map(@NonNull Function<SV,SV2> transform);

    <SV2> Outcome<SV2> flatMap(@NonNull Function<SV,Outcome<SV2>> transform);

    default <RT> RT transform(@NonNull Function<Outcome<SV>, RT> transform)
    {
      return transform.apply(this);
    }

    default <RT> RT transform(@NonNull Function<Success<SV>, RT> successTransform, @NonNull Function<Failure<SV>, RT> failureTransform)
    {
        if (this instanceof Success<SV> success)
        {
            return successTransform.apply(success);
        }


        if (this instanceof Failure<SV> failure)
        {
            return failureTransform.apply(failure);
        }

        // This CAN NEVER happen
        throw new IllegalStateException("Outcome is not a success or failure");
    }




    /**
     * Attempt to execute the given supplier and return the outcome
     *
     * @param supplier function that supplies a new value. Not null.
     *
     * @return populated Success if success, Failure if failure.
     *
     * @param <SV2> Type of the supplied value
     */

    static <SV2> Outcome<SV2> attempt(@NonNull ExceptionalSupplier<SV2> supplier)
    {
        checkNotNull(supplier, "Supplier cannot be null");

        try
        {
            return new Success<>(supplier.get());
        }
        catch (Exception e)
        {
            return new Failure<>(FailureDescription.of()
                    .cause(e)
                    .build());
        }
    }


}
