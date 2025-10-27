package org.saltations.endeavour;

import java.io.File;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FunctionalTest {

    @Test
    @Order(0)
    public void whenCreatingFunctionalInstanceThenSucceeds() {
        // Test that Functional constructor can be called
        var functional = new Functional();
        assertNotNull(functional, "Functional instance should not be null");
    }

    @Test
    public void whenWrappedExceptionalSupplierThrowsExceptionThenShouldThrowRecastException() {
        var supplier = Functional.toSupplier(() -> new File("/notarealpathhere/foo.txt").createNewFile());
        assertThrows(RuntimeException.class, () -> supplier.get());
    }

    @Test
    @Order(1)
    public void whenWrappedExceptionalFunctionThrowsExceptionThenShouldThrowRecastException() {
        var function = Functional.toFunction(x -> new File("/notarealpathhere/foo.txt").createNewFile());
        assertThrows(RuntimeException.class, () -> function.apply("nothing"));
    }

    @Test
    public void whenWrappedExceptionalConsumerThrowsExceptionThenShouldThrowRecastException() {
        var consumer = Functional.toConsumer(x -> new File("/notarealpathhere/foo.txt").createNewFile());
        assertThrows(RuntimeException.class, () -> consumer.accept("nothing"));
    }

    @Test
    public void whenWrappedExceptionalBiConsumerThrowsExceptionThenShouldThrowRecastException() {
        var consumer = Functional.toConsumer( (x,y) -> new File("/notarealpathhere/foo.txt").createNewFile());
        assertThrows(RuntimeException.class, () -> consumer.accept("nothing","nothing"));
    }

    @Test
    public void whenWrappedExceptionalRunnableThrowsExceptionThenShouldThrowRecastException() {
        var runnable = Functional.toRunnable(() -> new File("/notarealpathhere/foo.txt").createNewFile());
        assertThrows(RuntimeException.class, () -> runnable.run());
    }

    @Test
    public void whenWrappedExceptionalCallableThrowsExceptionThenShouldThrowRecastException() {
        var callable = Functional.toCallable(() -> new File("/notarealpathhere/foo.txt").createNewFile());
        assertThrows(RuntimeException.class, () -> callable.call());
    }
}
