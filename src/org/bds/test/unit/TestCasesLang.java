package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test cases for language & compilation
 * <p>
 * Note: These test cases just check language parsing and compilation (what is supposed to compile OK, and what is not).
 *
 * @author pcingola
 */
public class TestCasesLang extends TestCasesBase {

    public TestCasesLang() {
        dir = "test/unit/lang/";
    }

    @Test
    public void test00() {
        // EOF test: No '\n' or ';' at the end of last line
        compileOk(dir + "test00.bds");
    }

    @Test
    public void test25() {
        // Multi-line strings
        compileOk(dir + "test25.bds");
    }


    @Test
    public void test27() {
        // Type casting error: real to int
        String errs = "ERROR [ file 'test/unit/lang/test27.bds', line 2 ] :	Cannot cast real to int\n";
        compileErrors(dir + "test27.bds", errs);
    }

    @Test
    public void test35() {
        // String interpolation
        compileOk(dir + "test35.bds");
    }

    @Test
    public void test36() {
        // String interpolation: Missing variable
        String errs = "ERROR [ file 'test/unit/lang/test36.bds', line 3 ] :	Symbol 'j' cannot be resolved\n";
        compileErrors(dir + "test36.bds", errs);
    }


    @Test
    public void test42() {
        // Includes
        compileOk(dir + "test42.bds");
    }


    @Test
    public void test60_1() {
        // Include statements: Multiple includes. Include order should not affect compilation
        compileOk(dir + "test60.bds");
    }

    @Test
    public void test14() {
        // String interpolation
        runAndCheck(dir + "run_14.bds", "s", "this is string interpolation: int i = 42 and str = \"hi\" and both hi42");
    }
    @Test
    public void test38() {
        // String interpolation: Literal string with '$'
        runAndCheck(dir + "run_38.bds", "su", "$s world \\n");
    }
    @Test
    public void test40() {
        // Command line arguments: Setting global variables from command line arguments
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("file", "zzz.txt");
        expectedValues.put("opt", "true");
        expectedValues.put("num", "42");
        expectedValues.put("rnum", "3.1415");
        expectedValues.put("args", "[-file, zzz.txt, -num, 42, -rnum, 3.1415, -opt, -notProcessed, more, arguments]");

        // Arguments to add after program name
        ArrayList<String> argsAfter = new ArrayList<>();

        argsAfter.add("-file");
        argsAfter.add("zzz.txt");

        argsAfter.add("-num");
        argsAfter.add("42");

        argsAfter.add("-rnum");
        argsAfter.add("3.1415");

        argsAfter.add("-opt");

        argsAfter.add("-notProcessed");
        argsAfter.add("more");
        argsAfter.add("arguments");

        runAndCheck(dir + "run_40.bds", expectedValues, argsAfter);
    }


    @Test
    public void test47() {
        // Command line arguments: Passing a list of values
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("in", "[in1.txt, in2.txt, in3.txt]");
        expectedValues.put("out", "zzz.txt");
        expectedValues.put("ok", "true");

        ArrayList<String> args = new ArrayList<>();

        args.add("-ok");

        args.add("-in");
        args.add("in1.txt");
        args.add("in2.txt");
        args.add("in3.txt");

        args.add("-out");
        args.add("zzz.txt");

        runAndCheck(dir + "run_47.bds", expectedValues, args);
    }

    @Test
    public void test48() {
        // Statements: Warning and error
        runAndCheck(1, dir + "run_48.bds", "step", 2L);
    }

    @Test
    public void test50() {
        // Include
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("i", "32");
        expectedValues.put("j", "302");
        expectedValues.put("jx", "44");
        expectedValues.put("jy", "91");

        runAndCheck(dir + "run_50.bds", expectedValues);
    }

    @Test
    public void test51() {
        // Interpolating a hash
        runAndCheck(dir + "run_51.bds", "hash", "{ hi => bye }");
    }
    @Test
    public void test60() {
        // Command line arguments: boolean
        String fileName = dir + "run_60.bds";
        String[] args = {fileName, "-b"};
        runAndCheck(fileName, args, "b", true);
    }

    @Test
    public void test61() {
        // Command line arguments: boolean
        String fileName = dir + "run_60.bds";
        String[] args = {fileName, "-b", "true"};
        runAndCheck(fileName, args, "b", true);
    }

    @Test
    public void test62() {
        // Command line arguments: boolean
        String fileName = dir + "run_60.bds";
        String[] args = {fileName, "-b", "false"};
        runAndCheck(fileName, args, "b", false);
    }


    @Test
    public void test67() {
        // String interpolation, escaping '$'
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("s", "varS");
        expectedValues.put("s1", "Hi '$'");
        expectedValues.put("s2", "Hi $");
        expectedValues.put("s3", "Hi $ bye");
        runAndCheck(dir + "run_67.bds", expectedValues);
    }

    @Test
    public void test97() {
        // String interpolation and '\t', '\n' characters
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("h1", "1");
        expectedValues.put("h2", "3");
        expectedValues.put("h3", "2");
        expectedValues.put("h4", "2");
        expectedValues.put("h5", "1");
        runAndCheck(dir + "run_97.bds", expectedValues);
    }

    @Test
    public void test111() {
        // Error statement
        runAndCheck(dir + "run_111.bds", "runOk", "false");
    }

    @Test
    public void test112() {
        // Exit statement
        runAndCheck(dir + "run_112.bds", "runOk", "false");
    }

    @Test
    public void test123_literals() {
        // String literals, interpolation and escaped characters
        String output = "print_quote        |\\t|\n" //
                + "print_quote        |\\t|    variable:$hi\n" //
                + "print_double       |\t|\n" //
                + "print_double       |\t|    variable:Hello\n" //
                + "print_double_esc   |\\t|\n" //
                + "print_double_esc   |\\t|   variable:Hello\n" //
                ;

        runAndCheckStdout(dir + "run_123.bds", output);
    }

    @Test
    public void test125_automatic_help() {
        // Command line options: Help
        String output = "Command line options 'run_125.bds' :\n" //
                + "\t-mean <int>                                  : Help for argument 'mean' should be printed here\n" //
                + "\t-min <int>                                   : Help for argument 'min' should be printed here\n" //
                + "\t-num <int>                                   : Number of times 'hi' should be printed\n" //
                + "\t-salutation <string>                         : Salutation to use\n" //
                + "\t-someVeryLongCommandLineArgumentName <bool>  : This command line argument has a really long name\n" //
                + "\t-useTab <bool>                               : Use tab before printing line\n" //
                + "\n" //
                ;

        runAndCheckHelp(dir + "run_125.bds", output);
    }

    @Test
    public void test125b_automatic_help_unsorted() {
        // Command line options: Help
        String output = "Command line options 'run_125b.bds' :\n" //
                + "\t-useTab <bool>                               : Use tab before printing line\n" //
                + "\t-someVeryLongCommandLineArgumentName <bool>  : This command line argument has a really long name\n" //
                + "\t-salutation <string>                         : Salutation to use\n" //
                + "\t-num <int>                                   : Number of times 'hi' should be printed\n" //
                + "\t-min <int>                                   : Help for argument 'min' should be printed here\n" //
                + "\t-mean <int>                                  : Help for argument 'mean' should be printed here\n" //
                + "\n" //
                ;

        runAndCheckHelp(dir + "run_125b.bds", output);
    }

    /**
     * Show help when there are no arguments
     */
    @Test
    public void test125c_automatic_help() {
        // Command line options: Help
        String output = "Command line options 'run_125c.bds' :\n" //
                + "\t-mean <int>                                  : Help for argument 'mean' should be printed here\n" //
                + "\t-min <int>                                   : Help for argument 'min' should be printed here\n" //
                + "\t-num <int>                                   : Number of times 'hi' should be printed\n" //
                + "\t-salutation <string>                         : Salutation to use\n" //
                + "\t-someVeryLongCommandLineArgumentName <bool>  : This command line argument has a really long name\n" //
                + "\t-useTab <bool>                               : Use tab before printing line\n" //
                + "\n" //
                ;

        runAndCheckHelp(dir + "run_125c.bds", output);
    }

    @Test
    public void test134_automatic_help_sections() {
        // Show help when there are no arguments
        String output = "This program does blah\n" //
                + "Actually, a lot of blah blah\n" //
                + "    and even more blah\n" //
                + "    or blah\n" //
                + "\t-quiet <bool>     : Be very quiet\n" //
                + "\t-verbose <bool>   : Be verbose\n" //
                + "Options related to database\n" //
                + "\t-dbName <string>  : Database name\n" //
                + "\t-dbPort <int>     : Database port\n" //
                + "\n" //
                ;

        runAndCheckHelp(dir + "run_134.bds", output);
    }

    @Test
    public void test158_log() {
        // Log messages to console
        runAndCheckStderr(dir + "run_158.bds", "hi there");
    }
    @Test
    public void test164() {
        // PrintErr: print to stderr
        runAndCheck(dir + "run_164.bds", "out", "hi");
    }

}
