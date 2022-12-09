package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for Class definitions, methods, etc.
 *
 * @author pcingola
 */
public class TestCasesClass extends TestCasesBase {

    public TestCasesClass() {
        dir = "test/unit/run/";
    }

    @Test
    public void test56() {
        // Class: 'new' operator missing class definition
        compileErrors(dir + "test56.bds", "Cannot find class 'A'");
    }

    @Test
    public void test57() {
        // Class: 'new' operator, incorrect class
        compileErrors(dir + "test57.bds", "Cannot cast A to B");
    }

    @Test
    public void test58() {
        // Class definition: Missing variable in methods
        compileErrors(dir + "test58.bds", "Symbol 'out' cannot be resolved");
    }


}