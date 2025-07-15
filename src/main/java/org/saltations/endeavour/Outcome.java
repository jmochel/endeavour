package org.saltations.endeavour;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A generic interface for outcomes of operations.
 * <p>
 * An <code>Outcome</code> is a container (AKA Monad) that represents the result of an operation.
 * It is a container that allows us to handle the effects that are outside the function’s scope, so that
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
 * @param <V> Success payload class. Accessible in successes.
 *
 * @see Success
 * @see Failure
 *
 * @author Jim Mochel
 */

public sealed interface Outcome<V> permits Failure, Success
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

    V get();

    /**
     * Returns an Optional containing the success payload if this outcome is a success, otherwise returns an empty Optional.
     *
     * @return Optional containing the success payload if this outcome is a success, otherwise an empty Optional.
     */

    default Optional<V> opt()
    {
        return Optional.ofNullable(get());
    }

    /**
     * Executes action if this outcome is a success, takes no action otherwise.
     *
     * @param successConsumer the function that takes action based on success. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newOutcome = outcome.onSuccess(x -> log.info("{}", x.get()));
     * }
     */

     Outcome<V> consumeSuccess(Consumer<Outcome<V>> successConsumer);

    /**
     * Executes action if this outcome is a failure, takes no action otherwise.
     *
     * @param failureConsumer the function that takes action based on failure. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newOutcome = outcome.onFailure(x -> log.info("{}", x.get()));
     * }
     */

     Outcome<V> consumeFailure(@NonNull Consumer<Failure<V>> failureConsumer);

     /**
      * If this outcome is a failure execute the failure action, if success execute the success action.
      *
      * @param successConsumer the action to execute if this is a success
      * @param failureConsumer the action to execute if this is a failure
      *
      * <p><b>Example:</b>
      * {@snippet :
      *   var newOutcome = outcome.on(x -> log.info("{}", x.get()), y -> log.error("Nope!"));
      * }
      */
 
     void consume(Consumer<Outcome<V>> successConsumer, Consumer<Outcome<V>> failureConsumer);

    /**
     * Return supplied outcome if this outcome is a success, otherwise return the existing outcome
     *
     * @param supplyOnSuccess function that supplies a new outcome. Not null.
     *
     * @return populated Outcome.
     *
     * <b>Example 1</b>
     * {@snippet :
     *   // Return a new Success with value 21 if the current outcome is a Success
     *   var newOutcome = outcome.ifSuccess(() -> Outcomes.succeed(21));
     * }
     */

    Outcome<V> onSuccess(Supplier<Outcome<V>> supplyOnSuccess);

    /**
     * Returns the supplied outcome if this outcome is a failure, otherwise returns the existing outcome.
     *
     * @param outcomeSupplier the function that supplies a new outcome. Not null.
     *
     * @return the existing outcome if success, new Outcome if failure.
     *
     * <h4>Example:</h4>
     * {@snippet :
     *   var newOutcome = outcome.ifFailure(() -> Outcomes.succeed(21));
     * }
     *
     */

     Outcome<V> onFailure(Supplier<Outcome<V>> supplyOnFailure);

    /**
     * If this outcome is a success transform to a new outcome
     *
     * @param successTransform function that supplies a new outcome from an existing outcome. Not null
     *
     * @return transformed Success if success, Failure if failure.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newOutcome = outcome.ifSuccessApply(this::outcomeTransform);
     * }
     *
     */

    Outcome<V> onSuccess(Function<V, Outcome<V>> successTransform);

    /**
     * Returns a transformed outcome if this outcome is a failure
     *
     * @param faiulureTransform the function that creates a new outcome from the existing outcome. Not null.
     *
     * @return the existing outcome if not a failure, a new outcome otherwise.
     *
     * <p><h4>Example:</h4>
     * {@snippet :
     *   var newOutcome = outcome.ifFailureTransform(this::outcomeTransform);
     * }
     *
     */

    Outcome<V> onFailure(@NonNull Function<Outcome<V>, Outcome<V>> failureTransform);

    default <RT> RT transform(@NonNull Function<Success<V>, RT> successTransform, @NonNull Function<Failure<V>, RT> failureTransform)
    {
        return switch (this) {
            case Success<V> success -> successTransform.apply(success);
            case Failure<V> failure -> failureTransform.apply(failure);
            // default -> throw new IllegalStateException("Outcome is not a success or failure");
        };      
    }

    <V2> Outcome<V2> map(@NonNull Function<V,V2> transform);

    <V2> Outcome<V2> flatMap(@NonNull Function<V,Outcome<V2>> transform);

    default <RT> RT transform(@NonNull Function<Outcome<V>, RT> transform)
    {
      return transform.apply(this);
    }

    /**
     * Attempt to execute the given supplier and return the outcome
     *
     * @param supplier function that supplies a new value. Not null.
     *
     * @return populated Success if success, Failure if failure.
     *
     * @param <V2> Type of the supplied value
     */

    static <V2> Outcome<V2> attempt(@NonNull ExceptionalSupplier<V2> supplier)
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
