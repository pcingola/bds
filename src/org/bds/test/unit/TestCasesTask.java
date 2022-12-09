package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for 'task' statements
 *
 * @author pcingola
 */
public class TestCasesTask extends TestCasesBase {

    public TestCasesTask() {
        dir = "test/unit/run/";
    }


    @Test
    public void test32() {
        // Task definition
        compileOk(dir + "test32.bds");
    }

    @Test
    public void test33() {
        // Task definition: Improper tasks
        compileOk(dir + "test33.bds");
    }

    @Test
    public void test41() {
        // Task definition syntax
        compileOk(dir + "test41.bds");
    }
    @Test
    public void test49() {
        // Task definition: Using two lines without braces
        String errs = "ERROR [ file 'test/unit/lang/test49.bds', line 4 ] :\tTask has empty statement";
        compileErrors(dir + "test49.bds", errs);
    }
    @Test
    public void test59() {
        // Task: Using a method name instead of a variable
        compileErrors(dir + "test59.bds", "Expression should be string or string[], got '(A) -> string'");
    }



}
