package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;

/**
 * Test cases loops ('for', 'while') and 'if/else'
 *
 * @author pcingola
 */
public class TestCasesLoopsIf extends TestCasesBase {

    public TestCasesLoopsIf() {
        dir = "test/unit/run/";
    }
    @Test
    public void test09() {
        // For loop
        compileOk(dir + "test09.bds");
    }

    @Test
    public void test14() {
        // For loop using an undefined variable in 'for' definition
        String errs = "ERROR [ file 'test/unit/lang/test14.bds', line 3 ] :	Symbol 'i' cannot be resolved\n"//
                + "ERROR [ file 'test/unit/lang/test14.bds', line 4 ] :	Symbol 'i' cannot be resolved\n";

        compileErrors(dir + "test14.bds", errs);
    }

    @Test
    public void test15() {
        // For loop using an undefined variable in loop block
        String errs = "ERROR [ file 'test/unit/lang/test15.bds', line 4 ] :	Symbol 'j' cannot be resolved\n";
        compileErrors(dir + "test15.bds", errs);
    }

    @Test
    public void test16_1() {
        // For loop with variables defined in loop block
        compileOk(dir + "test16.bds");
    }

    @Test
    public void test28() {
        // For loop using multiple pre and post statements
        compileOk(dir + "test28.bds");
    }

    @Test
    public void test29() {
        // For loops with non-bool condition
        String errs = "ERROR [ file 'test/unit/lang/test29.bds', line 3 ] :	For loop condition must be a bool expression\n";
        compileErrors(dir + "test29.bds", errs);
    }

    @Test
    public void test38() {
        // For loop: Iterating over a list
        String errs = "ERROR [ file 'test/unit/lang/test38.bds', line 6 ] :	Cannot cast string to int\n";
        compileErrors(dir + "test38.bds", errs);
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
        // For loop
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
    public void test16() {
        // For loop iterating over a list
        runAndCheck(dir + "run_16.bds", "ss", "onetwothree");
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
    public void test42() {
        // If statement: different syntaxes
        runAndCheck(dir + "run_42.bds", "i", 6L);
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
    public void test56() {
        // For loop on hash
        runAndCheck(dir + "run_56.bds", "out", "Adios;Au revoir;Bye;");
        runAndCheck(dir + "run_56.bds", "str", "map = { Bonjour => Au revoir, Hello => Bye, Hola => Adios }");
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
    public void test122_nestest_break_continue() {
        // For loops: Nested loops with 'break' and 'continue' statements
        runAndCheck(dir + "run_122.bds", "out", "5\t7");
    }

    @Test
    public void test244_concurrent_modification() {
        // For loop List:ConcurrentModificationException
        runAndCheckException(dir + "run_244.bds", "ConcurrentModificationException");
    }

    @Test
    public void test249_concurrent_modification_hash() {
        // For loop Map: ConcurrentModificationException
        runAndCheckException(dir + "run_249.bds", "ConcurrentModificationException");
    }



}
