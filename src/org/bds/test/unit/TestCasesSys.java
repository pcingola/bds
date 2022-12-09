package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for 'sys' statements
 *
 * @author pcingola
 */
public class TestCasesSys extends TestCasesBase {

    public TestCasesSys() {
        dir = "test/unit/sys/";
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
        runAndCheck(dir + "run_75.bds", "ls", "EXEC\ntest/unit/sys/run_75.bds\nDONE\n");
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
    @Test
    public void test123LiteralsSys() {
        // String literals, interpolation and escaped characters
        String output = "" //
                // Note: This result may change if we use a different sysShell in bds.config
                + "sys                |\t|\n" //
                + "sys                |\t|    variable:Hello\n" //
                + "sys                |\\t|   variable:Hello\n" //
                ;

        runAndCheckStdout(dir + "run_123_literals_sys.bds", output);
    }
    @Test
    public void test129ChdirSys() {
        // sys: chDir
        String out = runAndReturnStdout(dir + "run_129.bds");
        Assert.assertTrue(out.contains("FILE_01\n"));
        Assert.assertTrue(out.contains("FILE_02\n"));
    }

    @Test
    public void test130ChdirTask() {
        // sys & chdir
        String out = runAndReturnStdout(dir + "run_130.bds");
        Assert.assertTrue(out.contains("FILE_01\n"));
        Assert.assertTrue(out.contains("FILE_02\n"));
    }

    @Test
    public void test157MultilineSys() {
        // Sys: multi-line statements
        runAndCheck(dir + "run_157.bds", "o", "hello world\n");
    }
}
