package org.bds.test.unit;

import org.junit.Assert;
import org.bds.run.BdsThread;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.bds.util.Timer;
import org.junit.Test;

/**
 * Test cases for 'task' statements
 *
 * @author pcingola
 */
public class TestCasesTask extends TestCasesBase {

    public TestCasesTask() {
        dir = "test/unit/task/";
    }

    @Test
    public void testTask01() {
        // Task definition
        compileOk(dir + "test32.bds");
    }

    @Test
    public void testTask02() {
        // Task definition: Improper tasks
        compileOk(dir + "test33.bds");
    }

    @Test
    public void testTask03() {
        // Task definition syntax
        compileOk(dir + "test41.bds");
    }

    @Test
    public void testTask04() {
        // Task definition: Using two lines without braces
        String errs = "ERROR [ file 'test/unit/task/test49.bds', line 4 ] :\tTask has empty statement";
        compileErrors(dir + "test49.bds", errs);
    }

    @Test
    public void testTask05() {
        // Task: Using a method name instead of a variable
        compileErrors(dir + "test59.bds", "Expression should be string or string[], got '(A) -> string'");
    }

    @Test
    public void testTask06() {
        // Task: Schedule and wait
        runAndCheck(dir + "run_29.bds", "events", "[runnning, wait, done]");
    }

    @Test
    public void testTask07() {
        // Task: kill
        Timer timer = new Timer();
        timer.start();
        runAndCheck(1, dir + "run_31.bds", "events", "[runnning, kill, done]");
        Assert.assertTrue(timer.elapsed() < 1 * 1000); // We should finish in much less than 1 secs (the program waits 60secs)
    }

    @Test
    public void testTask08() {
        // Task: read task's stdout
        runAndCheck(dir + "run_32.bds", "out", "Hi\n");
    }

    @Test
    public void testTask09() {
        // Task: read stderr
        runAndCheck(dir + "run_33.bds", "err", "Hi\n");
    }

    @Test
    public void testTask10() {
        // Task: get exit code
        runAndCheck(dir + "run_34.bds", "exitStat", "0");
    }

    @Test
    public void testTask11() {
        // Task: 'canFail = true'
        runAndCheck(dir + "run_35.bds", "exitStat", "1");
    }

    @Test
    public void testTask12() {
        // Task: Can fail
        runAndCheck(dir + "run_37.bds", "s", "after");
    }

    @Test
    public void testTask13() {
        // Task: Checking that output files have zero length (error condition)
        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "run_43.bds", "finished", 0L);
    }

    @Test
    public void testTask14() {
        // Task: timeout
        runAndCheckExit(dir + "run_44.bds", 0);
    }

    @Test
    public void testTask15() {
        // Task: timeout
        runAndCheckExit(dir + "run_45.bds", 1);
    }

    @Test
    public void testTask16() {
        // Task: cpus, not enough resources
        runAndCheckStderr(dir + "run_88.bds", "Not enough resources to execute task:");
    }

    @Test
    public void testTask17() {
        // Task: 'allowEmpty'. Checking that output files have zero length (override error condition)
        runAndCheck(dir + "run_110.bds", "runOk", "true");
    }

    @Test
    public void testTask18DependencyUsingPath() {
        // Task: 'dep' and 'goal'
        runAndCheckExit(dir + "run_118.bds", 0);
    }

    @Test
    public void testTask19QuietMode() {
        // Task quiet mode: do not show task's output to STDOUT/STDERR
        String output = "print 0\n" //
                + "print 1\n" //
                + "print 2\n" //
                + "print 3\n" //
                + "print 4\n" //
                + "print 5\n" //
                + "print 6\n" //
                + "print 7\n" //
                + "print 8\n" //
                + "print 9\n" //
                ;

        // Run and capture stdout
        String[] args = {"-quiet"};
        String stdout = runAndReturnStdout(dir + "run_124.bds", args);
        if (verbose) System.err.println("STDOUT: " + stdout);

        // Check that sys and task outputs are not there
        Assert.assertTrue("Print output should be in STDOUT", stdout.contains(output));
        Assert.assertTrue("Task output should NOT be in STDOUT", !stdout.contains("task"));
        Assert.assertTrue("Sys output should NOT be in STDOUT", !stdout.contains("sys"));
    }

    /**
     * Task dependent on output from a scheduled task
     */
    @Test
    public void testTask20TaskDependencyScheduled() {
        // Task: Nested loops of tasks (mutiple dependencies)
        String expectedOutput = "IN: " + Gpr.HOME + "/zzz/in.txt\n" //
                + "OUT: " + Gpr.HOME + "/zzz/out.txt\n" //
                + "OUT_0: " + Gpr.HOME + "/zzz/out_0.txt\n" //
                + "    OUT_0_0: " + Gpr.HOME + "/zzz/out_0_0.txt\n" //
                + "    OUT_0_1: " + Gpr.HOME + "/zzz/out_0_1.txt\n" //
                + "    OUT_0_2: " + Gpr.HOME + "/zzz/out_0_2.txt\n" //
                + "OUT_1: " + Gpr.HOME + "/zzz/out_1.txt\n" //
                + "    OUT_1_0: " + Gpr.HOME + "/zzz/out_1_0.txt\n" //
                + "    OUT_1_1: " + Gpr.HOME + "/zzz/out_1_1.txt\n" //
                + "    OUT_1_2: " + Gpr.HOME + "/zzz/out_1_2.txt\n" //
                + "OUT_2: " + Gpr.HOME + "/zzz/out_2.txt\n" //
                + "    OUT_2_0: " + Gpr.HOME + "/zzz/out_2_0.txt\n" //
                + "    OUT_2_1: " + Gpr.HOME + "/zzz/out_2_1.txt\n" //
                + "    OUT_2_2: " + Gpr.HOME + "/zzz/out_2_2.txt\n" //
                ;

        String stdout = runAndReturnStdout(dir + "run_126.bds");
        if (verbose) {
            System.err.println("STDOUT:" //
                    + "\n----------------------------------------\n" //
                    + stdout //
                    + "\n----------------------------------------" //
            );
        }

        // Check that task output lines
        for (String out : expectedOutput.split("\n")) {
            Assert.assertTrue("Expected output line not found: '" + out + "'", stdout.contains(out));
        }
    }

    @Test
    public void testTask21InterpolateVariableWithUnderscores() {
        // Task and sys combined
        String output = "bwa parameters\n" //
                + "bwa parameters\n" //
                + "bwa parameters\n" //
                + "bwa parameters\n" //
                ;

        runAndCheckStdout(dir + "run_127.bds", output);
    }

    @Test
    public void testTask22TaskLocalVariables() {
        // Task: Defining a variable within the task's scope
        runAndCheckStdout(dir + "run_128.bds", "TEST\n");
    }

    @Test
    public void testTask23ChdirTask() {
        // Task & chdir
        String out = runAndReturnStdout(dir + "run_130.bds");
        Assert.assertTrue(out.contains("FILE_01\n"));
        Assert.assertTrue(out.contains("FILE_02\n"));
    }

    @Test
    public void testTask24TaskName() {
        // Task taskName: Make sure taskId contains 'taskName' parameter
        String out = runAndReturnStdout(dir + "run_132.bds");
        Assert.assertTrue(out.contains("run_132.mytask"));
    }

    @Test
    public void testTask25TaskNameUnsafe() {
        // Task: taskName
        //   - Make sure taskId contains 'taskName' parameter
        //   - In this test 'taskName' is not safe to be used with as file name, so it has to be sanitized
        String out = runAndReturnStdout(dir + "run_133.bds");
        Assert.assertTrue(out.contains("run_133.mytask_unsafe_with_spaces"));
    }

    @Test
    public void testTask26DollarSignInTask() {
        // Task: Variable interpolation and literals
        //
        // We want to execute an inline perl script within a task
        // E.g.:
        //     task perl -e 'use English; print "PID: \$PID\n";'
        //
        // Here $PID is a perl variable and should not be interpreted
        // by bds. We need a way to escape such variables.
        String bdsFile = dir + "run_144.bds";

        String stdout = runAndReturnStdout(bdsFile);

        // Parse STDOUT
        String[] lines = stdout.split("\n");
        Assert.assertEquals("No lines found?", 3, lines.length);

        for (String line : lines) {
            String[] fields = line.split(":");
            Assert.assertEquals("Cannot parse line:\n" + line, 2, fields.length);

            int positiveNumber = Gpr.parseIntSafe(fields[1]);
            Assert.assertTrue("Positive number expected: '" + fields[1] + "'", positiveNumber > 0);
        }
    }

    @Test
    public void testTask27TaskPrelude() {
        // Task: Prelude in Config file
        String[] args = {"-c", dir + "run159_prelude_task.config"};
        runAndCheckStdout(dir + "run_159.bds", "=== TASK PRELUDE local ===", args, false);
    }

    @Test
    public void testTask28WaitThrowsException() {
        // Task error should produce a 'WaitException' instead of an error
        runAndCheckException(dir + "task_28.bds", "WaitException", "Error in wait statement, file test/unit/task/task_28.bds, line 5");
    }

    @Test
    public void testTask29WaitThrowsException() {
        // Task error: When the task has an error after the end of the program (i.e. in
        // the implicit 'wait' statement), there is no exception, just an error
        runAndCheckError(dir + "task_29.bds", "Error waiting pending tasks");
    }

    @Test
    public void testTask30WaitThrowsException() {
        // Task error should produce a 'WaitException' instead of an error. Dependent tasks
        runAndCheckException(dir + "task_30.bds", "WaitException", "Error in wait statement, file test/unit/task/task_30.bds, line 21");
    }

    @Test
    public void testTask31WaitThrowsExceptionCatch() {
        // Task error with try catch does not produce an exception
        runAndCheck(dir + "task_31.bds", "captured", "true");
    }

    @Test
    public void testTask32WaitThrowsExceptionCatch() {
        // Task error with try/catch does NOT CATCH the exception, because
        // the "implicit wait at the end of the program" is executed AFTER the
        // program ends (i.e. outside the try/catch clause).
        runAndCheck(BdsThread.EXITCODE_ERROR, dir + "task_32.bds", "captured", "false");
    }

    @Test
    public void testTask33WaitThrowsExceptionCatch() {
        // Task error with try/catch does not produce an exception. Dependent task
        runAndCheck(dir + "task_33.bds", "captured", "true");
    }

    @Test
    public void testTask34WaitThrowsExceptionCatch() {
        // Task error with try/catch captures the exception, but the exit code
        // is '1', because it is not forced by an 'exit' statement
        runAndCheck(BdsThread.EXITCODE_ERROR, dir + "task_34.bds", "captured", "true");
    }
}
