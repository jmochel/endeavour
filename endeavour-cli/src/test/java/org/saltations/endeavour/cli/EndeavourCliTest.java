package org.saltations.endeavour.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.saltations.endeavour.FailureDescription;
import org.saltations.endeavour.Outcome;
import org.saltations.endeavour.Outcomes;

class EndeavourCliTest {

    private EndeavourCli cli;

    @BeforeEach
    void setUp() {
        cli = new EndeavourCli();
    }

    @Test
    void testSuccessExample() {
        Outcome<FailureDescription, String> result = Outcomes.succeed("Hello from Endeavour!");
        
        boolean[] successCalled = {false};
        int exitCode = result.transform(
            success -> {
                successCalled[0] = true;
                assertThat(success.get()).isEqualTo("Hello from Endeavour!");
                return 0;
            },
            failure -> {
                throw new AssertionError("Should not have failed");
            }
        );
        
        assertThat(successCalled[0]).isTrue();
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void testFailureExample() {
        Outcome<FailureDescription, String> result = Outcomes.failWithDetails("This is a demonstration error");
        
        boolean[] failureCalled = {false};
        int exitCode = result.transform(
            success -> {
                throw new AssertionError("Should not have succeeded");
            },
            failure -> {
                failureCalled[0] = true;
                assertThat(failure.getType().getTitle()).isEqualTo("generic-failure");
                assertThat(failure.fail().getDetail()).isEqualTo("This is a demonstration error");
                return 0;
            }
        );
        
        assertThat(failureCalled[0]).isTrue();
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void testChainExampleSuccess() {
        Outcome<FailureDescription, String> result = Outcomes.succeed("initial")
            .flatMap(input -> Outcomes.succeed(input + " -> step1"))
            .flatMap(input -> Outcomes.succeed(input + " -> step2"))
            .flatMap(input -> Outcomes.succeed(input + " -> step3"));
        
        boolean[] successCalled = {false};
        int exitCode = result.transform(
            success -> {
                successCalled[0] = true;
                assertThat(success.get()).isEqualTo("initial -> step1 -> step2 -> step3");
                return 0;
            },
            failure -> {
                throw new AssertionError("Should not have failed");
            }
        );
        
        assertThat(successCalled[0]).isTrue();
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void testChainExampleFailure() {
        Outcome<FailureDescription, String> result = Outcomes.succeed("initial")
            .flatMap(input -> Outcomes.succeed(input + " -> step1"))
            .flatMap(input -> Outcomes.failWithDetails("Step 2 failed"))
            .flatMap(input -> Outcomes.succeed(input + " -> step3")); // This should not be called
        
        boolean[] failureCalled = {false};
        int exitCode = result.transform(
            success -> {
                throw new AssertionError("Should not have succeeded");
            },
            failure -> {
                failureCalled[0] = true;
                assertThat(failure.getType().getTitle()).isEqualTo("generic-failure");
                assertThat(failure.fail().getDetail()).isEqualTo("Step 2 failed");
                return 0;
            }
        );
        
        assertThat(failureCalled[0]).isTrue();
        assertThat(exitCode).isEqualTo(0);
    }
}
