package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.test.TestCasesBase;
import org.bds.util.Timer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for 'switch' statements
 *
 * @author pcingola
 */
public class TestCasesSwitch extends TestCasesBase {

    public TestCasesSwitch() {
        dir = "test/unit/run/";
    }


    @Test
    public void test52() {
        // Switch statement, 'case' fall-through and 'default'
        compileOk(dir + "test52.bds");
    }

    @Test
    public void test53() {
        // Switch statement, 'default' not at the end
        compileOk(dir + "test53.bds");
    }

    @Test
    public void test54() {
        // Switch statement, Incorrect type in 'case' expression
        String errs = "ERROR [ file 'test/unit/lang/test54.bds', line 9 ] :	Switch expression and case expression types do not match (string vs int): case 7";
        compileErrors(dir + "test54.bds", errs);
    }

    @Test
    public void test55() {
        // Switch: Missing variable
        String errs = "ERROR [ file 'test/unit/lang/test55.bds', line 15 ] :	Symbol 'b' cannot be resolved";
        compileErrors(dir + "test55.bds", errs);
    }

    @Test
    public void test68() {
        // Switch: Empty statement error
        compileErrors(dir + "test68.bds", "Empty switch statement");
    }

}
