# Endeavour — Operational Result Pattern for Java

Endeavour is a Java library that implements the operational result pattern, providing a robust and type-safe way to handle operations that can succeed or fail without relying on exceptions for control flow.

![Tactical Ferret](./tactical-ferret-logo.png)
[^1] Tactical Ferret images created using Craiyon

## Quick Start (5 minutes)

```bash
# Clone + build
git clone https://github.com/jmochel/endeavour.git
cd endeavour
mvn clean package -DskipTests

# Sanity check (unit tests fast path)
mvn test -DskipITs
```

## Install

### Maven
```xml
<dependency>
  <groupId>org.saltations.endeavour</groupId>
  <artifactId>endeavour</artifactId>
  <version>0.4.0-SNAPSHOT</version>
</dependency>
```

### Gradle (Kotlin DSL)
```kotlin
dependencies {
  implementation("org.saltations.endeavour:endeavour:0.4.0-SNAPSHOT")
}
```

## Usage

```java
// Create a successful result
Result<String> success = Try.success("Hello World");

// Create a failure
Result<String> failure = Try.failure("Operation failed");

// Transform values safely
Result<Integer> length = success.map(String::length);

// Handle both success and failure cases
String message = success.reduce(
    value -> "Success: " + value,
    error -> "Failed: " + error.getDetail()
);
```

## Core Concepts & Modules

- **JDK:** Java 21+
- **Modules:** 
  - `endeavour` - Core library implementing the operational result pattern
  - `endeavour-cli` - CLI application for testing and demonstration
- **Key Concepts:**
  - **Result Monad**: Sealed interface `Result<T>` with `Success<T>` and `Failure<T>` implementations
  - **Exception Handling**: Wraps operations that may throw exceptions into Result types
  - **Monadic Operations**: `map`, `flatMap`, `reduce`, `orElse` for composing operations
  - **Type Safety**: Explicit error handling without exceptions for control flow

### Implementation Overview

The operational result is represented in _Endeavour_ by the sealed interface `Result<T>`. 
This interface has two main implementations: `Success<T>` and `Failure<T>`.
Each implementation acts as a monad: it wraps a value that may or may not be present and provides methods to perform computations on that value in a safe manner.

#### Core Components

**Result Monad Interface:**
* [Result.java](endeavour/src/main/java/org/saltations/endeavour/Result.java) - Main sealed interface defining monadic operations
* [Success.java](endeavour/src/main/java/org/saltations/endeavour/Success.java) - Sealed interface for successful outcomes
* [QuantSuccess.java](endeavour/src/main/java/org/saltations/endeavour/QuantSuccess.java) - Success with a payload value
* [QualSuccess.java](endeavour/src/main/java/org/saltations/endeavour/QualSuccess.java) - Success without a payload
* [Failure.java](endeavour/src/main/java/org/saltations/endeavour/Failure.java) - Failure implementation with detailed error information

**Factory and Utilities:**
* [Try.java](endeavour/src/main/java/org/saltations/endeavour/Try.java) - Factory methods for creating Results from operations that may throw exceptions
* [FailureDescription.java](endeavour/src/main/java/org/saltations/endeavour/FailureDescription.java) - Rich error information with templating support

**Checked Exception Handling:**
* [CheckedConsumer.java](endeavour/src/main/java/org/saltations/endeavour/CheckedConsumer.java) - Consumer that can throw checked exceptions
* [CheckedFunction.java](endeavour/src/main/java/org/saltations/endeavour/CheckedFunction.java) - Function that can throw checked exceptions  
* [CheckedSupplier.java](endeavour/src/main/java/org/saltations/endeavour/CheckedSupplier.java) - Supplier that can throw checked exceptions

**Legacy Functional Interfaces (for compatibility):**
* [ExceptionalBiConsumer.java](endeavour/src/main/java/org/saltations/endeavour/ExceptionalBiConsumer.java) - BiConsumer that can throw exceptions
* [ExceptionalCallable.java](endeavour/src/main/java/org/saltations/endeavour/ExceptionalCallable.java) - Callable wrapper
* [ExceptionalConsumer.java](endeavour/src/main/java/org/saltations/endeavour/ExceptionalConsumer.java) - Consumer that can throw exceptions *(unused)*
* [ExceptionalFunction.java](endeavour/src/main/java/org/saltations/endeavour/ExceptionalFunction.java) - Function that can throw exceptions
* [ExceptionalPredicate.java](endeavour/src/main/java/org/saltations/endeavour/ExceptionalPredicate.java) - Predicate that can throw exceptions *(unused)*
* [ExceptionalRunnable.java](endeavour/src/main/java/org/saltations/endeavour/ExceptionalRunnable.java) - Runnable that can throw exceptions
* [ExceptionalSupplier.java](endeavour/src/main/java/org/saltations/endeavour/ExceptionalSupplier.java) - Supplier that can throw exceptions

**Utility Classes:**
* [Functional.java](endeavour/src/main/java/org/saltations/endeavour/Functional.java) - Utility methods for casting Exceptional* interfaces to standard Java functional interfaces

### API Design Philosophy

The library provides two approaches for exception handling:

1. **Checked Exception Interfaces** (`Checked*`) - Used by the core Result monad operations, allowing explicit exception handling
2. **Exceptional Interfaces** (`Exceptional*`) - Legacy interfaces that convert checked exceptions to unchecked exceptions

The core Result monad operations (`map`, `flatMap`, `act`, `reduce`, `ifSuccess`, `ifFailure`) use `Checked*` interfaces to provide explicit exception handling, while maintaining backward compatibility through the `Functional` utility class.

## Usage Examples

### Basic Result Creation and Handling

```java
// Create results using Try factory methods
Result<String> success = Try.success("Hello World");
Result<String> failure = Try.failure("Operation failed");

// Check result type
if (success.isSuccess()) {
    String value = success.get(); // Safe to call on success
}

// Transform values safely
Result<Integer> length = success.map(String::length);

// Chain operations with flatMap
Result<String> processed = success.flatMap(s -> 
    s.length() > 5 ? Try.success(s.toUpperCase()) : Try.failure("Too short")
);

// Handle both cases with reduce
String message = success.reduce(
    value -> "Success: " + value,
    error -> "Failed: " + error.getDetail()
);
```

### Exception Handling

```java
// Wrap operations that may throw exceptions
Result<Integer> parseResult = Try.ofCallable(() -> 
    Integer.parseInt("123")
);

// Handle checked exceptions explicitly
Result<String> fileContent = Try.ofCallable(() -> 
    Files.readString(Paths.get("config.txt"))
).map(content -> content.trim());

// Perform side effects safely
parseResult.act(value -> System.out.println("Parsed: " + value));
```

### Error Recovery

```java
// Provide fallback values
Result<String> config = Try.ofCallable(() -> 
    Files.readString(Paths.get("config.txt"))
).orElse(Try.success("default-config"));

// Recover from failures
Result<String> recovered = config.orElseGet(() -> 
    Try.ofCallable(() -> Files.readString(Paths.get("backup-config.txt")))
);
```

## Development Workflow

```bash
# Build
mvn clean compile

# Test (unit)
mvn test

# Format / Lint
# (No formatter configured - consider adding Spotless or similar)

# Generate docs (Javadoc)
mvn javadoc:javadoc

# Generate test coverage report
mvn test jacoco:report

# Package the library
mvn package

# Install to local repository
mvn install
```

## Compatibility & Versioning

- Supported Java: JDK 21+
- Semantic versioning unless noted; see **Changelog**.
- **Current Status:**
  - ✅ **Core Result Monad**: Fully implemented with comprehensive test coverage  
  - ✅ **Checked Exception Handling**: Enhanced with `CheckedConsumer` and `CheckedFunction` interfaces  
  - ✅ **Test Coverage**: 100% line coverage achieved across all core components  
  - ✅ **API Consistency**: Unified exception handling across all monadic operations  
  - ⚠️ **API Stability**: Still in development - breaking changes may occur (version 0.4.0-SNAPSHOT)

## Troubleshooting

- **JDK version mismatch**: `maven-compiler-plugin` target is 21; install JDK 21+.
- **Proxy/cert issues downloading dependencies**: Set `MAVEN_OPTS` with proxy settings or SSL truststore configuration.
- **Test failures**: Ensure all dependencies are properly downloaded. Run `mvn clean install -U` to force update dependencies.

## Links

- API Docs: [Javadoc](https://jmochel.github.io/endeavour/) (if published) or generate locally with `mvn javadoc:javadoc`
- Changelog: [GitHub Releases](https://github.com/jmochel/endeavour/releases)
- Issues: [GitHub Issues](https://github.com/jmochel/endeavour/issues)
- Security: [GitHub Security Policy](https://github.com/jmochel/endeavour/security/policy)
- Contributing: See Contributing section below
- License: [License information to be added]

---

## Additional Documentation

<!-- MARKED FOR REVIEW: The following sections contain valuable content but don't fit the standard README structure. 
     Consider moving to separate documentation files or a docs/ directory. -->

### Rationale for the Operational Result Pattern

The operational result pattern is a design pattern that is used to handle the results of operations that can either succeed or fail.
It is particularly useful in functional programming, where exceptions are often avoided in favor of more explicit error handling.
The operational result pattern is similar to the `Either` type in some functional programming languages, or the `Try` Monad in others.

Using exceptions expensive for a couple of reasons:
* They impact performance because raising an exception requires additional resources to activate the exception mechanism and process the exception
* They require searching and manipulating the stack to find the appropriate catch block
* The interruption of the control flow requires the release and manipulation of resources.
* Checked and unchecked exceptions can end up being used for control flow, which mostly makes code harder to follow.

The operational result pattern is useful for a number of reasons:
* Exceptions are expensive to throw and catch, and often have a negative impact on performance. Using the operational result pattern, you can avoid the overhead. 
* Exceptions can be difficult to reason about, especially when they are thrown deep in the call stack. By using the operational result pattern, you can make error handling more explicit and easier to understand
* It is easy to fall into the pitfall of using exceptions for control flow, which can make code harder to follow. By using the operational result pattern, you can separate error handling from the normal flow of the program

In using the operational result pattern, we reserve exceptions for the truly exceptional circumstances that 
cannot be handled by the normal flow of the program. for normal failures, such as validation, failures, etc., where we would expect things to fail, we return a result. 
And even in the cases of exceptional failure, we convert these into a result that preserves what is captured by the exception and allows it to be pushed upward.

> We believe that exceptions should rarely be used as part of a program's normal flow:
> exceptions should be reserved for unexpected events. Assume that an uncaught exception 
> will terminate your program and ask yourself, 
> "Will this code still run if I remove all the exception handlers?" 
> If the answer is "no", then maybe exceptions are being used in nonexceptional circumstances.
> -- Dave Thomas and Andy Hunt

### Library Intentions 

Intention is to identify success or failure of an operation is a compact, and hopefully typesafe,
representation that allows 

* Create exceptions with some strong domain level typing when useful
    *  Wrap and invoke functions that might blow up and catch their errors
* Easy access to the values returned including complex results
* Creating successes or failures from methods that throw exceptions
* Handle both success and failure whenever we want
* Transform a failure in the called operation to a success in our current operations
* Be able to map from one success type to another across data boundaries, ie from domain to presentation
    * Transform a success in the called operation to a failure in our current operations
* Be able to map errors from one type to another, perhaps converting a Throwable to something more useful to the presentation layer
* Play well with code that uses java Exceptions as part of their flow control.
* Ideally, we'd be able to chain or combine a bunch of these operations together, and only handle the error state at the end of the computation

A failure in any result that is not a success in the context of the method.

. If your not sure if something should be an exception or a failure you could ask yourself "Would the user understand what to do with this failure message?"
Typically a user doesn't know what to do with exception messages, they are intended for a technical person to do a more in depth troubleshooting of
the problem. Business failure messages however, could be interpreted by a person and they should be able to act on them.

### Success, Partial Success and Failure

Most outcomes in apis can be broken up into Successes, Partial Successes and Failures

* Success - Operation worked; We got a normal expected result
* Partial Successes - Operation worked for some portion of the values to be worked on
* Failure -  Operation failed; We did not get what we wanted
  * Failure can be broken down into
    * Failure with a cause
    * Failure with a category/title and detail

### We want a model that is

* Usable.
    * Must be easy for developers to do the "right" thing in the face of error
    * Should not impose excessive ceremony in order to write idiomatic code
    * Cognitively familiar to our target audience.
* Performant.
    * The common case needs to be extremely fast. That means as close to zero overhead as possible for success paths.
    * Any added costs for failure paths must be entirely "pay-for-play."
* Diagnosable.
    * Debugging failures, either interactively or after-the-fact, needs to be productive and easy.
* Composable.
    * At the core, the error model is a programming language feature, sitting at the center of a developer's expression of code. As such, it had to provide familiar orthogonality and composability with other features of the system. Integrating separately authored components had to be natural, reliable, and predictable.

### Operation Result Pattern

Return a Union of an OK Result (success or failure without error) OR an error result.

OK, fulfilled.

accomplishment
achievement
realization
success
attainment
consummation
actualization
fruition

Should provide

* filter
* recover ?
* map
    * mapSuccess - transforms the value of a successful result.
    * mapError  -  transforms the value of an error result.
* flatMap
    * flatMapSuccess -  transforms a successful result into a different one
    * flatMapError - transforms a failed result into a different one.

Creating Result Objects

* Results.success
* Results.ofNullable creates a new result based on the given possibly-null value.
* Results.ofOptional creates a new result based on the given possibly-empty optional.
* Results.ofCallable creates a new result based on the given possibly-throwing task.
* Results.ofRunnable creates a new result based on the given possibly-throwing task.
* Results.error

Conditional Actions

* ifSuccess performs the given action with a successful result's value.
* ifFailure performs the given action with a failed result's value.
* ifSuccessOrElse performs either of the given actions with a result's value.

Advantages

* It is more explicit than throwing an Exception.
    * Why? Because the operation result type is explicitly specified as the returned value of the method, which makes it pretty obvious compared to knowing what Exception could be thrown by the operation.
* It is faster.
    * Why? because returning an object is faster than throwing an Exception.

Disadvantages

* It is more complex to use than exceptions.
    * Why? Because it must be "manually propagated up the call stack" (AKA returned by the callee and handled by the caller).

Rule of Thumb : If you return Operating Result, never throw an exception. If someting is thrown. Return an Error esult with an exceptional.

Places where it really works

* Functions that perform long-running calculations or operations
* Functions that run business rules or validations
* Functions that call out to unpredictable 3rd parties, like data access or IO

Some cases where you might NOT want to use operation results:

* Functions that are operating on the UI
* Code that does logging, or error reporting

### Recent Improvements

#### Enhanced Exception Handling
- **Unified API**: All monadic operations now use consistent `Checked*` interfaces for explicit exception handling
- **Simplified `orElse`**: Changed from `orElse(CheckedSupplier<Result<T>>)` to `orElse(Result<T>)` for cleaner usage
- **Improved `act` Method**: Now operates directly on payload values for successes, with no-op behavior for failures

#### Test Coverage Achievements
- **100% Line Coverage**: Comprehensive test suite covering all core components
- **Exception Path Testing**: Complete coverage of exception handling scenarios including:
  - `InterruptedException` handling with proper thread interrupt flag restoration
  - `RuntimeException` and checked exception propagation
  - Null parameter validation across all methods
- **Edge Case Coverage**: Tests for boundary conditions and error scenarios

#### Code Quality Improvements
- **Null Safety**: Explicit null checks using `Objects.requireNonNull()` throughout
- **Javadoc Updates**: Comprehensive documentation for all public APIs
- **API Consistency**: Unified method signatures and behavior patterns
- **Legacy Code Analysis**: Identified unused `ExceptionalConsumer` and `ExceptionalPredicate` interfaces

### Monads

A Monad is a concept in functional programming that describes computations as a series of steps.
It's a design pattern that allows you to structure your programs in a way that's easier to reason about.
Monads can be used to handle side effects, manage state, handle exceptions, and more.

The `Result<T>` type in Endeavour implements the Monad pattern with these key characteristics:

* **Wrapping**: `Try.success(value)` wraps a value in a successful result
* **Binding**: `flatMap()` allows chaining operations that return new Results
* **Mapping**: `map()` transforms values within the Result context
* **Error Handling**: Failures are preserved and propagated through the chain

This provides a safe, composable way to handle operations that may fail without using exceptions for control flow.

### Dyad

A Dyad is a term from the APL programming language, referring to a function that takes two arguments. In other words, it's a binary function. The term is used in functional
programming to describe functions that operate on two inputs.

In the context of Java, a similar concept would be a BiFunction interface, which represents a function that accepts two arguments and produces a result. This is a functional
interface whose functional method is `apply(Object, Object)`.

Here's a simple example of using `BiFunction`:

```java
BiFunction<Integer, Integer, Integer> addition = (a, b) -> a + b;
int result = addition.apply(5, 3);
```

In this snippet, `BiFunction<Integer, Integer, Integer> addition = (a, b) -> a + b;` defines a `BiFunction` that takes two integers as input and returns their sum. The `apply`
method is then used to apply this function to the numbers 5 and 3, storing the result in the `result` variable.

This is a basic example, but it illustrates the core idea of a Dyad: it's a function that operates on two inputs.

### Triad

A Triad, similar to a Monad and Dyad, is a term from the APL programming language. It refers to a function that takes three arguments, making it a ternary function. This term is
used in functional programming to describe functions that operate on three inputs.

In the context of Java, a similar concept would be a `TriFunction` interface. However, unlike `BiFunction`, Java does not have a built-in `TriFunction` interface. But we can easily
create one. This interface would represent a function that accepts three arguments and produces a result.

Here's a simple example of how you might define and use a `TriFunction`:

```java
@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}

TriFunction<Integer, Integer, Integer, Integer> addition = (a, b, c) -> a + b + c;
int result = addition.apply(5, 3, 2);
```

In this snippet, `TriFunction<Integer, Integer, Integer, Integer> addition = (a, b, c) -> a + b + c;` defines a `TriFunction` that takes three integers as input and returns their
sum. The `apply` method is then used to apply this function to the numbers 5, 3, and 2, storing the result in the `result` variable.

This is a basic example, but it illustrates the core idea of a Triad: it's a function that operates on three inputs.

### Tetrad

<!-- MARKED FOR REVIEW: This section appears incomplete. Consider completing or removing. -->

### Try

The `Try` Monad is a concept in functional programming that encapsulates computations which may either result in a value or throw an exception.
It's a design pattern that allows you to structure your programs in a way that's easier to reason about, especially when dealing with error handling.
Specifically

In Java, there isn't a built-in `Try` Monad, but it can be implemented using standard Java features. The idea is to wrap a computation in a `Try` object, and then provide methods
to handle both the success and failure cases.

Here's a simple example of how you might define and use a `Try` Monad:

```java
public abstract class Try<T> {
    public abstract T get() throws Exception;
    public abstract boolean isSuccess();

    public static <T> Try<T> of(Callable<T> callable) {
        try {
            return new Success<>(callable.call());
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    private static class Success<T> extends Try<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    private static class Failure<T> extends Try<T> {
        private final Exception exception;

        public Failure(Exception exception) {
            this.exception = exception;
        }

        @Override
        public T get() throws Exception {
            throw exception;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
    }
}

Try<Integer> tryMonad = Try.of(() -> Integer.parseInt("123"));
if (tryMonad.isSuccess()) {
    System.out.println(tryMonad.get());
} else {
    System.out.println("Parsing failed");
}
```

In this example, `Try.of(() -> Integer.parseInt("123"))` creates a `Try` that contains the result of parsing a string to an integer. If the parsing is successful, `isSuccess()`
returns true and `get()` returns the parsed integer. If the parsing fails (for example, if the string cannot be parsed to an integer), `isSuccess()` returns false and `get()`
throws the exception that occurred during parsing.

This is a basic example, but it demonstrates the core idea of a `Try` Monad: it's a way to encapsulate computations that may either result in a value or throw an exception,
allowing for more robust error handling.

### Either

The `either` concept in Erlang is not a built-in feature of the language, but it's a common pattern used in functional programming languages. It's often used to handle computations
that can result in two different types of values, typically representing success and failure cases.

In Erlang, this pattern can be implemented using tuples. A common convention is to use a tuple where the first element is an atom such as `ok` or `error`, and the second element is
the actual value or error information.

Here's a simple example of how you might use this pattern in Erlang:

```erlang
case some_function() of
    {ok, Value} ->
        %% Handle the success case
        io:format("Success: ~p~n", [Value]);
    {error, Reason} ->
        %% Handle the error case
        io:format("Error: ~p~n", [Reason])
end.
```

In this snippet, `some_function()` is expected to return either `{ok, Value}` or `{error, Reason}`. The `case` statement is then used to handle these two possible return values.
If `some_function()` returns `{ok, Value}`, the success case is handled and the value is printed. If `some_function()` returns `{error, Reason}`, the error case is handled and the
reason for the error is printed.

This is a basic example, but it illustrates the core idea of the `either` pattern: it's a way to handle computations that can result in two different types of values, allowing for
more robust error handling.

### Operational Result

The Operational Result pattern is a software design pattern often used in functional programming. It's a way to handle computations that can result in either a success or a
failure, and it's particularly useful for error handling. This pattern is similar to the `Either` type in some functional programming languages, or the `Try` Monad in others.

In Java, this pattern can be implemented using a class that encapsulates the result of an operation, which can be either a success or a failure. This class typically provides
methods to check if the operation was successful, retrieve the result in case of success, or retrieve the error in case of failure.

Here's a simple example of how you might define and use an `OperationalResult` class:

```java
public class OperationalResult<T> {
    private final T result;
    private final Exception error;

    private OperationalResult(T result, Exception error) {
        this.result = result;
        this.error = error;
    }

    public static <T> OperationalResult<T> success(T result) {
        return new OperationalResult<>(result, null);
    }

    public static <T> OperationalResult<T> failure(Exception error) {
        return new OperationalResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }
}

OperationalResult<Integer> result = OperationalResult.success(123);
if (result.isSuccess()) {
    System.out.println(result.getResult());
} else {
    System.out.println(result.getError().getMessage());
}
```

In this example, `OperationalResult.success(123)` creates an `OperationalResult` that represents a successful operation with a result of 123. The `isSuccess()` method is then used
to check if the operation was successful. If it was, `getResult()` is used to retrieve the result. If the operation was not successful, `getError().getMessage()` is used to
retrieve the error message.

This is a basic example, but it demonstrates the core idea of the Operational Result pattern: it's a way to encapsulate the result of an operation, which can be either a success or
a failure, allowing for more robust error handling.

### Operation Outcomes

The Endeavour library implements a simplified operational result pattern with two main outcome types:

* **Success** - Operation completed successfully
    * `QuantSuccess<T>` - Success with a payload value
    * `QualSuccess<T>` - Success without a payload (qualitative success)
* **Failure** - Operation failed
    * Contains detailed error information via `FailureDescription`
    * Supports templated error messages
    * Preserves exception causes and stack traces

#### Key Operations

**Creation:**
* `Try.success(value)` - Create a successful result with a value
* `Try.success()` - Create a qualitative success without a value  
* `Try.failure(message)` - Create a failure with a message
* `Try.ofCallable(callable)` - Wrap a potentially throwing operation
* `Try.ofRunnable(runnable)` - Wrap a potentially throwing void operation

**Transformation:**
* `map(CheckedFunction<T,U>)` - Transform success values, preserve failures
* `flatMap(CheckedFunction<T,Result<U>>)` - Chain operations that return Results
* `reduce(CheckedFunction<T,V>, CheckedFunction<Failure<T>,V>)` - Extract values from both success and failure cases

**Conditional Actions:**
* `ifSuccess(CheckedConsumer<Success<T>>)` - Perform action on success
* `ifFailure(CheckedConsumer<Failure<T>>)` - Perform action on failure
* `act(CheckedConsumer<T>)` - Perform action on success payload, no-op on failure

**Error Recovery:**
* `orElse(Result<T>)` - Provide alternative result for failures
* `orElseGet(CheckedSupplier<Result<T>>)` - Provide alternative via supplier for failures

### Basic Resources on the Operational Result Pattern

#### What is the Operational Result Pattern?

- [Operational Result Pattern](https://martinfowler.com/articles/replaceThrowWithNotification.html)
- [Operational Result Pattern – A simple guide] https://medium.com/@cummingsi1993/the-operation-result-pattern-a-simple-guide-fe10ff959080
- [Result Pattern] https://medium.com/@wgyxxbf/result-pattern-a01729f42f8c

#### Language-specific articles

- [Operational result pattern in Java] https://www.linkedin.com/pulse/operation-result-pattern-java-artyom-panfutov-t94ae/
-  https://belief-driven-design.com/functional-programming-with-java-exception-handling-e69997c11d3/
- [Operational Result pattern in Kotlin] https://adambennett.dev/2020/05/the-result-monad/
- [Operation Result in C#] https://www.forevolve.com/en/articles/2018/03/19/operation-result/

#### Other Operational Result Pattern Repositories

- [Vavr](https://www.vavr.io/)
- [jOOL](https://github.com/jOOQ/jOOL)
- [Either](https://github.com/jbock-java/either)
- [Java Monads](https://github.com/enelson/java_monads)
- [Java functional adapter](https://github.com/armtuk/java-functional-adapter/tree/develop/src/main/java/com/plexq/functional)

### Test Coverage

The project maintains 100% line coverage across all core components:

- **Result Monad**: Complete coverage of all success and failure paths
- **Exception Handling**: Full coverage of checked exception scenarios
- **Edge Cases**: Comprehensive testing of null parameters and boundary conditions
- **Integration Tests**: End-to-end testing of monadic operation chains

Coverage reports are generated in `target/site/jacoco/index.html` after running `mvn test jacoco:report`.

## Contributing

This library is currently in active development. Key areas for contribution:

- **API Stabilization**: Help finalize the public API before version 1.0
- **Performance Optimization**: Identify and optimize hot paths
- **Documentation**: Improve examples and usage guides
- **Code Cleanup**: Remove unused `ExceptionalConsumer` and `ExceptionalPredicate` interfaces
- **Additional Features**: Consider adding more monadic operations as needed
