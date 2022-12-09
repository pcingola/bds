package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases loops: 'for', 'while'
 *
 * @author pcingola
 */
public class TestCasesLoops extends TestCasesBase {

    public TestCasesLoops() {
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
    public void test16() {
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



}
