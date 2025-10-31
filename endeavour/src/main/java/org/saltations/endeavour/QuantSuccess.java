package org.saltations.endeavour;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a successful operation that produced a quantified result value.
 * This is the concrete implementation of {@link Success} for cases where
 * the operation succeeded and produced a meaningful value.
 *
 * @param <T> the type of the successful result value
 */

public record QuantSuccess<T>(T value) implements Success<T> {

    @Override
    public boolean hasPayload()
    {
        return true;
    }

    @Override
    public T get()
    {
        return value;
    }
    
    @Override
    public Optional<T> opt()
    {
        return Optional.of(value);
    }

    @Override
    public <U> Result<U> map(CheckedFunction<T, U> mapping) throws Exception
    {
        Objects.requireNonNull(mapping, "Mapping function cannot be null");
        
        // If the mapping transforms a payload into a null, we are returning a QualSuccess of an appropriate type,
        // otherwise we are returning a QuantSuccess with the new payload

        var newValue = mapping.apply(get());
        return Objects.isNull(newValue) ? new QualSuccess<U>() : new QuantSuccess<U>(newValue);
    }

    @Override
    public <U> Result<U> flatMap(CheckedFunction<T, Result<U>> mapping)
    {
        Objects.requireNonNull(mapping, "Mapping function cannot be null");

        try {
            return mapping.apply(value);
        } catch (Exception ex) {
            return new Failure<>(FailureDescription.of()
                .type(FailureDescription.GenericFailureType.GENERIC_EXCEPTION)
                .cause(ex)
                .build());
        }
    }

    public String toString()
    {
        return new StringBuilder("Success").append("[")
                                           .append(value)
                                           .append("]")
                                           .toString();
    }
}
