package org.saltations.endeavour;

import java.util.Optional;
import java.util.function.Function;

import lombok.NonNull;


/**
 * A generic interface for outcomes of operations.
 * <p>
 * An <code>Result</code> is a container (AKA Monad) that represents the result of an operation.
 * It is a container that allows us to handle the effects that are outside the function’s scope so that
 * we don't mess up the functions handling the effects, thus keeping the function code clean.
 * Often explained as a "box with a special way to open it", it can be used to chain operations
 * together while isolating potential complications within the computation.
 * <p>
 * <h4>Success</h4>
 * A <code>Success</code> represents the successful completion of an operation. Successes may be either a {@code Value} (guaranteed to have a payload of type <V>)
 * or it will be a {@code NoValue} which is guaranteed not to have a payload of type <V> associated with it.
 *
 * <h4>Failure</h4>
 * A <code>Failure</code> represents an unsuccessful completion of an operation. It <em>will</em> contain a description of the failure of type {@code FailureDescription}
 * It will not contain a success payload.
 *
 * @param <T> Success payload type. Accessible in successes.
 *
 * @see Value
 * @see Failure
 *
 * @author Jim Mochel
 */

public sealed interface Result<T> permits Failure, Success
{
    /**
     * Returns <em>true</em> if this outcome has a success payload.
     * <p>
     * Having a success payload is distinct from being a success.
     * A {@code Value} will <b>always</b> have a payload.
     * A {@code NoValue} will <b>never</b> have a payload.
     * A {@code Failure}  will <b>never</b> have a success payload.
     *
     * @return <em>true</em> if this outcome has a success payload,<em>false</em> otherwise.
     */

    boolean hasPayload();

    /**
     * Returns the payload if this outcome has one.
     *
     * @return payload associated with the success if a {@code Value}, a null if the result is {@code NoValue}, 
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
     * Returns an {@code Optional} containing the success payload if this outcome is a {@code Value}, otherwise returns an empty {@code Optional}.
     *
     * @return {@code Optional} containing the success payload if this outcome is a {@code Value}, otherwise an empty {@code Optional}.
     */

    default Optional<T> opt()
    {
        return switch (this) {
            case Value<T> value -> Optional.of(value.get());
            case NoValue<T> noValue -> Optional.empty();
            case Failure<T> failure -> throw new IllegalStateException("Cannot get optional from a failure: " + failure.getTitle() + " - " + failure.getDetail());
        };
    }

    /**
     * Maps a internal payload to {@code Result<U>} by mapping payload of type {@code <T>} to payload of type {@code <U>}
     *
     * @param mapping a mapping function for T to U. <b>Not null.</b> <b>Must handle nulls from the {@code Result#get()}. method</b>
     *
     * @return mapped result
     *
     * @param <U>
     */

    <U> Result<U> map(@NonNull Function<T,U> mapping);

    /**
     * Maps the unwrapped payload of type {@code T} to {@code Result<U>}
     *
     * @param mapping a mapping function for T to  {@code Result<U>}.  <b>Not null.</b> <b>Must handle nulls.</b>
     *
     * @return mapped result
     *
     * @param <U>
     */

    <U> Result<U> flatMap(@NonNull Function<T,Result<U>> mapping);

    /**
     * Reduces the {@code Result<T>} to a single value of type {@code V} using a fold operation.
     * This follows the standard monadic fold pattern where:
     * - For Success: applies the success function to the contained value
     * - For Failure: applies the failure function to the failure details
     *
     * @param onSuccess function to apply when this is a success with the contained value
     * @param onFailure function to apply when this is a failure with the failure details
     * 
     * @param <V> the type of the value to reduce to
     * 
     * @return the reduced value
     */

    default <V> V reduce(@NonNull Function<T, V> onSuccess, @NonNull Function<Failure<T>, V> onFailure)
    {
        return switch (this) {
            case Value<T> value -> onSuccess.apply(value.get());
            case NoValue<T> noValue -> onSuccess.apply(null);
            case Failure<T> failure -> onFailure.apply(failure);
        };
    }

    /**
     * Consumes any {@code Result}
     *
     * @param action the action to execute
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newResult = outcome.act(x -> log.info("{}", x.get()));
     * }
     */

    default void act(ExceptionalConsumer<Result<T>> action)
    {
        action.accept(this);
    }

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
     * Supplies a new outcome from existing success, otherwise returns the existing outcome.
     *
     * @param supplier function that supplies a new outcome. <b>Not null.</b>
     *
     * @return populated Result.
     *
     * <b>Example 1</b>
     * {@snippet :
     *   // Return a new Success with value 21 if the current outcome is a Success
     *   var newResult = outcome.ifSuccess(() -> Try.succeed(21));
     * }
     */

    Result<T> orElse(ExceptionalSupplier<Result<T>> supplier);

    /**
     * Returns the supplied outcome if this outcome is a failure, otherwise returns the existing outcome.
     *
     * @param supplier function that supplies a new outcome. <b>Not null.</b>
     * 
     * @return the existing outcome if success, new Result if failure.
     *
     * <h4>Example:</h4>
     * {@snippet :
     *   var newResult = outcome.ifFailure(() -> Try.succeed(21));
     * }
     */

     Result<T> orElseGet(ExceptionalSupplier<Result<T>> supplier);

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

    Result<T> flatMapOnSuccess(ExceptionalFunction<T, Result<T>> mapping);

    /**
     * Returns a transformed outcome if this outcome is a failure
     *
     * @param failureTransform the function that creates a new outcome from the existing outcome. Not null.
     *
     * @return the existing outcome if not a failure, a new outcome otherwise.
     *
     * <p><h4>Example:</h4>
     * {@snippet :
     *   var newResult = outcome.ifFailureTransform(this::outcomeTransform);
     * }
     *
     */

    Result<T> mapOnFailure(@NonNull ExceptionalFunction<Result<T>, Result<T>> mapping);


}
