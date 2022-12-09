package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for 'sys' statements
 *
 * @author pcingola
 */
public class TestCasesSys extends TestCasesBase {

    public TestCasesSys() {
        dir = "test/unit/run/";
    }

    @Test
    public void test26() {
        // sys statements, parsing escaped characters and multi-line sys statements
        compileOk(dir + "test26.bds");
    }
    @Test
    public void test46() {
        // Sys
        runAndCheck(dir + "run_46.bds", "i", 2L);
    }

    @Test
    public void test68() {
        // Sys: Multi line
        runAndCheck(dir + "run_68.bds", "out", "hi bye end\n");
    }
    @Test
    public void test75() {
        // sys: return commands outputs
        runAndCheck(dir + "run_75.bds", "ls", "EXEC\ntest/unit/run/run_75.bds\nDONE\n");
    }

    @Test
    public void test94() {
        // sys: failing command
        runAndCheckExit(dir + "run_94.bds", 1);
    }
    @Test
    public void test99() {
        // sys with 'canFail'
        runAndCheck(dir + "run_99.bds", "finished", "true");
    }


}
