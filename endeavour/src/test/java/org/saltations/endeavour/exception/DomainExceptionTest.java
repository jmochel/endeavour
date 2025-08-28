package org.saltations.endeavour.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class DomainExceptionTest {

    @Nested
    class GivenMessageOnlyConstructor {
        @Test
        void whenCreatingWithSimpleMessageThenPropertiesAreSet() {
            // Given a simple message
            String message = "Simple message";
            var exception = new DomainException(message);

            // Then all properties are set correctly
            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
            assertNotNull(exception.getTraceId());
            assertNotNull(exception.getTimestamp());
        }

        @Test
        void whenCreatingWithFormattedMessageThenMessageIsFormatted() {
            // Given a formatted message with arguments
            String template = "User {} not found";
            String arg = "john";
            var exception = new DomainException(template, arg);

            // Then the message is formatted correctly
            assertEquals("User john not found", exception.getMessage());
            assertNull(exception.getCause());
            assertNotNull(exception.getTraceId());
            assertNotNull(exception.getTimestamp());
        }

        @Test
        void whenCreatingMultipleInstancesThenTraceIdsAreUnique() {
            // Given multiple exceptions
            String message1 = "First";
            String message2 = "Second";
            var exception1 = new DomainException(message1);
            var exception2 = new DomainException(message2);

            // Then trace IDs are unique
            assertNotNull(exception1.getTraceId());
            assertNotNull(exception2.getTraceId());
            assertNotSame(exception1.getTraceId(), exception2.getTraceId());
        }
    }

    @Nested
    class GivenMessageAndCauseConstructor {
        @Test
        void whenCreatingWithCauseThenPropertiesAreSet() {
            // Given a cause and message
            var cause = new RuntimeException("Root cause");
            String message = "Operation failed";
            var exception = new DomainException(cause, message);

            // Then all properties are set correctly
            assertEquals(message, exception.getMessage());
            assertSame(cause, exception.getCause());
            assertNotNull(exception.getTraceId());
            assertNotNull(exception.getTimestamp());
        }

        @Test
        void whenCreatingWithCauseAndFormattedMessageThenBothAreSet() {
            // Given a cause and formatted message
            var cause = new RuntimeException("Root cause");
            String template = "User {} not found";
            String arg = "john";
            var exception = new DomainException(cause, template, arg);

            // Then both are set correctly
            assertEquals("User john not found", exception.getMessage());
            assertSame(cause, exception.getCause());
            assertNotNull(exception.getTraceId());
            assertNotNull(exception.getTimestamp());
        }

    }
} 