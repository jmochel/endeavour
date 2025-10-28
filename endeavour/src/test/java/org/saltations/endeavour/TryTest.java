package org.saltations.endeavour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.saltations.endeavour.fixture.ReplaceBDDCamelCase;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.saltations.endeavour.fixture.ResultAssert.assertThat;

@Order(20)
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TryTest
{
    @Getter
    @AllArgsConstructor
    enum ExemplarFailure implements FailureType
    {
        GENERAL("Uncategorized error",""),
        POTENTIALLY_FATAL("Potentially Fatal error of some sort", ""),
        NOT_REALLY_SO_BAD("Not so bad problem", "Happened in widget [{}]");

        private final String title;
        private final String template;
    }

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GoldenPathSuccesses {
        @Test
        @Order(1)
        void canCreateSuccessWithValue()
        {
            var result = Try.success("OK");
            assertThat(result)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue("OK");
        }

        @Test
        @Order(2)
        void canCreateSuccessWithoutValue()
        {
            var result = Try.success();

            assertThat(result)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue(Boolean.TRUE);
        }
    }


    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SimpleUntypedFailures
    {
        @Test
        @Order(2)
        void canCreateSimplestFailure()
        {
            var result = Try.failure();

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC)
                .hasNoCause();
        }

        @Test
        @Order(4)
        void canCreateFailureWithDetails()
        {
            var result = Try.failureWithDetails("{} did it", "Bozo");

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC)
                .hasNoCause();
        }
    }


    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TitledFailures
    {
        @Test
        @Order(2)
        void canCreateTitledFailure()
        {
            var result = Try.titledFailure("Strange Category");

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC)
                .hasNoCause();
        }

        @Test
        @Order(4)
        void canCreateTitledFailureWithDetail()
        {
            var detail = "This went really bad";
            var result = Try.titledFailureWithDetails("Really Bad", "Details: {} Bad", "Really Really");

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC)
                .hasNoCause();
        }

    }

    @Nested
    @Order(6)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TypedFailures
    {
        @Test
        @Order(2)
        void canCreateTypedFailure()
        {
            var result = Try.typedFailure(ExemplarFailure.POTENTIALLY_FATAL);

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.POTENTIALLY_FATAL)
                .hasNoCause();
        }

        @Test
        @Order(6)
        void canCreateTypedFailureWithDetail()
        {
            var detail = "This went really bad";
            var result = Try.typedFailure(ExemplarFailure.POTENTIALLY_FATAL, detail);

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.POTENTIALLY_FATAL)
                .hasNoCause();
        }

        @Test
        @Order(8)
        void canCreateTypedFailureWithDetails()
        {
            var result = Try.typedFailureWithDetails(ExemplarFailure.NOT_REALLY_SO_BAD, "Details: {} Bad", "Really Really");
            
            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.NOT_REALLY_SO_BAD)
                .hasNoCause();
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CausedFailures
    {
        @Test
        @Order(20)
        void canCreateCausedFailure()
        {
            var cause = new Exception();
            var result = Try.causedFailure(cause);

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC)
                .hasCause()
                .hasCause(cause);
        }

        @Test
        @Order(22)
        void canCreateCausedFailureWithExceptionAndType()
        {
            var cause = new Exception();
            var result = Try.causedFailure(cause, ExemplarFailure.POTENTIALLY_FATAL);

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.POTENTIALLY_FATAL)
                .hasCause()
                .hasCause(cause);
        }

        @Test
        @Order(24)
        void canCreateCausedFailureWithExceptionAndTypeAndDetail()
        {
            var cause = new Exception();
            var detail = "This went really bad";
            var result = Try.causedFailure(cause, ExemplarFailure.POTENTIALLY_FATAL, detail);

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.POTENTIALLY_FATAL)
                .hasCause()
                .hasCause(cause);
        }

        @Test
        @Order(26)
        void canCreateCausedFailureWithExceptionAndDetails()
        {
            var cause = new Exception();
            var detail = "This went really bad";
            var result = Try.causedFailureWithDetails(cause, detail);

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(FailureDescription.GenericFailureType.GENERIC)
                .hasCause()
                .hasCause(cause);
        }

        @Test
        @Order(28)
        void canCreateCausedFailureWithTemplate()
        {
            var cause = new Exception();
            var template = "This is a template message";
            var result = Try.causedFailure(cause, ExemplarFailure.GENERAL, template);

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.GENERAL)
                .hasCause()
                .hasCause(cause);
        }

        @Test
        @Order(30)
        void canCreateCausedFailureWithArgs()
        {
            var providedExceptionMessage = "This is a provided exception message";

            var cause = new Exception(providedExceptionMessage);
            var result = Try.causedFailure(cause, ExemplarFailure.NOT_REALLY_SO_BAD, "widget1", "widget2");

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.NOT_REALLY_SO_BAD)
                .hasCause()
                .hasCause(cause);
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenSuccessfulOperation {

        @Test
        @Order(30)
        void whenProvidedValueThenAbleToRetrieve() throws Throwable
        {
            var result = simpleReturnOfOKString();
            assertThat(result)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue("OK");
        }

        @Test
        @Order(31)
        void whenProvidedValueThenAbleToRetrieveRatherThanThrowException() throws Throwable
        {
            var result = simpleReturnOfOKString();
            assertThat(result)
                .isSuccess()
                .isQuantSuccess()
                .hasPayload()
                .hasValue("OK");
        }

        Result<String> simpleReturnOfOKString()
        {
            return Try.success("OK");
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class GivenFailedOperation {


        @Test
        @Order(41)
        void whenRetrievingValueThenThrowsException() throws Throwable
        {
            var result = simpleReturnOfFailure();

            assertThat(result)
                .isFailure()
                .hasNoPayload()
                .hasFailureType(ExemplarFailure.NOT_REALLY_SO_BAD);
            assertThrows(IllegalStateException.class, () -> result.get(), "Should throw exception when trying to get value from failure");
        }

        Result<Object> simpleReturnOfFailure()
        {
            return Try.typedFailure(ExemplarFailure.NOT_REALLY_SO_BAD);
        }
    }

    @Nested
    @Order(20)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ToString {


        @Test
        @Order(2)
        void canShowSuccess()
        {
            assertEquals("Success[Jake]", Try.success("Jake").toString());
        }

        @Test
        @Order(4)
        void canShowFailure()
        {
            assertEquals("Failure[GENERIC:generic-failure:]", Try.failure().toString());
        }
    }

}
