package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for function definitions, invocations, etc.
 *
 * @author pcingola
 */
public class TestCasesFunctions extends TestCasesBase {

    public TestCasesFunctions() {
        dir = "test/unit/run/";
    }

    @Test
    public void test17() {
        // Function definition and function call
        compileOk(dir + "test17.bds");
    }

    @Test
    public void test18() {
        // Function definition with default value
        compileOk(dir + "test18.bds");
    }

    @Test
    public void test22() {
        // Function definition within function
        compileOk(dir + "test22.bds");
    }

    @Test
    public void test30() {
        // Function definition: Wrong casting in 'return' statement
        String errs = "ERROR [ file 'test/unit/lang/test30.bds', line 4 ] :	Cannot cast real to int\n";
        compileErrors(dir + "test30.bds", errs);
    }

    @Test
    public void test31() {
        // Function definition: Missing return statement
        String errs = "ERROR [ file 'test/unit/lang/test31.bds', line 4 ] :	Function has no return statement\n";
        compileErrors(dir + "test31.bds", errs);
    }

    @Test
    public void test34() {
        // Function call: Undefined function
        String errs = "ERROR [ file 'test/unit/lang/test34.bds', line 5 ] :	Function f(int) cannot be resolved\n";
        compileErrors(dir + "test34.bds", errs);
    }

    @Test
    public void test47() {
        // Function has the same name as a variable
        String errs = "ERROR [ file 'test/unit/lang/test47.bds', line 5 ] :	Duplicate local name 'gsea'";
        compileErrors(dir + "test47.bds", errs);
    }

    @Test
    public void test63() {
        // Function definition: Duplicate name for variable and function
        compileErrors(dir + "test63.bds", "ERROR [ file 'test/unit/lang/test63.bds', line 5 ] :	Duplicate local name 'zzz'");
    }

    @Test
    public void test64() {
        // Function definition: Duplicate function names
        compileErrors(dir + "test64.bds", "Duplicate function 'zzz() -> void'");
    }
    @Test
    public void test70() {
        // Function: Return different type of array (class)
        compileErrors(dir + "test70.bds", "Cannot cast A[] to B[]");
    }


}
