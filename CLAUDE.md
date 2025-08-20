# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Endeavour is a Java library implementing the operational result pattern as an alternative to exception-based error handling. It provides Success, Failure, and PartialSuccess types wrapped in an `Outcome<T>` interface, along with exceptional functional interfaces for safe lambda operations.

## Build & Test Commands

```bash
# Build the project
mvn clean compile

# Run all tests
mvn test

# Run tests with coverage report
mvn clean test jacoco:report

# Run specific test class
mvn test -Dtest=OutcomeTest

# Run specific test method
mvn test -Dtest=OutcomeTest#testSuccessCreation

# View coverage report
# Generated at: target/site/jacoco/index.html
```

## Architecture

### Core Concepts

**Outcome Pattern**: Central abstraction for operations that can succeed or fail
- `Outcome<T>` - Sealed interface representing operation results
- `Success<T>` - Successful operation with value
- `Failure` - Failed operation with error details
- `PartialSuccess<T>` - Mixed result with both success value and failure information

**Exceptional Functional Interfaces**: Lambda-compatible interfaces that handle checked exceptions
- `ExceptionalSupplier<T>` - Supplier that can throw exceptions
- `ExceptionalFunction<T,R>` - Function that can throw exceptions
- `ExceptionalConsumer<T>` - Consumer that can throw exceptions
- `ExceptionalRunnable` - Runnable that can throw exceptions
- `ExceptionalCallable<T>` - Callable wrapper
- `ExceptionalPredicate<T>` - Predicate that can throw exceptions
- `ExceptionalBiConsumer<T,U>` - BiConsumer that can throw exceptions

### Key Classes

- **Outcomes**: Factory class for creating outcome instances
- **Functional**: Utility class for wrapping throwing operations
- **FailureType**: Enumeration of failure categories
- **FailureDescription**: Structured failure information

## Development Standards

### Error Handling Philosophy
- Reserve exceptions for truly exceptional circumstances
- Use `Outcome<T>` for expected failure scenarios (validation, business rules)
- Convert exceptions to failures at system boundaries
- Provide clear, actionable failure messages for users

### Code Style
- Use Lombok annotations: `@Data`, `@Builder`, `@Slf4j`
- Follow immutable-first design principles
- Validate method arguments early
- Keep methods focused - either "do something" or "ask something"
- Limit method parameters to 3 maximum

### Testing Standards
- Use BDD naming: `givenStateX_whenActionY_thenOutcomeZ`
- Structure tests with Given/When/Then comment sections
- Test positive cases, negative cases, and edge cases
- Use AssertJ for fluent assertions
- Achieve high test coverage (aim for >90%)

### Documentation
- Use Javadoc for public APIs
- Include code examples in documentation
- Document the "why" not just the "what"
- Use `{@code}` for inline code snippets in Javadoc

## Common Patterns

### Creating Outcomes
```java
// Success
Outcome<String> success = Outcomes.success("value");

// Failure
Outcome<String> failure = Outcomes.failure(FailureType.VALIDATION, "Invalid input");

// From throwing operation
Outcome<Integer> result = Functional.tryTo(() -> Integer.parseInt("123"));
```

### Transforming Outcomes
```java
// Map success values
outcome.mapSuccess(String::toUpperCase);

// Flat map for chaining operations
outcome.flatMapSuccess(value -> performNextOperation(value));

// Handle both success and failure
outcome.ifSuccessOrElse(
    value -> System.out.println("Got: " + value),
    failure -> System.err.println("Failed: " + failure.getDescription())
);
```

### Exception Handling
```java
// Convert throwing lambda to non-throwing
ExceptionalSupplier<String> supplier = () -> riskyOperation();
Outcome<String> result = Functional.tryTo(supplier);
```

## Dependencies

### Core Dependencies
- **Lombok 1.18.36**: Boilerplate reduction
- **Logback 1.5.12**: Logging implementation
- **Guava 33.3.1**: Utility collections

### Test Dependencies
- **JUnit 5.11.3**: Testing framework
- **AssertJ 3.12.0**: Fluent assertions

## Key Design Principles

1. **Explicit Error Handling**: Make failure cases visible in method signatures
2. **Performance**: Zero overhead for success paths, pay-for-play failure handling
3. **Composability**: Chain operations safely without exception propagation
4. **Type Safety**: Leverage generics for compile-time safety
5. **Immutability**: Outcome instances are immutable value objects

## Integration Notes

This library is designed to be a foundational dependency for other projects in the tools-and-exemplars repository. When used as a dependency:

- Import as Maven dependency with version 1.0-SNAPSHOT
- Use `Outcome<T>` as return types for operations that can fail
- Convert exceptions to outcomes at service boundaries
- Leverage exceptional functional interfaces for lambda operations