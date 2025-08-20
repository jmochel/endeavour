package org.saltations.endeavour.fixture;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

/**
 * JUnit 5 Test Display Name generator
 * <p>
 * Does several things to transform test method names to test names.
 * <ol>
 *  <li>Separates camel case names with spaces</li>
 *  <li>Replace BDD key words with all caps versions. i.e. 'GIVEN','WHEN','THEN'. 'given' Is only uppercase when
 *  it is the first word of the test method name </li>
 * </ol>
 * <p>
 * Does several things to transform nested class names to test scenario names.
 * <ol>
 *  <li>Removes 'Test' from the end of class </li>
 *  <li>Separates camel case names with spaces</li>
 *  <li>Replace BDD key words with all caps versions. i.e. 'AND','GIVEN','WHEN','THEN'. 'given' and 'and' are only uppercased
 *  when they are the first word in the name of the class.</li>
 * </ol>
 */
public class ReplaceBDDCamelCase extends DisplayNameGenerator.Standard
{
    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {

        return splitCamelCase(testClass.getSimpleName().replaceAll("[Tt]est$",""));
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {

        var nestedClassDisplayName = splitCamelCase(nestedClass.getSimpleName().replaceAll("[Tt]est$",""))
                .toLowerCase()
                .replaceAll("^and", "AND ")
                .replaceAll("^given", "GIVEN ")
                .replaceAll(" when ", "WHEN ")
                .replaceAll(" then ", " THEN ")
                .replaceAll("  ", " ")
                .trim();

       return nestedClassDisplayName;          
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        var methodDisplayName =  splitCamelCase(testMethod.getName())
                .toLowerCase()
                .replaceAll("^given", "GIVEN ")
                .replaceAll("when ", "WHEN ")
                .replaceAll(" then ", " THEN ")
                .replaceAll("  ", " ")
                .trim();

        return methodDisplayName;
    }

    private String splitCamelCase(String incoming)
    {
        return incoming.replaceAll("([A-Z][a-z]+)", " $1")
                       .replaceAll("([A-Z][A-Z]+)", " $1")
                       .replaceAll("([A-Z][a-z]+)", "$1 ")
                       .trim();
    }
}
