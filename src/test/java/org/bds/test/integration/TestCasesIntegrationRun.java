package org.bds.test.integration;

import junit.framework.Assert;
import org.bds.run.BdsThread;
import org.bds.test.TestCasesBase;
import org.bds.util.Timer;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Test cases that require BDS code execution and check results.
 * These "integration" test usually require a bit longer execution times than the unit tests.
 * <p>
 * Note: These test cases requires that the BDS code is correctly parsed, compiled and executes.
 *
 * @author pcingola
 */
public class TestCasesIntegrationRun extends TestCasesBase {

    public TestCasesIntegrationRun() {
        dir = dir + "test/integration/run/";
    }

    @Test
    public void test104() {
        runAndCheck(dir + "run_104.bds", "isRun", "true");
    }

    @Test
    public void test105() {
        runAndCheck(dir + "run_105.bds", "isRun", "false");
    }

    @Test
    public void test114_parallel_function_task_calls() {
        String stdout = runAndReturnStdout(dir + "run_114.bds");

        Set<String> linesPar = new HashSet<>();
        for (String line : stdout.split("\n"))
            if (line.startsWith("TASK")) linesPar.add(line);

        // Check
        Assert.assertTrue("There should be 5 tasks", linesPar.size() == 5);
    }

    @Test
    public void test115_task_dependency_using_taskId() {
        String stdout = runAndReturnStdout(dir + "run_115.bds");
        Assert.assertEquals("Hi 1\nBye 1\nHi 2\nBye 2\n", stdout);
    }

    @Test
    public void test117_serial_parallel_tasks() {
        String expectedStdout = "Iter 1, Task 1: End\n" //
                + "Iter 1, Task 2: Start\n" //
                + "Iter 1, Task 2: End\n" //
                + "Iter 5, Task 1: End\n" //
                + "Iter 5, Task 2: Start\n" //
                + "Iter 5, Task 2: End\n" //
                ;

        String stdout = runAndReturnStdout(dir + "run_117.bds");

        if (stdout.indexOf(expectedStdout) < 0) {
            String msg = "Cannot find desired output:\n" //
                    + "---------- Expected output ----------\n" //
                    + expectedStdout //
                    + "-------------- STDOUT --------------\n" //
                    + stdout //
                    ;
            System.err.println(msg);
            throw new RuntimeException(msg);
        }
    }

    @Test
    public void test119_task_dependency() {

        // Delete input file
        String inFile = "tmp_in.txt";
        (new File(inFile)).delete();

        String expectedStdout1 = "Creating tmp_in.txt\n" //
                + "Running task\n" //
                + "Creating tmp_out.txt\n" //
                + "Done\n" //"
                ;

        String expectedStdout2 = "Running task\n" //
                + "Done\n" //"
                ;

        if (verbose) System.out.println("First run:");
        runAndCheckStdout(dir + "run_119.bds", expectedStdout1);

        if (verbose) System.out.println("\n\nSecond run:");
        runAndCheckStdout(dir + "run_119.bds", expectedStdout2);
    }

    @Test
    public void test123_literals_task() {

        String output = "" //
                // Note: This result may change if we use a different taskShell in bds.config
                + "task               |\t|\n" //
                + "task               |\t|    variable:Hello\n" //
                + "task               |\\t|   variable:Hello\n" //
                ;

        runAndCheckStdout(dir + "run_123_literals_task.bds", output);
    }

    @Test
    public void test27() {
        Timer timer = new Timer();
        timer.start();
        runAndCheck(1, dir + "run_27.bds", "timeout", "1"); // 2 seconds timeout
        Assert.assertTrue(timer.elapsed() < 3 * 1000); // We should finish in less than 3 secs (the program waits 60secs)
    }

    @Test
    public void test30() {
        runAndCheck(dir + "run_30.bds", "events", "[runnning, wait, done]");
    }

    @Test
    public void test36() {
        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "run_36.bds", "s", "before");
    }

    @Test
    public void test84() {
        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "run_84.bds", "taskOk", "false");
    }

    @Test
    public void test85() {
        runAndCheckStderr(dir + "run_84.bds", "ERROR_TIMEOUT");
    }

    @Test
    public void test87() {
        runAndCheck(dir + "run_87.bds", "cpus", "1");
    }

    @Test
    public void test90() {
        runAndCheck(dir + "run_90.bds", "ok", "true");
    }

    @Test
    public void test91() {
        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "run_91.bds", "ok", "false");
    }

    @Test
    public void test92() {
        runAndCheck(dir + "run_92.bds", "outs", "TASK 1\nTASK 2\n");
    }

    @Test
    public void test93() {
        runAndCheck(dir + "run_93.bds", "outs", "TASK 1\nTASK 2\n");
    }

}
