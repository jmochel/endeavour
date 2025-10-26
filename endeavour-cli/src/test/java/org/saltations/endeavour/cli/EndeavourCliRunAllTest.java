package org.saltations.endeavour.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;
import org.saltations.endeavour.Result;
import org.saltations.endeavour.Try;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

class EndeavourCliRunAllTest {


    @Test
    public void givenSimpleCommandWhenGivenCorrectArgsThenSuccessIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var app = new RootCommand();
        var cmdLine = new CommandLine(app);
        cmdLine.setExecutionStrategy(new RunAllStopOnError());

        cmdLine.execute("-h");

        assertThat(baos.toString()).contains("Usage:");
    }

    @Test
    public void givenCommandWithSubCommandsWhenGivenCorrectSubCommandArgsThenSuccessIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var app = new RootCommand();
        var cmdLine = new CommandLine(app);
        cmdLine.setExecutionStrategy(new RunAllStopOnError());
        cmdLine.execute("sc");

        assertThat(baos.toString()).contains("Invoked Root Command")
            .contains("Successfully ran Root Command")
            .contains("Invoked SubCommand1")
            .contains("Successfully ran SubCommand1")
            ;
    }

    @Test
    public void givenCommandWithSubCommandsWhenGivenIncorrectSubCommandArgsThenFailureIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var app = new RootCommand();
        var cmdLine = new CommandLine(app);
        cmdLine.setExecutionStrategy(new RunAllStopOnError());
        cmdLine.execute("sc", "-f", "ssc");

        assertThat(baos.toString()).contains("Invoked Root Command")
            .contains("Successfully ran Root Command")
            .contains("Invoked SubCommand1")
            .contains("Failed to run SubCommand1")
            .doesNotContain("Invoked SubSubCommand1")
            ;
    }

    @Test
    public void givenCommandWithSubSubCommandsWhenGivenCorrectSubSubCommandArgsThenSuccessIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var app = new RootCommand();
        var cmdLine = new CommandLine(app);
        cmdLine.setExecutionStrategy(new RunAllStopOnError());
        cmdLine.execute("sc", "ssc");

        assertThat(baos.toString())
        .contains("Invoked Root Command").contains("Successfully ran Root Command").contains("Invoked SubCommand1").contains("Invoked SubSubCommand1");
    }


    @Test
    public void givenCommandWithSubSubCommandsWhenGivenIncorrectSubSubCommandArgsThenFailureIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var app = new RootCommand();
        var cmdLine = new CommandLine(app);
        cmdLine.setExecutionStrategy(new RunAllStopOnError());
        cmdLine.execute("sc", "ssc", "-f");

        assertThat(baos.toString())
            .contains("Invoked Root Command")
            .contains("Successfully ran Root Command")
            .contains("Invoked SubCommand1")
            .contains("Successfully ran SubCommand1")
            .contains("Invoked SubSubCommand1")
            .contains("Failed to run SubSubCommand1")
            .doesNotContain("Successfully ran SubSubCommand1");
    }

    @Command(
    name = "root-app",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "Root Command",
    subcommands = {
        SubCommand1.class
    })    
    static class RootCommand implements Callable<Result<Integer>> {

        @Option(names = {"-f", "--fail"}, description = "Fail the command")
        private boolean fail;

        @Override
        public Result<Integer> call() {
            System.out.println("Invoked Root Command");
            if (fail) {
                System.out.println("Failed to run Root Command");
                return Try.failureWithDetails("Failed to run Root Command");
            }

            System.out.println("Successfully ran Root Command");
            return Try.success(0);
        }
    }

    @Command(name = "sc", 
    description = "Sub Command Description 1",
    mixinStandardHelpOptions = true,
    subcommands = {
        SubSubCommand1.class
    }) 
    static class SubCommand1 implements Callable<Result<Integer>> {

        @Option(names = {"-f", "--fail"}, description = "Fail the command")
        private boolean fail;

        @Override
        public Result<Integer> call() {
            System.out.println("Invoked SubCommand1");
            if (fail) {
                System.out.println("Failed to run SubCommand1");
                return Try.failureWithDetails("Failed to run SubCommand1");
            }

            System.out.println("Successfully ran SubCommand1");
            return Try.success(0);
        }

    }
    
    @Command(name = "ssc", 
    description = "Sub Sub Command Description 1",
    mixinStandardHelpOptions = true) 
    static class SubSubCommand1 implements Callable<Result<Integer>> {

        @Option(names = {"-f", "--fail"}, description = "Fail the command")
        private boolean fail;

        @Override
        public Result<Integer> call() {
            System.out.println("Invoked SubSubCommand1");
            if (fail) {
                System.out.println("Failed to run SubSubCommand1");
                return Try.failureWithDetails("Failed to run SubSubCommand1");
            }

            System.out.println("Successfully ran SubSubCommand1");
            return Try.success(0);
        }
    }
   

}
