package org.saltations.endeavour;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * @return payload associated with the success if a {@code Value}, a null if the result is {@code NoValue}.
     *
     * @throws UnsupportedOperationException If called on a {@code Failure}
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
     * Returns an Optional containing the success payload if this outcome is a success, otherwise returns an empty Optional.
     *
     * @return Optional containing the success payload if this outcome is a success, otherwise an empty Optional.
     */

    default Optional<T> opt()
    {
        return Optional.ofNullable(get());
    }

    /**
     * Maps {@code Result<T>} to  {@code Result<U>} by mapping {@code <T>} to {@code <U>}
     * <p>
     * <h4>Note</h4>
     * Any mapping function must handle nulls as one of the values.
     *
     * @param mapping a mapping function for T to U
     *
     * @return mapped result
     *
     * @param <U>
     */

    <U> Result<U> map(@NonNull Function<T,U> mapping);

    /**
     * Maps the unwrapped payload of type {@code T} to {@code Result<U>}
     * <p>
     * <h4>Note</h4>
     * Any mapping function used within the flat map must handle nulls as one of the values.
     *
     * @param mapping a mapping function for T to  {@code Result<U>}
     *
     * @return mapped result
     *
     * @param <U>
     */

    <U> Result<U> flatMap(@NonNull Function<T,Result<U>> mapping);

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

    void act(Consumer<Result<T>> action);

    /**
     * Executes action if this outcome is a success, takes no action otherwise.
     *
     * @param action the function that takes action based on success. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newResult = outcome.actOnSuccess(x -> log.info("{}", x.get()));
     * }
     */

     Result<T> actOnSuccess(Consumer<Success<T>> action);

    /**
     * Executes action if this outcome is a failure, takes no action otherwise.
     *
     * @param action the function that takes action based on failure. Not null.
     *
     * <p><b>Example:</b>
     * {@snippet :
     *   var newResult = outcome.actOnFailure(x -> log.info("{}", x.get()));
     * }
     */

     Result<T> actOnFailure(@NonNull Consumer<Failure<T>> action);




    /**
     * Return supplied outcome if this outcome is a success, otherwise return the existing outcome
     *
     * @param supplyOnSuccess function that supplies a new outcome. Not null.
     *
     * @return populated Result.
     *
     * <b>Example 1</b>
     * {@snippet :
     *   // Return a new Success with value 21 if the current outcome is a Success
     *   var newResult = outcome.ifSuccess(() -> Try.succeed(21));
     * }
     */

    Result<T> onSuccess(Supplier<Result<T>> supplyOnSuccess);

    /**
     * Returns the supplied outcome if this outcome is a failure, otherwise returns the existing outcome.
     *
     * @return the existing outcome if success, new Result if failure.
     *
     * <h4>Example:</h4>
     * {@snippet :
     *   var newResult = outcome.ifFailure(() -> Try.succeed(21));
     * }
     *
     */

     Result<T> onFailure(Supplier<Result<T>> supplyOnFailure);

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

    Result<T> onSuccess(Function<T, Result<T>> successTransform);

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

    Result<T> onFailure(@NonNull Function<Result<T>, Result<T>> failureTransform);



    default <RT> RT transform(@NonNull Function<Result<T>, RT> transform)
    {
      return transform.apply(this);
    }

}
