package org.saltations.endeavour.exception;

import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;

/**
 * A runtime exception that allows the user to pass in messages with parameters. Messages and parameters are
 * done using {@link org.slf4j.helpers.MessageFormatter} format strings.
 */

@Getter
public class FormattedUncheckedException extends RuntimeException
{
    /**
     * An exception really should have a serialization version.
     */

    private static final long serialVersionUID = 1L;

    /**
     * Constructor that takes {@link org.slf4j.helpers.MessageFormatter} format strings and parameters
     *
     * @param msg Formatting message. Uses {@link org.slf4j.helpers.MessageFormatter#format} notation.
     * @param args Objects as message parameters
     */

    public FormattedUncheckedException(String msg, Object... args) {
        super(MessageFormatter.basicArrayFormat(msg, args));
    }

    /**
     * Constructor that takes {@link org.slf4j.helpers.MessageFormatter} format strings and parameters
     *
     * @param e Root cause exception. Non-null.
     * @param msg Formatting message. Uses {@link org.slf4j.helpers.MessageFormatter#format} notation.
     * @param args Objects as message parameters
     */

    public FormattedUncheckedException(Throwable e, String msg, Object... args) {
        super(MessageFormatter.basicArrayFormat(msg, args), e);
    }
}
