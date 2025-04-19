package org.saltations.endeavour;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Order(60)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("FailureAssay")
class FailureAssayTest
{
    private final MemoryAppender memoryAppender = new MemoryAppender();

    @BeforeAll
    public void setupLogCapture()
    {
        var logger = (Logger) LoggerFactory.getLogger(FailureAssay.class);
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());

        logger.setLevel(Level.WARN);
        logger.addAppender(memoryAppender);

        memoryAppender.start();
    }

    @AfterAll
    public void teardownLogCapture()
    {
        memoryAppender.stop();
    }

    @BeforeEach
    void clearLogCapture()
    {
        memoryAppender.reset();
    }

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WhenCreatedWithoutFailureType
    {
        @Test
        @Order(1)
        void andWithoutTitleOrDetailOrTemplateOrCauseThenAssayContainsMeaninglessTitle()
        {
            var failureAssay = FailureAssay.of()
                    .build();

            // @formatter:off
            assertAll("Assay",
                    () -> assertEquals(FailureAssay.FAILURE_ASSAY_IS_MEANINGLESS, failureAssay.getTitle(), "has meaningless title"),
                    () -> assertEquals("", failureAssay.getDetail(), "has meaningless detail")
            );
            // Verify that it logs a warning
            assertThat(memoryAppender.search("Failure assay was created without a title, detail or template or caused that would allow us to provide information.",Level.WARN)).hasSize(1);
            // @formatter:on
        }

        @Test
        @Order(2)
        void andWithOnlyACauseThenAssayContainsExceptionOnlyTitle()
        {
            var providedErrorMessage = "Dang!";
            var failureAssay = FailureAssay.of()
                    .cause(new RuntimeException(providedErrorMessage))
                    .build();

            // @formatter:off
            assertAll("Assay",
                    () -> assertEquals(FailureAssay.FAILURE_FROM_EXCEPTION, failureAssay.getTitle(), "has exception only title"),
                    () -> assertEquals(providedErrorMessage, failureAssay.getDetail(), "has detail derived from exception message")
            );

            // Verify that it logs a warning
            assertThat(memoryAppender.search("Failure assay was created with juist an exception and no other information.",Level.WARN)).hasSize(1);
            // @formatter:on
        }

        @Test
        @Order(3)
        void andWithProvidedTitleAndDetailThenAssayContainsProvidedTitleAndDetail()
        {
            var providedTitle = "Provided title";
            var providedDetail = "Provided detail";

            var failureAssay = FailureAssay.of()
                    .title(providedTitle)
                    .detail(providedDetail)
                    .build();

            // @formatter:off
            assertAll("Failure Assay created without failure type and with provided title and detail",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals(providedTitle, failureAssay.getTitle(), "uses provided title"),
                    () -> assertEquals(providedDetail, failureAssay.getDetail(), "has provided detail")
            );
            // @formatter:on
        }

        @Test
        @Order(4)
        void andProvidedTemplateWithNoArgsThenAssayContainsDetailFromExpandedTemplate()
        {
            var providedTitle = "Provided title";
            var providedTemplate = "Provided template is {}";

            var failureAssay = FailureAssay.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .build();

            // @formatter:off
            assertAll("Failure Assay created without failure type and with provided template",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals(providedTitle, failureAssay.getTitle(), "uses provided title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAssay.getDetail(), "has detail from unexpanded template")
            );
            // @formatter:on
        }

        @Test
        @Order(5)
        void andProvidedTemplateWithProvidedDetailThenAssayContainsProvidedDetail()
        {
            var providedTitle = "Provided title";
            var providedDetail = "Provided detail";
            var providedTemplate = "Provided template is {}";

            var failureAssay = FailureAssay.of()
                    .title(providedTitle)
                    .detail(providedDetail)
                    .template(providedTemplate)
                    .build();

            // @formatter:off
            assertAll("Failure Assay created without failure type and with provided template",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals(providedTitle, failureAssay.getTitle(), "uses provided title"),
                    () -> assertEquals(providedDetail, failureAssay.getDetail(), "has provided detail")
            );
            // @formatter:on
        }

        @Test
        @Order(6)
        void andProvidedTemplateWithArgsThenAssayContainsDetailFromExpandedTemplate()
        {
            var providedTitle = "Provided title";
            var providedTemplate = "Provided template is {}";

            var failureAssay = FailureAssay.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .args("PRESENT")
                    .build();

            // @formatter:off
            assertAll("Failure Assay created without failure type and with provided template",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals(providedTitle, failureAssay.getTitle(), "uses provided title"),
                    () -> assertTrue(failureAssay.getDetail().contains("PRESENT"), "has detail from expanded template")
            );
            // @formatter:on
        }
    }

    @Nested
    @Order(1)
    @DisplayName("hasCause() method")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class HasCauseMethod {
        @Test
        @Order(1)
        @DisplayName("should return false when cause is null")
        void shouldReturnFalseWhenCauseIsNull() {
            var failureAssay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .build();

            assertFalse(failureAssay.hasCause(), "should return false when cause is null");
        }

        @Test
        @Order(2)
        @DisplayName("should return true when cause is present")
        void shouldReturnTrueWhenCauseIsPresent() {
            var failureAssay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .cause(new RuntimeException("Test exception"))
                    .build();

            assertTrue(failureAssay.hasCause(), "should return true when cause is present");
        }
    }

    @Nested
    @Order(2)
    @DisplayName("Builder")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BuilderTests {
        @Test
        @Order(1)
        @DisplayName("should handle null template with non-null args")
        void shouldHandleNullTemplateWithNonNullArgs() {
            var failureAssay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .args("test")
                    .build();

            assertEquals("", failureAssay.getDetail(), "detail should be empty when template is null");
        }

        @Test
        @Order(2)
        @DisplayName("should handle null args with non-null template")
        void shouldHandleNullArgsWithNonNullTemplate() {
            var failureAssay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .template("test template")
                    .args((Object[]) null)
                    .build();

            assertEquals("test template", failureAssay.getDetail(), "detail should be raw template when args are null");
        }

        @Test
        @Order(3)
        @DisplayName("should handle null cause message")
        void shouldHandleNullCauseMessage() {
            var failureAssay = FailureAssay.of()
                    .cause(new Exception())
                    .build();

            assertAll("Assay with null cause message",
                    () -> assertEquals(FailureAssay.FAILURE_FROM_EXCEPTION, failureAssay.getTitle(), "has exception only title"),
                    () -> assertEquals("", failureAssay.getDetail(), "detail should be empty string when cause message is null")
            );
        }

        @Test
        @Order(4)
        @DisplayName("should handle fewer arguments than template placeholders")
        void shouldHandleFewerArgumentsThanPlaceholders() {
            var failureAssay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .template("Test {} {} {}")
                    .args("one")
                    .build();

            assertTrue(failureAssay.getDetail().contains("one"), "should contain the provided argument");
            assertTrue(failureAssay.getDetail().contains(""), "should contain empty strings for missing arguments");
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WhenCreatedWithGenericFailureType
    {
        @Test
        @Order(1)
        void thenAssayHasAllValuesProvidedByFailureType()
        {
            var failureAssay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .build();

            // @formatter:off
            assertAll("Assay",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals("generic-failure", failureAssay.getTitle(), "has a generic failure title"),
                    () -> assertEquals("", failureAssay.getDetail(), "has no details")
            );
            // @formatter:on
        }

        @Test
        @Order(2)
        void andProvidedTitleAndDetailThenAssayHasProvidedTitleAndDetail()
        {
            var providedTitle = "more-specific-failure";
            var providedDetail = "Explicitly provided details";

            var failureAssay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .title(providedTitle)
                    .detail(providedDetail)
                    .build();

            // @formatter:off
            assertAll("Assay",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals(providedTitle, failureAssay.getTitle(), "has a generic failure title"),
                    () -> assertEquals(providedDetail, failureAssay.getDetail(), " has provided details")
            );
            // @formatter:on
        }

        @Test
        @Order(3)
        void andProvidedTemplateAndNoArgsThenAssayDetailHasProvidedTemplate()
        {
            var providedTitle = "more-specific-failure";
            var providedTemplate = "Provided template is {}";

            var failureAssay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .title(providedTitle)
                    .template(providedTemplate)
                    .build();

            // @formatter:off
            assertAll("Assay",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals(providedTitle, failureAssay.getTitle(), "has a generic failure title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAssay.getDetail(), " has provided template")
            );
            // @formatter:on
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WhenCreatedWithDomainFailureType
    {
        @Test
        @Order(1)
        void thenAssayHasAllValuesProvidedByFailureType()
        {
            var failureAssay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .build();

            // @formatter:off
            assertAll("Assay",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals("clearer-failure", failureAssay.getTitle(), "has a generic failure title"),
                    () -> assertEquals("Looks like it had something to do with NotSupplied", failureAssay.getDetail(), "has template with NotSupplied for missing argument")
            );
            // @formatter:on
        }

        @Test
        @Order(2)
        void andProvidedTemplateAndArgsThenAssayDetailHasExpandedFailureTypeTemplate()
        {
            var providedTitle = "more-specific-failure";

            var failureAssay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .title(providedTitle)
                    .args("NOW")
                    .build();

            // @formatter:off
            assertAll("Assay",
                    () -> assertFalse(failureAssay.hasCause(), "has no cause"),
                    () -> assertEquals(providedTitle, failureAssay.getTitle(), "has a generic failure title"),
                    () -> assertTrue(failureAssay.getDetail().contains("NOW"), " has expanded template")
            );
            // @formatter:on
        }
    }

    @Nested
    @DisplayName("Template Expansion")
    class TemplateExpansion
    {
        @Test
        @DisplayName("should handle mismatched argument count gracefully")
        void shouldHandleMismatchedArgumentCount()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .template("Test template with {} argument")
                    .args("one", "two")  // Providing more arguments than template expects
                    .build();

            assertThat(assay.getDetail())
                    .as("Should use first argument when too many provided")
                    .isEqualTo("Test template with one argument");
        }

        @Test
        @DisplayName("should handle missing arguments gracefully")
        void shouldHandleMissingArguments()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .template("Test template with {0} argument")
                    .build();  // No arguments provided

            assertThat(assay.getDetail())
                    .as("Should handle missing arguments gracefully")
                    .isEqualTo("Test template with {0} argument");
        }

        @Test
        @DisplayName("should handle null arguments gracefully")
        void shouldHandleNullArguments()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .template("Test template with {0} argument")
                    .args((Object[]) null)
                    .build();

            assertThat(assay.getDetail())
                    .as("Should handle null arguments gracefully")
                    .isEqualTo("Test template with {0} argument");
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BuilderCopyConstructor
    {
        @Test
        @Order(1)
        void shouldCopyAllFieldsFromExistingAssay()
        {
            var originalAssay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .title("Original Title")
                    .detail("Original Detail")
                    .cause(new RuntimeException("Original Cause"))
                    .build();

            var builder = new FailureAssay.Builder(originalAssay);
            var copiedAssay = builder.build();

            assertAll("Copied Assay",
                    () -> assertEquals(originalAssay.getType(), copiedAssay.getType(), "type should be copied"),
                    () -> assertEquals(originalAssay.getTitle(), copiedAssay.getTitle(), "title should be copied"),
                    () -> assertEquals(originalAssay.getDetail(), copiedAssay.getDetail(), "detail should be copied"),
                    () -> assertEquals(originalAssay.getCause(), copiedAssay.getCause(), "cause should be copied")
            );
        }
    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BuilderTypeMethod
    {
        @Test
        @Order(1)
        void shouldNotOverrideExistingTitleAndTemplate()
        {
            var originalTitle = "Original Title";
            var originalTemplate = "Original Template {}";
            var newType = FailureAssayTestFailureType.MORE_CLEAR;

            var assay = FailureAssay.of()
                    .title(originalTitle)
                    .template(originalTemplate)
                    .type(newType)
                    .build();

            assertAll("Assay with existing title and template",
                    () -> assertEquals(originalTitle, assay.getTitle(), "should keep original title"),
                    () -> assertEquals("Original Template NotSupplied", assay.getDetail(), "should expand template with NotSupplied")
            );
        }
    }

    @Nested
    @Order(6)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TemplateExpansionEdgeCases
    {
        @Test
        @Order(1)
        void shouldReplaceMissingArgumentsWithNotSupplied()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .template("Test template with {} and {} arguments")
                    .args("first")  // Only providing one argument when template expects two
                    .build();

            assertThat(assay.getDetail())
                    .as("Should pad missing arguments with NotSupplied")
                    .isEqualTo("Test template with first and NotSupplied arguments");
        }

        @Test
        @Order(2)
        void shouldPadMultipleMissingArgumentsWithNotSupplied()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.TEMPLATE_TEST)
                    .template("Template with {} and {} and {} arguments")
                    .args("first")  // Only providing one argument when template expects three
                    .build();

            assertThat(assay.getDetail())
                    .as("Should pad multiple missing arguments with NotSupplied")
                    .isEqualTo("Template with first and NotSupplied and NotSupplied arguments");
        }

        @Test
        @Order(3)
        void shouldHandleNoArgumentsWithNotSupplied()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .template("Test template with {} and {} arguments")
                    .build();  // No arguments provided

            assertThat(assay.getDetail())
                    .as("Should pad all arguments with NotSupplied when none provided")
                    .isEqualTo("Test template with NotSupplied and NotSupplied arguments");
        }

        @Test
        @Order(4)
        void shouldHandleNullArgumentsWithNotSupplied()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .template("Test template with {} and {} arguments")
                    .args((Object[]) null)  // Null arguments array
                    .build();

            assertThat(assay.getDetail())
                    .as("Should pad all arguments with NotSupplied when args is null")
                    .isEqualTo("Test template with NotSupplied and NotSupplied arguments");
        }

        @Test
        @Order(5)
        void shouldHandlePartialNullFields()
        {
            var assay = FailureAssay.of()
                    .title("Test Title")  // Only title is set
                    .build();

            assertAll("Assay with partial fields",
                    () -> assertEquals("Test Title", assay.getTitle(), "should keep provided title"),
                    () -> assertEquals("", assay.getDetail(), "should have empty detail")
            );
        }

        @Test
        @Order(6)
        void shouldHandleVariousNullFieldCombinations()
        {
            // Case 1: Only detail is not null
            var assay1 = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .detail("Some detail")
                    .build();
            assertAll("Assay with only detail",
                    () -> assertEquals(FailureAssay.GenericFailureType.GENERIC.getTitle(), assay1.getTitle(), "should use generic title"),
                    () -> assertEquals("Some detail", assay1.getDetail(), "should keep provided detail")
            );

            // Case 2: Only template is not null
            var assay2 = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .template("Template {}")
                    .build();
            assertAll("Assay with only template",
                    () -> assertEquals(FailureAssay.GenericFailureType.GENERIC.getTitle(), assay2.getTitle(), "should use generic title"),
                    () -> assertEquals("Template NotSupplied", assay2.getDetail(), "should keep template as detail")
            );

            // Case 3: Template and detail both not null
            var assay3 = FailureAssay.of()
                    .type(FailureAssay.GenericFailureType.GENERIC)
                    .template("Template {}")
                    .detail("Explicit detail")
                    .build();
            assertAll("Assay with template and detail",
                    () -> assertEquals(FailureAssay.GenericFailureType.GENERIC.getTitle(), assay3.getTitle(), "should use generic title"),
                    () -> assertEquals("Explicit detail", assay3.getDetail(), "should use explicit detail over template")
            );
        }

        @Test
        @Order(7)
        void shouldHandleArgumentPadding()
        {
            // Create a failure type that expects 3 arguments
            var assay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.TEMPLATE_TEST)
                    .args("first")  // Providing only one argument when three are expected
                    .build();

            assertThat(assay.getDetail())
                    .as("Should pad missing arguments with NotSupplied")
                    .isEqualTo("Template with first and NotSupplied and NotSupplied");
        }
    }

    @Nested
    @Order(7)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class NonNullConstraints
    {
        @Test
        @Order(1)
        void shouldDefaultToGenericTypeWhenTypeIsNull()
        {
            var assay = FailureAssay.of()
                    .type(null)
                    .build();

            assertAll("Assay with null type",
                    () -> assertEquals(FailureAssay.GenericFailureType.GENERIC, assay.getType(), "should use generic type"),
                    () -> assertEquals("generic-failure", assay.getTitle(), "should use generic title"),
                    () -> assertEquals("", assay.getDetail(), "should have empty detail")
            );
        }

        @Test
        @Order(2)
        void shouldDefaultToGenericTypeWhenInitialDataHasNullType()
        {
            var originalAssay = new FailureAssay(null, "Original Title", "Original Detail", null);
            var builder = new FailureAssay.Builder(originalAssay);
            var copiedAssay = builder.build();

            assertAll("Copied Assay with null type",
                    () -> assertEquals(FailureAssay.GenericFailureType.GENERIC, copiedAssay.getType(), "should use generic type"),
                    () -> assertEquals("Original Title", copiedAssay.getTitle(), "should keep original title"),
                    () -> assertEquals("Original Detail", copiedAssay.getDetail(), "should keep original detail")
            );
        }

        @Test
        @Order(3)
        void shouldNotAllowNullInitialData()
        {
            assertThrows(NullPointerException.class, () -> {
                new FailureAssay.Builder(null);
            }, "should not allow null initial data in builder constructor");
        }
    }

    @Nested
    @Order(8)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DefaultValues
    {
        @Test
        @Order(1)
        void shouldUseGenericTypeByDefault()
        {
            var assay = FailureAssay.of().build();
            assertEquals(FailureAssay.GenericFailureType.GENERIC, assay.getType(),
                    "should use GENERIC failure type by default");
        }

        @Test
        @Order(2)
        void shouldUseTypesTitleByDefault()
        {
            var assay = FailureAssay.of()
                    .type(FailureAssayTestFailureType.MORE_CLEAR)
                    .build();
            assertEquals(FailureAssayTestFailureType.MORE_CLEAR.getTitle(), assay.getTitle(),
                    "should use failure type's title by default");
        }
    }

    @Getter
    @AllArgsConstructor
    public enum FailureAssayTestFailureType implements FailureType
    {
        UNCLEAR("unclear-failure", "We are unclear on why this happened"),
        MORE_CLEAR("clearer-failure", "Looks like it had something to do with {}"),
        TEMPLATE_TEST("test", "Template with {} and {} and {}"),
        ;

        private final String title;
        private final String template;
    }
}
