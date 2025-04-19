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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Order(60)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
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
                    () -> assertEquals(providedTemplate, failureAssay.getDetail(), "has detail from unexpanded template")
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
                    () -> assertEquals(providedTemplate, failureAssay.getDetail(), " has provided template")
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
                    () -> assertEquals(FailureAssayTestFailureType.MORE_CLEAR.getTemplate(), failureAssay.getDetail(), "has tempplate for details")
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

    @Getter
    @AllArgsConstructor
    public enum FailureAssayTestFailureType implements FailureType
    {
        UNCLEAR("unclear-failure", "We are unclear on why this happened"),
        MORE_CLEAR("clearer-failure", "Looks like it had something to do with {}"),
        ;

        private final String title;
        private final String template;
    }
}
