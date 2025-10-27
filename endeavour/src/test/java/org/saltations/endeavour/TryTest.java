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
            assertResultIsSuccess(result);
            assertAll("Success",
                    () -> assertEquals("OK",result.get(), "Value")
            );
        }

        @Test
        @Order(2)
        void canCreateSuccessWithoutValue()
        {
            var result = Try.success();

            assertResultIsSuccess(result);

            assertAll("Success",
                    () -> assertEquals(Boolean.TRUE, result.get(), "Value")
            );
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

            var failure = assertResultIsFailure(result);

            assertAll("Failure", () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, failure.getType(), "Type"), () -> assertNull(failure.getCause(), "Cause"));

            assertEquals("", failure.getDetail(), "Detail");
        }

        @Test
        @Order(4)
        void canCreateFailureWithDetails()
        {
            var result = Try.failureWithDetails("{} did it", "Bozo");

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, failure.getType(), "Type"),
                    () -> assertNull(failure.getCause(), "Cause"),
                    () -> assertEquals("Bozo did it", failure.getDetail(), "Detail")
            );
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

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, failure.getType(), "Type"),
                    () -> assertNull(failure.getCause(), "Cause")
            );

            assertEquals("Strange Category", failure.getTitle(), "Title");
        }

        @Test
        @Order(4)
        void canCreateTitledFailureWithDetail()
        {
            var detail = "This went really bad";
            var result = Try.titledFailureWithDetails("Really Bad", "Details: {} Bad", "Really Really");

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, failure.getType(), "Type"),
                    () -> assertNull(failure.getCause(), "Cause")
            );

            assertEquals("Really Bad", failure.getTitle(), "Title");
            assertEquals("Details: Really Really Bad", failure.getDetail(), "Detail");
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

            var failure = assertResultIsFailure(result);

            assertAll("Failure", () -> assertEquals(ExemplarFailure.POTENTIALLY_FATAL, failure.getType(), "Type"), () -> assertNull(failure.getCause(), "Cause"));

            assertEquals("", failure.getDetail(), "Detail");
        }

        @Test
        @Order(6)
        void canCreateTypedFailureWithDetail()
        {
            var detail = "This went really bad";
            var result = Try.typedFailure(ExemplarFailure.POTENTIALLY_FATAL, detail);

            var failure = assertResultIsFailure(result);

            assertAll("Failure", () -> assertEquals(ExemplarFailure.POTENTIALLY_FATAL, failure.getType(), "Type"), () -> assertNull(failure.getCause(), "Cause"));

            assertEquals(detail, failure.getDetail(), "Detail");
        }

        @Test
        @Order(8)
        void canCreateTypedFailureWithDetails()
        {
            var result = Try.typedFailureWithDetails(ExemplarFailure.NOT_REALLY_SO_BAD, "Details: {} Bad", "Really Really");
            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(ExemplarFailure.NOT_REALLY_SO_BAD, failure.getType(), "Type"),
                    () -> assertTrue(failure.getDetail().contains("Details: Really Really Bad"), "Detail contains expanded arg"),
                    () -> assertNull(failure.getCause(), "Cause"));
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

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, failure.getType(), "Type"),
                    () -> assertEquals("", failure.getDetail(), "Detail"),
                    () -> assertNotNull(failure.getCause(), "Cause")
            );
        }

        @Test
        @Order(22)
        void canCreateCausedFailureWithExceptionAndType()
        {
            var cause = new Exception();
            var result = Try.causedFailure(cause, ExemplarFailure.POTENTIALLY_FATAL);

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(ExemplarFailure.POTENTIALLY_FATAL, failure.getType(), "Type"),
                    () -> assertEquals("", failure.getDetail(), "Detail"),
                    () -> assertNotNull(failure.getCause(), "Cause")
            );
        }

        @Test
        @Order(24)
        void canCreateCausedFailureWithExceptionAndTypeAndDetail()
        {
            var cause = new Exception();
            var detail = "This went really bad";
            var result = Try.causedFailure(cause, ExemplarFailure.POTENTIALLY_FATAL, detail);

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(ExemplarFailure.POTENTIALLY_FATAL, failure.getType(), "Type"),
                    () -> assertEquals(detail, failure.getDetail(), "Detail"),
                    () -> assertNotNull(failure.getCause(), "Cause")
            );
        }

        @Test
        @Order(26)
        void canCreateCausedFailureWithExceptionAndDetails()
        {
            var cause = new Exception();
            var detail = "This went really bad";
            var result = Try.causedFailureWithDetails(cause, detail);

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, failure.getType(), "Type"),
                    () -> assertEquals(detail, failure.getDetail(), "Detail"),
                    () -> assertNotNull(failure.getCause(), "Cause")
            );
        }

        @Test
        @Order(28)
        void canCreateCausedFailureWithTemplate()
        {
            var cause = new Exception();
            var template = "This is a template message";
            var result = Try.causedFailure(cause, ExemplarFailure.GENERAL, template);

            var failure = assertResultIsFailure(result);

            assertAll("Failure",
                    () -> assertEquals(ExemplarFailure.GENERAL, failure.getType(), "Type"),
                    () -> assertEquals(template, failure.getDetail(), "Detail"),
                    () -> assertNotNull(failure.getCause(), "Cause")
            );
        }

        @Test
        @Order(30)
        void canCreateCausedFailureWithArgs()
        {
            var providedExceptionMessage = "This is a provided exception message";

            var cause = new Exception(providedExceptionMessage);
            var result = Try.causedFailure(cause, ExemplarFailure.NOT_REALLY_SO_BAD, "widget1", "widget2");

            var failure = assertResultIsFailure(result);

            // @formatter:off
            assertAll("Failure",
                    () -> assertEquals(ExemplarFailure.NOT_REALLY_SO_BAD, failure.getType(), "Type"),
                    () -> assertEquals(providedExceptionMessage, failure.getDetail(), "Detail contains provided exception message"),
                    () -> assertNotNull(failure.getCause(), "Cause")
            );
            // @formatter:on
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
            var value = result.get();
        }

        @Test
        @Order(31)
        void whenProvidedValueThenAbleToRetrieveRatherThanThrowException() throws Throwable
        {
            var result = simpleReturnOfOKString();
            var value = result.get();
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

    private Failure<?> assertResultIsFailure(Result<?> result)
    {
        if (result instanceof QuantSuccess<?>)
        {
            fail("Result should be a Failure");
        }

        assertAll("Result",
                () -> assertFalse(result.hasPayload(),"Is Not Success")
        );

        return (Failure<?>) result;
    }


    private void assertResultIsSuccess(Result<?> result)
    {
        if (result instanceof Failure<?>)
        {
            fail("Result should be a Success");
        }

        assertAll("Result",
                () -> assertTrue(result.hasPayload(),"Has Success value")
        );

    }


}
