package org.saltations.endeavour.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import picocli.CommandLine;

class EndeavourCliTest {

    private EndeavourCli cli;

    @BeforeEach
    void setUp() {
        cli = new EndeavourCli();
    }

    @Test
    public void testWithCommandLineOption() throws Exception {
        
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        var cmd = new CommandLine(cli);
        cmd.execute("success");

        assertThat(baos.toString()).contains("Success: Hello from Endeavour!");
    }

    
}
