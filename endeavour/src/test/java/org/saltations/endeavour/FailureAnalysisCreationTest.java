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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.saltations.endeavour.fixture.MemoryAppender;
import org.saltations.endeavour.fixture.ReplaceBDDCamelCase;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.DisplayNameGeneration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;


/**
 * This class tests the FailureAnalysis class.  
 * <p>
 * It is intended to be a comprehensive test of the FailureAnalysis class.
 * We are not using pairwise testing or any other stochastic methodology because of the linked relationships between the various attributes that can be passed in. 
 */

@Slf4j
@Order(60)
@DisplayNameGeneration(ReplaceBDDCamelCase.class)
class FailureAnalysisCreationTest
{
    private final MemoryAppender memoryAppender = new MemoryAppender();

    @BeforeAll
    public void setupLogCapture()
    {
        var logger = (Logger) LoggerFactory.getLogger(FailureDescription.class);
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
    @DisplayNameGeneration(ReplaceBDDCamelCase.class)
    class WhenCreatedWithNoFailureTypeAndNoCause
    {
        @Test
        @Order(1)
        void andFailureTypeIsExplicitlyNullThenAnalysisHasDefaultGenericTypeTitleAndEmptyDetails()
        {
            // Given
            var analysis = new FailureDescription(null, null, null, null);

            // Then
            assertAll(
                () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, analysis.getType()),
                () -> assertNull(analysis.getTitle()),
                () -> assertNull(analysis.getDetail()),
                () -> assertFalse(analysis.hasCause())
            );
        }

        @Test
        @Order(2)
        void andNothingElseThenAnalysisHasDefaultGenericTypeTitleAndEmptyDetails()
        {
            // Given
            var analysis = new FailureDescription(null, null, null, null);

            // Then
            assertAll(
                () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, analysis.getType()),
                () -> assertNull(analysis.getTitle()),
                () -> assertNull(analysis.getDetail()),
                () -> assertFalse(analysis.hasCause())
            );
        }

        @Test
        @Order(3)
        void andFailureTypeIsExplicitlyGenericThenAnalysisHasGenericTypeTitleAndEmptyDetails()
        {
            // Given
            var analysis = new FailureDescription(FailureDescription.GenericFailureType.GENERIC, null, null, null);

            // Then
            assertAll(
                () -> assertEquals(FailureDescription.GenericFailureType.GENERIC, analysis.getType()),
                () -> assertNull(analysis.getTitle()),
                () -> assertNull(analysis.getDetail()),
                () -> assertFalse(analysis.hasCause())
            );
        }

        @Nested
        @Order(4)
        @DisplayNameGeneration(ReplaceBDDCamelCase.class)
        class AndIsOverriddenWith
        {
            @Test
            @Order(1)
            void titleThenAnalysisHasProvidedTitleAndEmptyDetails()
            {
                var providedTitle = "Provided title";

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals("", failureAnalysis.getDetail(), "has empty detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(2)
            void templateThenAnalysisHasDefaultGenericFailureTypeTitleAndDetailFromProvidedTemplate()
            {
                var providedTemplate = "Provided template is {}";

                var failureAnalysis = FailureDescription.of()
                    .template(providedTemplate)
                    .build();   
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }


            @Test
            @Order(3)
            void detailThenAnalysisHasDefaultGenericFailureTypeTitleAndProvidedDetail()
            {
                var providedDetail = "Provided detail";

                var failureAnalysis = FailureDescription.of()
                    .detail(providedDetail)
                    .build();   
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(4)
            void titleAndTemplateThenAnalysisHasProvidedTitleAndDetailFromTemplate()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(5)
            void titleAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedDetail = "Provided detail";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .detail(providedDetail)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(6)
            void titleAndTemplateAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       
                var providedDetail = "Provided detail";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .detail(providedDetail)
                    .args("THING")
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }

        }
    }


    @Nested
    @Order(2)
    @DisplayNameGeneration(ReplaceBDDCamelCase.class)
    class WhenCreatedWithCustomFailureTypeAndNoCause
    {
        @Test
        @Order(1)
        void andNothingElseThenHasFailureTypeTitleAndDetails()
        {
            var failureAnalysis = FailureDescription.of()
                    .type(CustomFailureType.SINGULAR_ERROR)
                    .args("THING")
                    .build();

            // @formatter:off
            assertAll("FailureAnalysis",
                    () -> assertEquals(CustomFailureType.SINGULAR_ERROR.getTitle(), failureAnalysis.getTitle(), "has failure type title"),
                    () -> assertEquals("Looks like it had something to do with THING", failureAnalysis.getDetail(), "has detail from failure type template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
            );
            // @formatter:on
        }

        @Nested
        @Order(4)
        @DisplayNameGeneration(ReplaceBDDCamelCase.class)
        class AndIsOverriddenWith
        {
            @Test
            @Order(1)
            void titleThenAnalysisHasProvidedTitleAndEmptyDetails()
            {
                var providedTitle = "Provided title";

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals("", failureAnalysis.getDetail(), "has empty detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(2)
            void templateThenAnalysisHasDefaultGenericFailureTypeTitleAndDetailFromProvidedTemplate()
            {
                var providedTemplate = "Provided template is {}";

                var failureAnalysis = FailureDescription.of()
                    .template(providedTemplate)
                    .build();   
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")   
                );
                // @formatter:on
            }


            @Test
            @Order(3)
            void detailThenAnalysisHasDefaultGenericFailureTypeTitleAndProvidedDetail()
            {
                var providedDetail = "Provided detail";

                var failureAnalysis = FailureDescription.of()
                    .detail(providedDetail)
                    .build();   
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(4)
            void titleAndTemplateThenAnalysisHasProvidedTitleAndDetailFromTemplate()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")   
                );
                // @formatter:on
            }

            @Test
            @Order(5)
            void titleAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedDetail = "Provided detail";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .detail(providedDetail)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")   
                );
                // @formatter:on
            }

            @Test
            @Order(6)
            void titleAndTemplateAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       
                var providedDetail = "Provided detail";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .detail(providedDetail)
                    .args("THING")
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause"),
                    () -> assertFalse(failureAnalysis.hasCause(), "has no cause")
                );
                // @formatter:on
            }
        }
    }


    @Nested
    @Order(2)
    @DisplayNameGeneration(ReplaceBDDCamelCase.class)
    class WhenCreatedWithCauseAndNoFailureType
    {

        @Test
        @Order(2)
        void andNothingElseThenHasGenericExceptionFailureTypeTitleAndDetailFromCause()
        {
                var providedErrorMessage = "Dang!";

                var failureAnalysis = FailureDescription.of()
                        .type(null)
                        .cause(new RuntimeException(providedErrorMessage))
                        .build();

            // @formatter:off
            assertAll("FailureAnalysis",
                () -> assertEquals(FailureDescription.GenericFailureType.GENERIC_EXCEPTION.getTitle(), failureAnalysis.getTitle(), "has type title"),
                () -> assertEquals(providedErrorMessage, failureAnalysis.getDetail(), "has cause detail"),
                () -> assertNotNull(failureAnalysis.getCause(), "has cause"),
                () -> assertTrue(failureAnalysis.hasCause(), "has cause")
            );
            // @formatter:on
        }

        @Nested
        @Order(4)
        @DisplayNameGeneration(ReplaceBDDCamelCase.class)
        class AndIsOverriddenWith
        {
            @Test
            @Order(1)
            void titleThenAnalysisHasProvidedTitleAndDetailFromCause()
            {
                var providedTitle = "Provided title";

                var providedErrorMessage = "Dang!";

                var failureAnalysis = FailureDescription.of()
                        .type(null)
                        .cause(new RuntimeException(providedErrorMessage))
                        .title(providedTitle)
                        .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedErrorMessage, failureAnalysis.getDetail(), "has cause detail"),
                    () -> assertNotNull(failureAnalysis.getCause(), "has cause"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause")
                );
                // @formatter:on
            }

            @Test
            @Order(2)
            void templateThenAnalysisHasDefaultGenericExceptionFailureTypeTitleAndDetailFromProvidedTemplate()
            {
                var providedTemplate = "Provided template is {}";
                var providedErrorMessage = "Dang!";

                var failureAnalysis = FailureDescription.of()
                        .type(null)
                        .cause(new RuntimeException(providedErrorMessage))
                        .template(providedTemplate)
                        .build();
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC_EXCEPTION.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNotNull(failureAnalysis.getCause(), "has cause"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause")
                );
                // @formatter:on
            }


            @Test
            @Order(3)
            void detailThenAnalysisHasDefaultGenericFailureTypeTitleAndProvidedDetail()
            {
                var providedDetail = "Provided detail";
                var providedErrorMessage = "Dang!";

                var failureAnalysis = FailureDescription.of()
                        .type(null)
                        .cause(new RuntimeException(providedErrorMessage))
                        .detail(providedDetail)
                        .build();
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC_EXCEPTION.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNotNull(failureAnalysis.getCause(), "has cause"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause")
                );
                // @formatter:on
            }

            @Test
            @Order(4)
            void titleAndTemplateThenAnalysisHasProvidedTitleAndDetailFromTemplate()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       
                var providedErrorMessage = "Dang!";

                var failureAnalysis = FailureDescription.of()
                        .type(null)
                        .cause(new RuntimeException(providedErrorMessage))
                        .title(providedTitle)
                        .template(providedTemplate)
                        .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNotNull(failureAnalysis.getCause(), "has cause"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause")
                );
                // @formatter:on
            }

            @Test
            @Order(5)
            void titleAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedDetail = "Provided detail";       
                var providedErrorMessage = "Dang!";

                var failureAnalysis = FailureDescription.of()
                        .type(null)
                        .cause(new RuntimeException(providedErrorMessage))
                        .title(providedTitle)
                        .detail(providedDetail)
                        .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNotNull(failureAnalysis.getCause(), "has cause"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause")
                );
                // @formatter:on
            }

            @Test
            @Order(6)
            void titleAndTemplateAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       
                var providedDetail = "Provided detail";       
                var providedErrorMessage = "Dang!";

                var failureAnalysis = FailureDescription.of()
                        .type(null)
                        .cause(new RuntimeException(providedErrorMessage))
                        .title(providedTitle)
                        .template(providedTemplate)
                        .detail(providedDetail)
                        .args("THING")
                        .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNotNull(failureAnalysis.getCause(), "has cause"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause")
                );
                // @formatter:on
            }
        }

    }


   
    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WhenCreatedWithAFailureTypeAndCause {

        @Test
        @Order(1)
        void andCauseHasNotMessageThenAnalysisHasProvidedFailureTypeTitleAndEmptyDetail()
        {
            var failureAnalysis = FailureDescription.of()
                    .type(CustomFailureType.SINGULAR_ERROR)
                    .cause(new RuntimeException())
                    .build();

            // @formatter:off
            assertAll("FailureAnalysis",
                    () -> assertEquals(CustomFailureType.SINGULAR_ERROR.getTitle(), failureAnalysis.getTitle(), "has type title"),
                    () -> assertEquals("", failureAnalysis.getDetail(), "has empty detail"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause"),
                    () -> assertNotNull(failureAnalysis.getCause(), "cause is not null")
            );
            // @formatter:on
        }

        @Test
        @Order(2)
        void andNothingElseThenAnalysisHasProvidedFailureTypeTitleAndDetailsFromCause()
        {
            String providedExceptionMessage = "Dang!";

            var failureAnalysis = FailureDescription.of()
                    .type(CustomFailureType.SINGULAR_ERROR)
                    .cause(new RuntimeException(providedExceptionMessage))
                    .build();

            // @formatter:off
            assertAll("FailureAnalysis",
                    () -> assertEquals(CustomFailureType.SINGULAR_ERROR.getTitle(), failureAnalysis.getTitle(), "has type title"),
                    () -> assertEquals(providedExceptionMessage, failureAnalysis.getDetail(), "has detail from cause"),
                    () -> assertTrue(failureAnalysis.hasCause(), "has cause"),
                    () -> assertNotNull(failureAnalysis.getCause(), "cause is not null")
            );
            // @formatter:on
        }

        @Nested
        @Order(4)
        @DisplayNameGeneration(ReplaceBDDCamelCase.class)
        class AndIsOverriddenWith
        {
            @Test
            @Order(1)
            void titleThenAnalysisHasProvidedTitleAndEmptyDetails()
            {
                var providedTitle = "Provided title";

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals("", failureAnalysis.getDetail(), "has empty detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(2)
            void templateThenAnalysisHasDefaultGenericFailureTypeTitleAndDetailFromProvidedTemplate()
            {
                var providedTemplate = "Provided template is {}";

                var failureAnalysis = FailureDescription.of()
                    .template(providedTemplate)
                    .build();   
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause")
                );
                // @formatter:on
            }


            @Test
            @Order(3)
            void detailThenAnalysisHasDefaultGenericFailureTypeTitleAndProvidedDetail()
            {
                var providedDetail = "Provided detail";

                var failureAnalysis = FailureDescription.of()
                    .detail(providedDetail)
                    .build();   
                    
                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(FailureDescription.GenericFailureType.GENERIC.getTitle(), failureAnalysis.getTitle(), "has default title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(4)
            void titleAndTemplateThenAnalysisHasProvidedTitleAndDetailFromTemplate()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals("Provided template is NotSupplied", failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(5)
            void titleAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedDetail = "Provided detail";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .detail(providedDetail)
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has provided detail"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause")
                );
                // @formatter:on
            }

            @Test
            @Order(6)
            void titleAndTemplateAndDetailThenAnalysisHasProvidedTitleAndProvidedDetail()
            {
                var providedTitle = "Provided title";
                var providedTemplate = "Provided template is {}";       
                var providedDetail = "Provided detail";       

                var failureAnalysis = FailureDescription.of()
                    .title(providedTitle)
                    .template(providedTemplate)
                    .detail(providedDetail)
                    .args("THING")
                    .build();

                // @formatter:off
                assertAll("FailureAnalysis",
                    () -> assertEquals(providedTitle, failureAnalysis.getTitle(), "has provided title"),
                    () -> assertEquals(providedDetail, failureAnalysis.getDetail(), "has detail from provided template"),
                    () -> assertNull(failureAnalysis.getCause(), "has no cause")
                );
                // @formatter:on
            }



        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BuilderCopyConstructor
    {
        @Test
        @Order(1)
        void shouldCopyAllFieldsFromExistingAnalysis()
        {
            var originalAnalysis = FailureDescription.of()
                    .type(FailureAnalysisTestFailureType.MORE_CLEAR)
                    .title("Original Title")
                    .detail("Original Detail")
                    .cause(new RuntimeException("Original Cause"))
                    .build();

            var builder = new FailureDescription.Builder(originalAnalysis);
            var copiedAnalysis = builder.build();

            assertAll("Copied Analysis",
                    () -> assertEquals(originalAnalysis.getType(), copiedAnalysis.getType(), "type should be copied"),
                    () -> assertEquals(originalAnalysis.getTitle(), copiedAnalysis.getTitle(), "title should be copied"),
                    () -> assertEquals(originalAnalysis.getDetail(), copiedAnalysis.getDetail(), "detail should be copied"),
                    () -> assertEquals(originalAnalysis.getCause(), copiedAnalysis.getCause(), "cause should be copied")
            );
        }
    }

    @Order(5)
    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("templateExpansionTestData")
    void templateExpansion(String scenarioName,String template, Object[] args, String expectedExpansion)
    {
        var analysis = FailureDescription.of()
                .template(template)
                .args(args)
                .build();

        assertEquals(expectedExpansion, analysis.getDetail(), "expanded template");
    }   

    static Stream<Arguments> templateExpansionTestData() {      
        return Stream.of(
            Arguments.of("Null template", null, new Object[0], ""),
            Arguments.of("Empty template", "", new Object[0], ""),
            
            Arguments.of("Template with no placeholders, 0 args", "Look Ma, No placeholders", new Object[0], "Look Ma, No placeholders"),
            Arguments.of("Template with no placeholders, 1 args", "Look Ma, No placeholders", new Object[]{"one"}, "Look Ma, No placeholders"),

            Arguments.of("Template with 1 placeholder, 0 argss", "Template with {}", new Object[0], "Template with NotSupplied"),
            Arguments.of("Template with 1 placeholder, 1 arg", "Template with {}", new Object[]{"one"}, "Template with one"),
            Arguments.of("Template with 1 placeholder, 2 args", "Template with {}", new Object[]{"one", "two"}, "Template with one"),

            Arguments.of("Template with 2 placeholders, 0 args", "Template with {} and {}", new Object[0], "Template with NotSupplied and NotSupplied"),
            Arguments.of("Template with 2 placeholders, 1 arg", "Template with {} and {}", new Object[]{"one"}, "Template with one and NotSupplied"),
            Arguments.of("Template with 2 placeholders, 2 args", "Template with {} and {}", new Object[]{"one", "two"}, "Template with one and two")
        );
    }
   

    @Getter
    @AllArgsConstructor
    enum CustomFailureType implements FailureType
    {
        SINGULAR_ERROR("Something went wrong with 1 thing", "Looks like it had something to do with {}"),
        COMPLEX_ERROR("Something went wrong with 3 things", "Looks like it had something to do with {} and {} and {}"),
        SIMPLE_ERROR("Something simple went wrong", "This is a simple message without placeholders"),
        ;

        private final String title;
        private final String template;
    }


    @Getter
    @AllArgsConstructor
    public enum FailureAnalysisTestFailureType implements FailureType
    {
        UNCLEAR("unclear-failure", "We are unclear on why this happened"),
        MORE_CLEAR("clearer-failure", "Looks like it had something to do with {}"),
        TEMPLATE_TEST("test", "Template with {} and {} and {}"),
        ;

        private final String title;
        private final String template;
    }

    @Test
    @Order(100)
    void whenBuildingWithNullTemplateThenReturnsEmptyDetail()
    {
        var analysis = FailureDescription.of()
                .template(null)
                .build();

        assertEquals("", analysis.getDetail(), "Detail should be empty when template is null");
    }

    @Test
    @Order(101)
    void whenBuildingWithNullArgsThenUsesEmptyArray()
    {
        var analysis = FailureDescription.of()
                .template("Template with {} placeholder")
                .args((Object[]) null)
                .build();

        assertEquals("Template with NotSupplied placeholder", analysis.getDetail(), 
                "Should handle null args by using empty array");
    }

    @Test
    @Order(102)
    void whenBuildingWithCauseButNoOtherInfoThenUsesCauseMessage()
    {
        Exception testException = new Exception("Test exception message");
        var analysis = FailureDescription.of()
                .cause(testException)
                .build();

        assertEquals("Test exception message", analysis.getDetail(), 
                "Should use cause message when no other detail provided");
    }

    @Test
    @Order(103)
    void whenBuildingWithCauseWithNullMessageThenUsesEmptyDetail()
    {
        Exception testException = new Exception();
        testException.initCause(null); // Ensure no message
        var analysis = FailureDescription.of()
                .cause(testException)
                .build();

        assertEquals("", analysis.getDetail(), 
                "Should use empty detail when cause has null message");
    }
}
