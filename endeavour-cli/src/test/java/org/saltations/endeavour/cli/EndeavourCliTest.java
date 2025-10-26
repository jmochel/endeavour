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

class EndeavourCliTest {

    @Test
    public void givenSimpleCommandWhenGivenCorrectArgsThenSuccessIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var cli = new DirtSimpleAppCommand();
        var cmd = new CommandLine(cli);
        cmd.execute("-h");

        assertThat(baos.toString()).contains("Usage:");
    }

    @Test
    public void givenCommandWithSubCommandsWhenGivenCorrectSubCommandArgsThenSuccessIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var cli = new TopLevelCommand();
        var cmd = new CommandLine(cli);
        cmd.execute("sc");

        assertThat(baos.toString()).contains("SubCommand1");
    }

    @Test
    public void givenCommandWithSubSubCommandsWhenGivenCorrectSubSubCommandArgsThenSuccessIsPrinted() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var cli = new TopLevelCommand();
        var cmd = new CommandLine(cli);
        cmd.execute("sc", "ssc");

        assertThat(baos.toString()).contains("SubSubCommand1");
    }

    @Command(
    name = "simple-app",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "Sample App Command")    
    static class DirtSimpleAppCommand implements Callable<Result<Integer>> {
        @Override
        public Result<Integer> call() {
            System.out.println("Top Level Command");
            return Try.success(0);
        }
    }

    @Command(
    name = "simple-app",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "Sample App Command",
    subcommands = {
        SubCommand1.class
    })    
    static class TopLevelCommand implements Callable<Result<Integer>> {
        @Override
        public Result<Integer> call() {
            System.out.println("Top Level Command");
            return Try.success(0);
        }
    }

    @Command(name = "sc", 
    description = "Sub Command",
    mixinStandardHelpOptions = true,
    subcommands = {
        SubSubCommand1.class
    }) 
    static class SubCommand1 implements Callable<Result<Integer>> {
        @Override
        public Result<Integer> call() {
            System.out.println("SubCommand1");
            return Try.success(0);
        }
    }
    
    @Command(name = "ssc", 
    description = "Sub Sub Command",
    mixinStandardHelpOptions = true) 
    static class SubSubCommand1 implements Callable<Result<Integer>> {
        @Override
        public Result<Integer> call() {
            System.out.println("SubSubCommand1");
            return Try.success(0);
        }
    }


    

}
