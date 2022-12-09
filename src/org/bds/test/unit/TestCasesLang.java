package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

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
    public void test60() {
        // Include statements: Multiple includes. Include order should not affect compilation
        compileOk(dir + "test60.bds");
    }
}
