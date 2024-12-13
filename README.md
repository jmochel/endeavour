# Endeavour

Endeavour is a Java library used to implement the operational result pattern.

![Tactical Ferret](./tactical-ferret-logo.png)
[^1] Tactical Ferret images created using Craiyon

>
> This library is not yet ready for prime time. 
> It is just an example until it starts getting versioned and 
> this message is removed
>

## Basic Resources on the Operational Result Pattern

### What is the Operational Result Pattern?

- [Operational Result Pattern](https://martinfowler.com/articles/replaceThrowWithNotification.html)
- [Operational Result Pattern – A simple guide] https://medium.com/@cummingsi1993/the-operation-result-pattern-a-simple-guide-fe10ff959080
- [Result Pattern] https://medium.com/@wgyxxbf/result-pattern-a01729f42f8c

### Language-specific articles

- [Operational result pattern in Java] https://www.linkedin.com/pulse/operation-result-pattern-java-artyom-panfutov-t94ae/
-  https://belief-driven-design.com/functional-programming-with-java-exception-handling-e69997c11d3/
- [Operational Result pattern in Kotlin] https://adambennett.dev/2020/05/the-result-monad/
- [Operation Result in C#] https://www.forevolve.com/en/articles/2018/03/19/operation-result/

### Other Operational Result Pattern Repositories

- [Vavr](https://www.vavr.io/)
- [jOOL](https://github.com/jOOQ/jOOL)
- [Either](https://github.com/jbock-java/either)
- [Java Monads](https://github.com/enelson/java_monads)
- [Java functional adapter](https://github.com/armtuk/java-functional-adapter/tree/develop/src/main/java/com/plexq/functional)

## Rationale for the Operational Result Pattern

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

>
> We believe that exceptions should rarely be used as part of a program's normal flow:
> exceptions should be reserved for unexpected events. Assume that an uncaught exception 
> will terminate your program and ask yourself, 
> “Will this code still run if I remove all the exception handlers?” 
> If the answer is “no”, then maybe exceptions are being used in nonexceptional circumstances.
> -- Dave Thomas and Andy Hunt
> 

-- Dave Thomas and Andy Hunt

## Library Intentions 

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
* Ideally, we’d be able to chain or combine a bunch of these operations together, and only handle the error state at the end of the computation

A failure in any result that is not a success in the context of the method.

. If your not sure if something should be an exception or a failure you could ask yourself “Would the user understand what to do with this failure message?”
Typically a user doesn’t know what to do with exception messages, they are intended for a technical person to do a more in depth troubleshooting of
the problem. Business failure messages however, could be interpreted by a person and they should be able to act on them.


# Success, Partial Success and Failure

Most outcomes in apis can be broken up into Successes, Partial Successes and Failures

* Success - Operation worked; We got a normal expected result
* Partial Successes - Operation worked for some portion of the values to be worked on
* Failure -  Operation failed; We did not get what we wanted
  * Failure can be broken down into
    * Failure with a cause
    * Failure with a category/title and detail

Wrap and invoke functions that might blow up and catch their errors
Be able to map from one success type to another across data boundaries, ie from domain to presentation
Be able to map errors from one type to another, perhaps converting a Throwable to something more useful to the presentation layer
Handle both success and failure whenever we want
Ideally, we’d be able to chain or combine a bunch of these operations together, and only handle the error state at the end of the computation


# We want a model that is

* Usable.
    * Must be easy for developers to do the “right” thing in the face of error
    * Should not impose excessive ceremony in order to write idiomatic code
    * Cognitively familiar to our target audience.
* Performant.
    * The common case needs to be extremely fast. That means as close to zero overhead as possible for success paths.
    * Any added costs for failure paths must be entirely “pay-for-play.”
* Diagnosable.
    * Debugging failures, either interactively or after-the-fact, needs to be productive and easy.
* Composable.
    * At the core, the error model is a programming language feature, sitting at the center of a developer’s expression of code. As such, it had to provide familiar orthogonality and composability with other features of the system. Integrating separately authored components had to be natural, reliable, and predictable.

# Operation Result Pattern

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

* ifSuccess performs the given action with a successful result’s value.
* ifFailure performs the given action with a failed result’s value.
* ifSuccessOrElse performs either of the given actions with a result’s value.

Advantages

* It is more explicit than throwing an Exception.
    * Why? Because the operation result type is explicitly specified as the returned value of the method, which makes it pretty obvious compared to knowing what Exception could be thrown by the operation.
* It is faster.
    * Why? because returning an object is faster than throwing an Exception.

Disadvantages

* It is more complex to use than exceptions.
    * Why? Because it must be “manually propagated up the call stack” (AKA returned by the callee and handled by the caller).

Rule of Thumb : If you return Operating Result, never throw an exception. If someting is thrown. Return an Error esult with an exceptional.

Places where it really works

* Functions that perform long-running calculations or operations
* Functions that run business rules or validations
* Functions that call out to unpredictable 3rd parties, like data access or IO

Some cases where you might NOT want to use operation results:

* Functions that are operating on the UI
* Code that does logging, or error reporting


## Implementation of the Operational Result Pattern

The operational result is represented in _Endeavour_ by the sealed interface `Outcome`. 
This interface has three implementations, `Success`, `PartialSuccess` and `Failure`.
Each one of these act as a monad: it wraps a value that may or may not be present and provides methods to perform computations on 
that value in a safe manner. 

Ideally, we would like to handle operations that can throw exceptions and turn them into operational results. In Java, 
this means managing the invocation of lambdas in a way that allows them to capture and transform exceptions and 
having lambdas (that implement functional interfaces) that can throw exceptions.   

* [ExceptionalBiConsumer.java](src/main/java/org/saltations/endeavour/ExceptionalBiConsumer.java)
* [ExceptionalCallable.java](src/main/java/org/saltations/endeavour/ExceptionalCallable.java)
* [ExceptionalConsumer.java](src/main/java/org/saltations/endeavour/ExceptionalConsumer.java)
* [ExceptionalFunction.java](src/main/java/org/saltations/endeavour/ExceptionalFunction.java)
* [ExceptionalPredicate.java](src/main/java/org/saltations/endeavour/ExceptionalPredicate.java)
* [ExceptionalRunnable.java](src/main/java/org/saltations/endeavour/ExceptionalRunnable.java)
* [ExceptionalSupplier.java](src/main/java/org/saltations/endeavour/ExceptionalSupplier.java)

These functional info interfaces take any exception thrown (checked or unchecked) and convert them to an unchecked exception.

## Monads

A Monad is a concept in functional programming that describes computations as a series of steps.
It's a design pattern that allows you to structure your programs in a way that's easier to reason about.
Monads can be used to handle side effects, manage state, handle exceptions, and more.

A Monad is any class (data type), which represents a specific calculation

* Yes, it encapsulated a mechanism for performing actions on results of either success or failure safely without
  causing a null pointer exception It must implement at least these two functions:
  * A function to wrap any basic value, creating a new monad. Also called the return function.  ofThrowable()
  * And a function that allows you to perform operations on a wrapped data type (monad). Also called the bind function. (flatMap)

In Java, there isn't a built-in Monad interface or class, but the concept can still be applied.
For example, the `Optional` class in Java can be thought of as a Monad. It wraps a value that may or may not be present,
and provides methods to perform computations on that value in a safe way.

Here's a simple example of using `Optional` as a Monad:

```java
Optional<String> optional = Optional.of("Hello, world!");
optional = optional.map(s -> s.toUpperCase());
optional.ifPresent(System.out::println);
```

In this example, `Optional.of("Hello, world!")` creates an `Optional` that contains a string. The `map` method is then used
to transform the value inside the `Optional` (if it is present) by applying a function to it. The `ifPresent` method is then used
to perform an action with the value (if it is present), in this case printing it to the console.

This is a very basic example, but it demonstrates the core idea of a Monad: it's a way to perform a series of
computations on a value in a controlled manner.

## Dyad

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

## Triad`

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

## Tetrad


## Try

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


## Either

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

## Operational Result

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


# Operation Outcomes

* Outcome
    * Success
        * Has success value
            * May be supplied
    * Failure
        * Has Failure value
            * Has Cause
            * Has category/title
            * Has detail
    * Partial Success
        * Has Failure value
            * Has Cause
            * Has category/title
            * Has detail
        * Has success value
            * May be supplied ?

* Events
    * Create
        * Outcome
            * from Fxn
            * from throwing Fxn
        * Success
            * From value
            * From value supplier ?
        * Failure
            * From cause
            * From type
            * From message template
        * Partial Success
            * From ????
    * Transform
        * Success<X> to Success<Y>
        * Success<X> to Failure
        * Success<X> to Partial Success<X>
        * Failure to Success?
        * Failure<X> to Failure<Y>
        * Failure to Partial Success<X>
        * Partial Success<X> to Success<X>
        * Partial Success<X> to Failure
