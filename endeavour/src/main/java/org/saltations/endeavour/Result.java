package org.saltations.endeavour;

import java.util.Optional;
import java.util.function.Function;

import lombok.NonNull;


/**
 * An <code>Result</code> is a container (AKA Monad) that represents the result of an operation.
 * <p>
 * It is a container that allows us to handle the effects that are outside the function's scope so that
 * we don't mess up the functions handling the effects, thus keeping the function code clean.
 * Often explained as a "box with a special way to open it", it can be used to chain operations
 * together while isolating potential complications within the computation.
 * <p>
 * <h4>Failure</h4>
 * A <code>Failure</code> represents an unsuccessful completion of an operation. It <em>will</em> contain a description of the failure of type {@code FailureDescription}
 * It will not contain a success payload.
 * <p>
 * <h4>Success</h4>
 * A <code>Success</code> represents the successful completion of an operation. Successes may be either a quantitative success {@code QuantSuccess} (guaranteed to have a payload of type <V>)
 * or it will be a  qualitative success{@code QualSuccess} which is guaranteed not to have a payload.
 * <p>
 *
 * <h4>IMPORTANT DESIGN NOTE:</h4>
 * 
 * <b></b> Both {@code QuantSuccess<T>} and {@code QualSuccess<T>} are considered "successful" outcomes.
 * The distinction is between operations that succeed with a value vs operations that succeed without producing a value
 * (e.g., void operations, deletions, updates). This design choice enables clean monadic operations where both success
 * cases are handled uniformly, while failures are handled separately. This is intentional and should not be flagged
 * as an issue in code reviews. * 
 * 
 * @param <T> Success payload type. Accessible in {@code QuantSuccess}.
 *
 * @see QuantSuccess
 * @see QualSuccess
 * @see Failure
 * 
 * @author Jim Mochel
 */

public sealed interface Result<T> permits Failure, Success
{
    /**
     * Returns <em>true</em> if this outcome has a success payload.
     * 
     * @return <em>true</em> if this outcome has a success payload,<em>false</em> otherwise.
     * 
     */

    boolean hasPayload();

    /**
     * Returns the payload if this outcome has one.
     *
     * @return payload associated with the success if a {@code QuantSuccess}, a null if the result is {@code QualSuccess}, 
     *         throws {@code IllegalStateException} if the result is a {@code Failure}.
     * 
     * @throws IllegalStateException if called on a {@code Failure}
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     *      return outcome.get();
     * }
     * </pre>
     */

    T get();

    /**
     * Returns an {@code Optional} containing the success payload (if any).
     * 
     * @return {@code Optional} containing the success payload if this outcome is a {@code QuantSuccess}, otherwise an empty {@code Optional}.
     */
    Optional<T> opt();

    /**
     * Maps a payload to {@code Result<U>} by mapping payload of type {@code <T>} to payload of type {@code <U>}
     *
     * @param mapping a mapping function for T to U. <b>Not null.</b> <b>Must handle nulls from the {@code Result#get()}. method</b>
     *
     * @return mapped result
     * 
     * @throws Exception if the mapping function throws a checked exception
     *
     * @param <U>
     */

    <U> Result<U> map(@NonNull CheckedFunction<T,U> mapping) throws Exception;

    /**
     * Maps the unwrapped payload of type {@code T} to {@code Result<U>}
     *
     * @param mapping a mapping function for T to {@code Result<U>}. <b>Not null.</b> <b>Must handle nulls.</b>
     *
     * @return mapped result
     * 
     * @throws Exception if the mapping function throws a checked exception
     *
     * @param <U>
     */

    <U> Result<U> flatMap(@NonNull CheckedFunction<T,Result<U>> mapping) throws Exception;

    /**
     * If this outcome is a success transform to a new outcome
     *
     * @param successTransform function that supplies a new outcome from an existing outcome. Not null
     *
     * @return transformed Success if success, Failure if failure.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newResult = outcome.ifSuccessApply(this::outcomeTransform);
     * }
     *
     */

     Result<T> flatMap(ExceptionalFunction<T, Result<T>> mapping);


    /**
     * Reduces the {@code Result<T>} to a single value of type {@code V} using a fold operation.
     * This follows the standard monadic fold pattern where:
     * - For Success: applies the success function to the contained value
     * - For Failure: applies the failure function to the failure details
     *
     * @param onSuccess function to apply when this is a success (including QualSuccess cases)
     * @param onFailure function to apply when this is a failure
     * 
     * @param <V> the type of the value to reduce to
     * 
     * @return the reduced value
     */

    default <V> V reduce(@NonNull Function<T, V> onSuccess, @NonNull Function<Failure<T>, V> onFailure)
    {
        return switch (this) {
            case QuantSuccess<T> value -> onSuccess.apply(value.get());
            case QualSuccess<T> qualSuccess -> onSuccess.apply(null);
            case Failure<T> failure -> onFailure.apply(failure);
        };
    }

    /**
     * Consumes the payload value from this {@code Result} if it's a success.
     * For failures, no action is taken.
     *
     * @param action the action to execute on the payload value. <b>Not null.</b>
     * 
     * @throws Exception if the action throws a checked exception
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   outcome.act(value -> log.info("Value: {}", value));
     * }
     */
    void act(CheckedConsumer<T> action) throws Exception;

    /**
     * Executes action if this outcome is a success, takes no action otherwise.
     *
     * @param action the function that takes action based on success. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newResult = outcome.ifSuccess(x -> log.info("{}", x.get()));
     * }
     */

     Result<T> ifSuccess(ExceptionalConsumer<Success<T>> action);

    /**
     * Executes action if this outcome is a failure, takes no action otherwise.
     *
     * @param action the function that takes action based on failure. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newResult = outcome.ifFailure(x -> log.info("{}", x.get()));
     * }
     */

     Result<T> ifFailure(@NonNull ExceptionalConsumer<Failure<T>> action);

    /**
     * Returns the alternate result if this outcome is a failure, otherwise returns the existing outcome.
     *
     * @param alternateResult the alternate result to return if this is a failure. <b>Not null.</b>
     *
     * @return the alternate result if failure, existing result if success.
     *
     * <b>Example 1</b>
     * {@snippet :
     *   // Return a new Success with value 21 if the current outcome is a Success
     *   var newResult = outcome.orElse(Try.succeed(666L));
     * }
     */

    Result<T> orElse(Result<T> alternateResult);

    /**
     * Returns the supplied outcome if this outcome is a failure, otherwise returns the existing outcome.
     * Uses a CheckedSupplier that can throw checked exceptions.
     *
     * @param supplier function that supplies a new result. <b>Not null.</b>
     * 
     * @return the existing outcome if success, new Result if failure.
     *
     * <h4>Example:</h4>
     * {@snippet :
     *   var newResult = outcome.orElseGet(() -> Try.succeed(21));
     * }
     */

     Result<T> orElseGet(CheckedSupplier<Result<T>> supplier);

}
