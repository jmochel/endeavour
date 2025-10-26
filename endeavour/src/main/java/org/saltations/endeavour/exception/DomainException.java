package org.saltations.endeavour.exception;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

/**
 * A business domain error. This is an unchecked exception that indicates that an exceptional event has happened.
 * This exists separately from the Try that are used to indicate the result of a business operation.
 * They may be carried by the Try but the two should not have any dependencies on each other.
 * This primary purpose of this class is to provide a an exception that can be used to carry a trace id in layers that require exceptions.
 * It is expected that the exception will primarily used in the service layer.
 */

public class DomainException extends FormattedUncheckedException
{
     private static final long serialVersionUID = 1L;

    /**
     * Tracer id for the exception. This is used to track the exception through the system from generation to where it is logged.
     */

    @Getter
    private final UUID traceId = UUID.randomUUID();

    // Add timestamp for when the exception occurred
    @Getter
    private final Instant timestamp = Instant.now();

    public DomainException(String msg, Object... args)
    {
        super(msg, args);
    }

    public DomainException(Throwable e, String msg, Object... args)
    {
        super(e, msg, args);
    }

}
