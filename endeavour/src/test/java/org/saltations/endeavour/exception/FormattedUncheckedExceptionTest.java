package org.saltations.endeavour.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

class FormattedUncheckedExceptionTest {

    @Nested
    class WhenCreatingWithWithoutCause {

        @Test
        void wuthSimpleMessageThenExceptionMessageIsThatSimpleMessage() {
            var simpleMessage = "Simple message";
            var exception = new FormattedUncheckedException(simpleMessage);

            assertEquals(simpleMessage, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        void withTemplateAndArgsThenExceptionMessageIsTheExpandedTemplate() {
            var template = "User {} not found";
            var arg = "john";
            var exception = new FormattedUncheckedException(template, arg);

            assertEquals("User john not found", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        void withTemplateAndMultipleArgsThenExceptionMessageIsTheExpandedTemplate() {
            var template = "User {} has {} items";
            var name = "john";
            var count = 5;
            var exception = new FormattedUncheckedException(template, name, count);

            assertEquals("User john has 5 items", exception.getMessage());
            assertNull(exception.getCause());
        }
    }

    @Nested
    class WhenCreatingWithWithCause {

        @Test
        void withCauseThenCauseIsSetAndExceptionMessageIsTheProvidedMessage() {
            var cause = new RuntimeException("Root cause");
            var message = "Operation failed";
            var exception = new FormattedUncheckedException(cause, message);

            assertEquals(message, exception.getMessage());
            assertSame(cause, exception.getCause());
        }

        @Test
        void withCauseAndTemplateAndArgsThenCauseIsSetAndExceptionMessageIsTheExpandedTemplate() {
            
            var cause = new RuntimeException("Root cause");
            var template = "User {} not found";
            var arg = "john";

            var exception = new FormattedUncheckedException(cause, template, arg);

            assertEquals("User john not found", exception.getMessage());
            assertSame(cause, exception.getCause());
        }
    }

    @Test
    void whenCreatingWithDifferentArgumentTypesThenMessageIsFormatted() {
        // Given
        String message = "User {} has {} items and {} is active";
        String username = "john";
        int itemCount = 5;
        boolean isActive = true;

        // When
        var exception = new FormattedUncheckedException(message, username, itemCount, isActive);

        // Then
        assertThat(exception.getMessage()).contains(username, String.valueOf(itemCount), String.valueOf(isActive));
    }

    @Test
    void whenCreatingWithNullArgumentsThenMessageIsFormatted() {
        // Given
        String message = "User {} is {}";
        String username = "john";
        String status = null;

        // When
        var exception = new FormattedUncheckedException(message, username, status);

        // Then
        assertThat(exception.getMessage()).contains(username, "null");
    }

    @Test
    void whenCreatingWithEmptyMessageThenMessageIsEmpty() {
        // Given
        String message = "";

        // When
        var exception = new FormattedUncheckedException(message);

        // Then
        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    void whenCreatingWithNoPlaceholdersThenMessageIsUnchanged() {
        // Given
        String message = "Simple message with no placeholders";

        // When
        var exception = new FormattedUncheckedException(message);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void whenCreatingWithMorePlaceholdersThanArgumentsThenMessageIsFormatted() {
        // Given
        String message = "User {} has {} items and {} is active";
        String username = "john";

        // When
        var exception = new FormattedUncheckedException(message, username);

        // Then
        assertThat(exception.getMessage()).contains(username, "{}", "{}");
    }

    @Test
    void whenCreatingWithFewerPlaceholdersThanArgumentsThenMessageIsFormatted() {
        // Given
        String message = "User {}";
        String username = "john";
        String extraArg = "extra";

        // When
        var exception = new FormattedUncheckedException(message, username, extraArg);

        // Then
        assertThat(exception.getMessage()).contains(username);
    }

    @Test
    void whenCreatingWithDifferentExceptionTypesAsCauseThenCauseIsSet() {
        // Given
        String message = "Error occurred";
        Exception cause = new RuntimeException("Root cause");
        Error error = new OutOfMemoryError("Memory error");

        // When
        var exception1 = new FormattedUncheckedException(cause, message);
        var exception2 = new FormattedUncheckedException(error, message);

        // Then
        assertThat(exception1.getCause()).isSameAs(cause);
        assertThat(exception2.getCause()).isSameAs(error);
    }

    @Test
    void whenCreatingWithNullMessageThenThrowsNullPointerException() {
        // Given
        String message = null;

        // When/Then
        assertThatThrownBy(() -> new FormattedUncheckedException(message))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void whenCreatingWithNullCauseThenThrowsNullPointerException() {
        // Given
        String message = "Error occurred";
        Throwable cause = null;

        // When/Then
        assertThatThrownBy(() -> new FormattedUncheckedException(cause, message))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void whenCreatingWithNullMessageAndCauseThenThrowsNullPointerException() {
        // Given
        String message = null;
        Throwable cause = new RuntimeException("Root cause");

        // When/Then
        assertThatThrownBy(() -> new FormattedUncheckedException(cause, message))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void whenCreatingWithEmptyArgsArrayThenMessageIsFormatted() {
        // Given
        String message = "Simple message";
        Object[] args = new Object[0];

        // When
        var exception = new FormattedUncheckedException(message, args);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void whenCreatingWithNullArgsArrayThenMessageIsFormatted() {
        // Given
        String message = "Simple message";
        Object[] args = null;

        // When
        var exception = new FormattedUncheckedException(message, args);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void whenCreatingWithComplexObjectArgsThenMessageIsFormatted() {
        // Given
        String message = "User {} has {} items";
        String username = "john";
        List<String> items = List.of("item1", "item2", "item3");

        // When
        var exception = new FormattedUncheckedException(message, username, items);

        // Then
        assertThat(exception.getMessage()).contains(username, items.toString());
    }
} 