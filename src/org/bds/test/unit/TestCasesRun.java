package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.test.TestCasesBase;
import org.bds.util.Timer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases that require BDS code execution and check results
 * <p>
 * Note: These test cases requires that the BDS code is correctly parsed, compiled and executes.
 *
 * @author pcingola
 */
public class TestCasesRun extends TestCasesBase {

    public TestCasesRun() {
        dir = "test/unit/run/";
    }

    @Test
    public void test00_plus_int() {
        // Binary operator: Integer addition
        runAndCheck(dir + "run_00_plus_int.bds", "i", 42L);
    }

    @Test
    public void test00_plus_real() {
        // Binary operator: Real addition
        runAndCheck(dir + "run_00_plus_real.bds", "i", 42.0);
    }

    @Test
    public void test00_plus_string() {
        // Binary operator: string addition (concatenation)
        runAndCheck(dir + "run_00_plus_string.bds", "i", "42 life universe, and everything");
    }

    @Test
    public void test00_minus_int() {
        // Binary operator: Integer subtraction
        runAndCheck(dir + "run_00_minus_int.bds", "i", 42L);
    }

    @Test
    public void test00_minus_real() {
        // Binary operator: Real subtraction
        runAndCheck(dir + "run_00_minus_real.bds", "i", 42.0);
    }

    @Test
    public void test00_div_int() {
        // Binary operator: Integer division
        runAndCheck(dir + "run_00_div_int.bds", "i", 3L);
    }

    @Test
    public void test00_div_real() {
        // Binary operator: Real division
        runAndCheck(dir + "run_00_div_real.bds", "i", 3.3333333333333335);
    }

    @Test
    public void test01() {
        // If condition
        runAndCheck(dir + "run_01.bds", "i", 2L);
    }

    @Test
    public void test02() {
        // If condition, else
        runAndCheck(dir + "run_02.bds", "i", 9L);
    }

    @Test
    public void test03() {
        // For loop
        runAndCheck(dir + "run_03.bds", "i", 10L);
    }

    @Test
    public void test03_2() {
        runAndCheck(dir + "run_03.bds", "j", 9L);
    }

    @Test
    public void test04() {
        // For loop, double post statement
        runAndCheck(dir + "run_04.bds", "i", 10L);
    }

    @Test
    public void test04_2() {
        //
        runAndCheck(dir + "run_04.bds", "j", 11L);
    }

    @Test
    public void test05() {
        // While loop
        runAndCheck(dir + "run_05.bds", "i", 10L);
    }

    @Test
    public void test06() {
        // Cast in Variable assignment: Real from bool and int
        runAndCheck(dir + "run_06.bds", "i", 1L);
    }

    @Test
    public void test06_2() {
        runAndCheck(dir + "run_06.bds", "r1", 1.0);
    }

    @Test
    public void test06_3() {
        runAndCheck(dir + "run_06.bds", "r2", 5.0);
    }

    @Test
    public void test07() {
        // Function definition
        runAndCheck(dir + "run_07.bds", "j", 4L);
    }

    @Test
    public void test08() {
        // Function definition
        runAndCheck(dir + "run_08.bds", "j", 5L);
    }

    @Test
    public void test09() {
        // Function with two return statements
        runAndCheck(dir + "run_09.bds", "i", 5L);
    }

    @Test
    public void test10() {
        // For loop with break statement
        runAndCheck(dir + "run_10.bds", "i", 4L);
    }

    @Test
    public void test11() {
        // For loop with continue
        runAndCheck(dir + "run_11.bds", "i", 6L);
    }

    @Test
    public void test12() {
        // Function: Variable inside a function shadowing a global variable
        runAndCheck(dir + "run_12.bds", "i", 1L);
    }

    @Test
    public void test13() {
        // Function: Variable inside a function shadowing a global variable
        runAndCheck(dir + "run_13.bds", "h", 8L);
    }

    @Test
    public void test14() {
        // String interpolation
        runAndCheck(dir + "run_14.bds", "s", "this is string interpolation: int i = 42 and str = \"hi\" and both hi42");
    }

    @Test
    public void test15_2() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li2", "[apple, orange, bannana]");
    }

    @Test
    public void test15_3() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li3", "[apple, orange, 1]");
    }

    @Test
    public void test15_4() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li4", "[apple, orange, 3.14]");
    }

    @Test
    public void test15_5() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li5", "[apple, orange, false]");
    }

    @Test
    public void test15_6() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li6", "[apple, orange, i=10hihihi]");
    }

    @Test
    public void test16() {
        // For loop iterating over a list
        runAndCheck(dir + "run_16.bds", "ss", "onetwothree");
    }

    @Test
    public void test17() {
        // String methods
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("s", " HEllo ");
        expectedValues.put("s1", "HEllo");
        expectedValues.put("s2", " hello ");
        expectedValues.put("s3", " HELLO ");
        expectedValues.put("s4", "Ello ");
        expectedValues.put("s5", "Ell");
        expectedValues.put("s6", " HEllo ");
        expectedValues.put("s7", "");
        expectedValues.put("s8", " HEzo ");
        expectedValues.put("i1", 7);
        expectedValues.put("i2", 3);
        expectedValues.put("i3", 4);
        expectedValues.put("i4", -1);
        expectedValues.put("i5", -1);
        expectedValues.put("ss", "hello");
        expectedValues.put("b1", true);
        expectedValues.put("b2", false);
        expectedValues.put("b3", true);
        expectedValues.put("b4", false);
        runAndCheck(dir + "run_17.bds", expectedValues);
    }

    @Test
    public void test18() {
        // For loop iterating over a list
        runAndCheck(dir + "run_18.bds", "s1", "three");
    }

    @Test
    public void test18_2() {
        // For loop iterating over a list
        runAndCheck(dir + "run_18.bds", "s3", "three");
    }

    @Test
    public void test19() {
        // List method head
        runAndCheck(dir + "run_19.bds", "h", "one");
    }

    @Test
    public void test20() {
        // List assign
        runAndCheck(dir + "run_20.bds", "h", "dos");
    }

    @Test
    public void test21() {
        // File readLines
        runAndCheck(dir + "run_21.bds", "l1", "line 1\nline 2\nline 3\n");
    }

    @Test
    public void test21_2() {
        // File: readLines
        runAndCheck(dir + "run_21.bds", "l2", "line 2");
    }

    @Test
    public void test22() {
        // Files: Dir
        runAndCheck(dir + "run_22.bds", "l2", "file_3.txt");
    }

    @Test
    public void test23() {
        // List: head, tail
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("ltsize", 2);
        expectedValues.put("lh", "one");
        expectedValues.put("lt0", "two");
        expectedValues.put("lt1", "three");
        runAndCheck(dir + "run_23.bds", expectedValues);
    }

    @Test
    public void test24() {
        // List: pop
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("p", 3);
        expectedValues.put("s", 3);
        expectedValues.put("l0", 1);
        expectedValues.put("l1", 2);
        expectedValues.put("l2", 4);
        runAndCheck(dir + "run_24.bds", expectedValues);
    }

    @Test
    public void test25() {
        // List: add
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l0", 1);
        expectedValues.put("l1", 2);
        expectedValues.put("l2", 3);
        runAndCheck(dir + "run_25.bds", expectedValues);
    }

    @Test
    public void test26() {
        // List: sort and element access
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l0", 1);
        expectedValues.put("l1", 2);
        expectedValues.put("l2", 3);
        expectedValues.put("l3", 4);
        expectedValues.put("l4", 5);
        expectedValues.put("s", 5);
        runAndCheck(dir + "run_26.bds", expectedValues);
    }

    @Test
    public void test28() {
        // List: add
        runAndCheck(dir + "run_28.bds", "events", "[done]");
    }

    @Test
    public void test29() {
        // Task: Schedule and wait
        runAndCheck(dir + "run_29.bds", "events", "[runnning, wait, done]");
    }

    @Test
    public void test31() {
        // Task: kill
        Timer timer = new Timer();
        timer.start();
        runAndCheck(1, dir + "run_31.bds", "events", "[runnning, kill, done]");
        Assert.assertTrue(timer.elapsed() < 1 * 1000); // We should finish in much less than 1 secs (the program waits 60secs)
    }

    @Test
    public void test32() {
        // Task: read task's stdout
        runAndCheck(dir + "run_32.bds", "out", "Hi\n");
    }

    @Test
    public void test33() {
        // Task: read stderr
        runAndCheck(dir + "run_33.bds", "err", "Hi\n");
    }

    @Test
    public void test34() {
        // Task: get exit code
        runAndCheck(dir + "run_34.bds", "exitStat", "0");
    }

    @Test
    public void test35() {
        // Task: 'canFail = true'
        runAndCheck(dir + "run_35.bds", "exitStat", "1");
    }

    @Test
    public void test37() {
        // Task: Can fail
        runAndCheck(dir + "run_37.bds", "s", "after");
    }

    @Test
    public void test38() {
        // String interpolation: Literal string with '$'
        runAndCheck(dir + "run_38.bds", "su", "$s world \\n");
    }

    @Test
    public void test39() {
        // Variables: Inheriting from environment
        String home = System.getenv("HOME");
        runAndCheck(dir + "run_39.bds", "home", home);
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
    public void test41() {
        // Global variable 'programName'
        runAndCheck(dir + "run_01.bds", "programName", "run_01.bds");
    }

    @Test
    public void test42() {
        // If statement: different syntaxes
        runAndCheck(dir + "run_42.bds", "i", 6L);
    }

    @Test
    public void test43() {
        // Task: Dependencies
        runAndCheck(1, dir + "run_43.bds", "finished", 0L);
    }

    @Test
    public void test44() {
        // Task: timeout
        runAndCheckExit(dir + "run_44.bds", 0);
    }

    @Test
    public void test45() {
        // Task: timeout
        runAndCheckExit(dir + "run_45.bds", 1);
    }

    @Test
    public void test46() {
        // Sys
        runAndCheck(dir + "run_46.bds", "i", 2L);
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
    public void test52() {
        // Hash assignment
        runAndCheck(dir + "run_52.bds", "hash", "{ one => 1 }");
    }

    @Test
    public void test53() {
        // For loop: Iterating over a hash
        runAndCheck(dir + "run_53.bds", "vals", "[bye, chau]");
    }

    @Test
    public void test54() {
        // Iterating over a hash
        runAndCheck(dir + "run_54.bds", "vals", "[hi, hola]");
    }

    @Test
    public void test55() {
        // Hash: hasValue, size, hasKey
        runAndCheck(dir + "run_55.bds", "hk1", "true");
        runAndCheck(dir + "run_55.bds", "hk2", "false");
        runAndCheck(dir + "run_55.bds", "hv1", "true");
        runAndCheck(dir + "run_55.bds", "hv2", "false");
        runAndCheck(dir + "run_55.bds", "hk3", "false");
    }

    @Test
    public void test56() {
        // For loop on hash
        runAndCheck(dir + "run_56.bds", "out", "Adios;Au revoir;Bye;");
        runAndCheck(dir + "run_56.bds", "str", "map = { Bonjour => Au revoir, Hello => Bye, Hola => Adios }");
    }

    @Test
    public void test57() {
        // Function: If with return
        runAndCheck(dir + "run_57.bds", "z", 0L);
    }

    @Test
    public void test58() {
        // Function: recursion
        runAndCheck(dir + "run_58.bds", "z", 0L);
    }

    @Test
    public void test59() {
        // Function: recursion
        runAndCheck(dir + "run_59.bds", "z", -1L);
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
    public void test63() {
        // List: Empty list literals
        runAndCheck(dir + "run_63.bds", "l", "[]");
    }

    @Test
    public void test64() {
        // Hash: Empty hash literal
        runAndCheck(dir + "run_64.bds", "m", "{}");
    }

    @Test
    public void test65() {
        // Variable initialization
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("bsfalse", false);
        expectedValues.put("bstrue", true);
        expectedValues.put("bifalse", false);
        expectedValues.put("bitrue", true);
        expectedValues.put("brfalse", false);
        expectedValues.put("brtrue", true);
        expectedValues.put("blfalse", false);
        expectedValues.put("bltrue", true);
        expectedValues.put("bmfalse", false);
        expectedValues.put("bmtrue", true);
        runAndCheck(dir + "run_65.bds", expectedValues);
    }

    @Test
    public void test66() {
        // If string as boolean empty
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("sif", "String 'hi' is NOT empty");
        expectedValues.put("lif", "List '[hi, bye]' is NOT empty");
        expectedValues.put("mif", "Map '{}' IS empty");
        runAndCheck(dir + "run_66.bds", expectedValues);
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
    public void test68() {
        // Sys: Multi line
        runAndCheck(dir + "run_68.bds", "out", "hi bye end\n");
    }

    @Test
    public void test69() {
        // File: removeExt
        runAndCheck(dir + "run_69.bds", "bgz", "path/to/file.txt");
    }

    @Test
    public void test69b() {
        // File: removeExt
        runAndCheck(dir + "run_69.bds", "btxt", "path/to/file");
    }

    @Test
    public void test70() {
        // For: Empty initialization clause
        runAndCheck(dir + "run_70.bds", "i", 10L);
    }

    @Test
    public void test71() {
        // For: Empty initialization clause, empty post (i.e. 'while')
        runAndCheck(dir + "run_71.bds", "i", 10L);
    }

    @Test
    public void test72() {
        // For: 'for(;;)'
        runAndCheck(dir + "run_72.bds", "i", 10L);
    }

    @Test
    public void test73() {
        // While loop
        runAndCheck(dir + "run_73.bds", "i", 10L);
    }

    @Test
    public void test74() {
        // While loop: Empty condition 'while()'
        runAndCheck(dir + "run_74.bds", "i", 10L);
    }

    @Test
    public void test75() {
        // sys: return commands outputs
        runAndCheck(dir + "run_75.bds", "ls", "EXEC\ntest/unit/run/run_75.bds\nDONE\n");
    }

    @Test
    public void test76() {
        // For loop: List: add
        runAndCheck(dir + "run_76.bds", "list", "[0, 2, 4, 6, 8, 10]");
    }

    @Test
    public void test77() {
        // For loop: List: add
        runAndCheck(dir + "run_77.bds", "list", "[10, 8, 6, 4, 2, 0]");
    }

    @Test
    public void test78() {
        // For loop: List: add
        runAndCheck(dir + "run_78.bds", "list", "[1, 2, 4, 8, 16, 32, 64]");
    }

    @Test
    public void test79() {
        // For loop: List: add
        runAndCheck(dir + "run_79.bds", "list", "[128, 64, 32, 16, 8, 4, 2, 1]");
    }

    @Test
    public void test80() {
        // Binary bit operator: '&'
        runAndCheck(dir + "run_80.bds", "j", 2L);
    }

    @Test
    public void test81() {
        // Binary bit operator: '|'
        runAndCheck(dir + "run_81.bds", "j", 19L);
    }

    @Test
    public void test82() {
        // Operator '+=': String
        runAndCheck(dir + "run_82.bds", "s", "Hi.Bye.");
    }

    @Test
    public void test83() {
        // Operator '+=': String, real
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("i", 3L);
        expectedValues.put("f", 5.85);
        expectedValues.put("s", "hibye");
        expectedValues.put("l", "[hi, bye, world]");
        runAndCheck(dir + "run_83.bds", expectedValues);
    }

    @Test
    public void test85() {
        // Global variables: K, M, G, T, P
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("m", 60L);
        expectedValues.put("h", 3600L);
        expectedValues.put("d", 86400L);
        expectedValues.put("oneK", 1024L);
        expectedValues.put("oneM", 1048576L);
        expectedValues.put("oneG", 1073741824L);
        expectedValues.put("oneT", 1099511627776L);
        expectedValues.put("oneP", 1125899906842624L);
        runAndCheck(dir + "run_85.bds", expectedValues);
    }

    @Test
    public void test88() {
        // Task: cpus, not enough resources
        runAndCheckStderr(dir + "run_88.bds", "Not enough resources to execute task:");
    }

    @Test
    public void test89() {
        // String: swapExt
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f", "file.txt");
        expectedValues.put("f2", "file.vcf");
        expectedValues.put("f3", "file.vcf");
        expectedValues.put("f4", "file.txt.vcf");
        runAndCheck(dir + "run_89.bds", expectedValues);
    }

    @Test
    public void test94() {
        // sys: failing command
        runAndCheckExit(dir + "run_94.bds", 1);
    }

    @Test
    public void test95() {
        // List: Concatenation
        runAndCheck(dir + "run_95.bds", "ll", "[zero, one, two, three, four, 5]");
    }

    @Test
    public void test96() {
        // Operator '+=': List
        runAndCheck(dir + "run_96.bds", "l", "[one, two, three, four]");
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
    public void test98() {
        // Operator '++' and '--' on list element
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("h1", "1");
        expectedValues.put("h2", "3");
        expectedValues.put("h3", "2");
        expectedValues.put("h4", "2");
        expectedValues.put("h5", "1");
        runAndCheck(dir + "run_98.bds", expectedValues);
    }

    @Test
    public void test99() {
        // sys with 'canFail'
        runAndCheck(dir + "run_99.bds", "finished", "true");
    }

}
