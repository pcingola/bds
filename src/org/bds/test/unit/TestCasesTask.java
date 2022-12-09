package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.test.TestCasesBase;
import org.bds.util.Timer;
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
    public void test88() {
        // Task: cpus, not enough resources
        runAndCheckStderr(dir + "run_88.bds", "Not enough resources to execute task:");
    }



}
