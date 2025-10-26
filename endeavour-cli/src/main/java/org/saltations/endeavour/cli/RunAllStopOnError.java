package org.saltations.endeavour.cli;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.saltations.endeavour.Failure;
import org.saltations.endeavour.Result;
import org.saltations.endeavour.Success;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.IExecutionStrategy;


/**
 * This is a custom execution strategy that will execute all commands in the chain, and stop on the first error.
 * It is used to ensure that all commands in the chain are executed, and that the first error is propagated.
 * <h4>Note</h4>
 * This should only be used for commands and subcommands that implement Callable<Result<Integer>>.
 */

@Slf4j
public class RunAllStopOnError implements IExecutionStrategy  {

    @Override 
    public int execute(CommandLine.ParseResult parseResult) throws CommandLine.ExecutionException {

        var helpExit = CommandLine.executeHelpRequest(parseResult);

        if (helpExit != null) 
        {
            return ExitCode.OK;
        }

        var cmdChain = parseResult.asCommandLineList();

        // Validate each populated command object in the command chain before execution.

        for (var cmdLine : cmdChain) {

            var userObj = cmdLine.getCommandSpec().userObject();

            // Bail if the command class does not implements Callable

            if (!(userObj instanceof Callable)) {
                System.err.println("Command class for " +  cmdLine.getCommandSpec().name() +  "  does not implement Callable");
                return ExitCode.SOFTWARE;
            }
        }

        // Execute the command chain and handle the results

        var results = new ArrayList<Object>();

        for (var cmdObj : cmdChain) {

            var result = cmdObj.getCommandSpec().userObject();

            // Bail if the command does not implements Callable

            if (!(result instanceof Callable)) {
                log.error("Command class for{} does not implement Callable", cmdObj.getCommandSpec().name());
                return ExitCode.SOFTWARE;
            }

            // Execute the command and check if it returns an Outcome object

            try {

                var outcome = ((Callable<?>) result).call();

                // Bail if the command does not return a Result object
                if (!(outcome instanceof Result)) {
                    System.err.println("Command does not return an operating result of Result");
                    return ExitCode.SOFTWARE;
                }

                // Evaluate the result 

                var operatingResult = ((Result<?>) outcome);

                switch (operatingResult) {
                    case Success<?> success:
                        results.add(success.get());
                        break;
                    case Failure<?> failure:
                        System.err.println(failure.getDetail());
                        return ExitCode.SOFTWARE;
                    default:
                        throw new CommandLine.ExecutionException(cmdObj, "Error checking command return type", new Exception("Unknown result type"));
                }

            } catch (Exception e) {
                throw new CommandLine.ExecutionException(cmdObj, "Error checking command return type", e);
            }

        }
  
        return ExitCode.OK;

    }

}