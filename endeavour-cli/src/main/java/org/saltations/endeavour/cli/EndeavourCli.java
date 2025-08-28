package org.saltations.endeavour.cli;

import java.util.concurrent.Callable;

import org.saltations.endeavour.Outcome;
import org.saltations.endeavour.Outcomes;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "endeavour",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "Endeavour CLI - Demonstrates functional error handling"
)
public class EndeavourCli implements Callable<Integer> {

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output")
    private boolean verbose;

    @Parameters(description = "Command to execute", defaultValue = "demo")
    private String command;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new EndeavourCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        if (verbose) {
            System.out.println("Endeavour CLI - Verbose mode enabled");
        }

        return switch (command.toLowerCase()) {
            case "demo" -> runDemo();
            case "success" -> runSuccessExample();
            case "failure" -> runFailureExample();
            case "chain" -> runChainExample();
            default -> {
                System.err.println("Unknown command: " + command);
                System.err.println("Available commands: demo, success, failure, chain");
                yield 1;
            }
        };
    }

    private int runDemo() {
        System.out.println("=== Endeavour CLI Demo ===\n");
        
        System.out.println("1. Success Example:");
        runSuccessExample();
        
        System.out.println("\n2. Failure Example:");
        runFailureExample();
        
        System.out.println("\n3. Chain Example:");
        runChainExample();
        
        return 0;
    }

    private int runSuccessExample() {
        Outcome<String> result = Outcomes.succeed("Hello from Endeavour!");
        
        return result.transform(
            success -> {
                System.out.println("✅ Success: " + success.get());
                return 0;
            },
            failure -> {
                System.err.println("❌ Unexpected failure: " + failure.fail().getTotalMessage());
                return 1;
            }
        );
    }

    private int runFailureExample() {
        Outcome<String> result = Outcomes.failWithDetails("This is a demonstration error");
        
        return result.transform(
            success -> {
                System.out.println("✅ Unexpected success: " + success.get());
                return 0;
            },
            failure -> {
                System.out.println("❌ Expected failure: " + failure.fail().getTotalMessage());
                System.out.println("   Error code: " + failure.getType());
                return 0;
            }
        );
    }

    private int runChainExample() {
        // Simulate a chain of operations that could fail
        Outcome<String> result = Outcomes.succeed("initial")
            .flatMap(this::processStep1)
            .flatMap(this::processStep2)
            .flatMap(this::processStep3);
        
        return result.transform(
            success -> {
                System.out.println("✅ Chain completed successfully: " + success.get());
                return 0;
            },
            failure -> {
                System.out.println("❌ Chain failed at step: " + failure.getType());
                System.out.println("   Error: " + failure.fail().getTotalMessage());
                return 0;
            }
        );
    }

    private Outcome<String> processStep1(String input) {
        if (verbose) {
            System.out.println("   Processing step 1 with: " + input);
        }
        return Outcomes.succeed(input + " -> step1");
    }

    private Outcome<String> processStep2(String input) {
        if (verbose) {
            System.out.println("   Processing step 2 with: " + input);
        }
        // Simulate a failure in step 2
        if (input.contains("fail")) {
            return Outcomes.failWithDetails("Step 2 failed as expected");
        }
        return Outcomes.succeed(input + " -> step2");
    }

    private Outcome<String> processStep3(String input) {
        if (verbose) {
            System.out.println("   Processing step 3 with: " + input);
        }
        return Outcomes.succeed(input + " -> step3");
    }
}
