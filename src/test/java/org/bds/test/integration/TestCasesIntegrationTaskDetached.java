package org.bds.test.integration;

import org.junit.Assert;
import org.bds.run.BdsThread;
import org.bds.test.BdsTest;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

/**
 * Test cases for detached tasks (executed on local computer)
 *
 * @author pcingola
 */
public class TestCasesIntegrationTaskDetached extends TestCasesBase {

    public TestCasesIntegrationTaskDetached() {
        dir = "test/integration/task_detached/";
    }

    /**
     * Execute a detached task: Local computer
     */
    @Test
    public void test01_DetachedLocalTask() {
        runAndCheckStdout(dir + "run_task_detached_01.bds", "Before\nAfter\nDone\n");
    }

    /**
     * Execute one detached task and one task with input dependencies (taskId)
     */
    @Test
    public void test02_DetachedWithDependentTask() {
        String outFile = "tmp.run_task_detached_02.txt";

        String catout = "Task 1: Start\n" + //
                "Task 2: Start\n" + //
                "Task 2: End\n";

        String outAfterWaitExpected = "Task 1: Start\n" + //
                "Task 2: Start\n" + //
                "Task 2: End\n" + //
                "Task 1: End\n";

        runAndCheck(dir + "run_task_detached_02.bds", "catout", catout);

        // Wait
        sleep(3);

        String outAfterWait = Gpr.readFile(outFile);
        Assert.assertEquals(outAfterWaitExpected, outAfterWait);
    }

    /**
     * Execute two detached task + one taks
     */
    @Test
    public void test03_TwoDetachedOneDependent() {
        String outFile = dir + "tmp.run_task_detached_03.txt";

        String catout = "Task 1: Start\n" + //
                "Task 2: Start\n" + //
                "Task 3\n";

        String outAfterWaitExpected = "Task 1: Start\n" + //
                "Task 2: Start\n" + //
                "Task 3\n" + //
                "Task 1: End\n" + //
                "Task 2: End\n";

        runAndCheck(dir + "run_task_detached_03.bds", "catout", catout);

        sleep(3);

        String outAfterWait = Gpr.readFile(outFile);
        Assert.assertEquals(outAfterWaitExpected, outAfterWait);
    }

    /**
     * Execute two detached task with output dependencies (files)
     */
    @Test
    public void test04_CheckDetachedOutputError() {
        String expectedExceptionMessage = "Detached task output files cannot be used as dependencies";
        BdsTest bdsTest = runAndCheckExit(dir + "run_task_detached_04.bds", BdsThread.EXITCODE_FATAL_ERROR);

        // Check that the exception causing the 'exit=1' code is the one we expected
        Throwable javaException = bdsTest.bds.getBdsRun().getBdsThread().getVm().getJavaException();
        Assert.assertTrue(javaException.getMessage().startsWith(expectedExceptionMessage));
    }
}
