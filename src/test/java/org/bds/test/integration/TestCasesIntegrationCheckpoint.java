package org.bds.test.integration;

import org.bds.Bds;
import org.bds.run.BdsThread;
import org.bds.run.BdsThreads;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Running bds code: Checkpoint and recovery (local, long running tests)
 */
public class TestCasesIntegrationCheckpoint extends TestCasesBase {

    public TestCasesIntegrationCheckpoint() {
        dir = "test/integration/checkpoint/";
    }

    /**
     * Test a checkpoint within a for-loop, reload checkpoint and continue execution
     */
    @Test
    public void test01() {
        runAndCheckpoint(dir + "checkpoint_01.bds", null, "i", "10");
    }

    /**
     * Test a checkpoint inside a function, reload checkpoint and continue execution
     */
    @Test
    public void test02() {
        runAndCheckpoint(dir + "checkpoint_02.bds", null, "l", "15");
    }

    /**
     * Test checkpoint recovery before declaring a variable
     */
    @Test
    public void test03() {
        runAndCheckpoint(dir + "checkpoint_03.bds", null, "s2", "After checkpoint 42");
    }

    /**
     * Test list and hashed values after recovering checkpoint
     */
    @Test
    public void test04() {
        runAndCheckpoint(dir + "checkpoint_04.bds", null, "s", "one\teins");
    }

    /**
     * Test list and hashed values after recovering checkpoint
     */
    @Test
    public void test04_2() {
        runAndCheckpoint(dir + "checkpoint_04.bds", null, "i", "3");
    }

    /**
     * Test list and hashed values after recovering checkpoint
     */
    @Test
    public void test04_3() {
        runAndCheckpoint(dir + "checkpoint_04.bds", null, "ss", "one\ttwo");
    }

    /**
     * Test checkpoint recovers list and string methods
     */
    @Test
    public void test05() {
        runAndCheckpoint(dir + "checkpoint_05.bds", null, "l0", "ONE");
    }

    /**
     * Test checkpoint when a task fails
     * <p>
     * How this test works:
     * 1) Try to delete a file that doesn't exits. Task fails, checkpoint is created and program finishes
     * 2) createFile is run: This creates the file to be deleted
     * 3) Checkpoint recovery, the task is re-executed. This time the file exists, so it runs OK. Variable 'b' is set to true
     */
    @Test
    public void test06() {
        final String fileToDelete = dir + "checkpoint_06.tmp";

        Runnable createFile = new Runnable() {

            @Override
            public void run() {
                // Create the file
                if (verbose) Gpr.debug("Creating file: '" + fileToDelete + "'");
                Gpr.toFile(fileToDelete, "Hello");
            }
        };

        // Make sure that the file doesn't exists
        (new File(fileToDelete)).delete();

        // Run test
        runAndCheckpoint(dir + "checkpoint_06.bds" //
                , "checkpoint_06.bds.chp" //
                , "b", "true" //
                , createFile);
    }

    /**
     * Test a checkpoint in a for-loop iterating over a list
     */
    @Test
    public void test07() {
        runAndCheckpoint(dir + "checkpoint_07.bds", null, "sloop", "three");
    }

    /**
     * Test a checkpoint recovering after 'dep' is capable of
     * executing 'goal' (which is defined after the checkpoint)
     */
    @Test
    public void test08() {
        // Remove old entries
        String prefix = dir + "checkpoint_08";
        File txt = new File(prefix + ".txt");
        final File csv = new File(prefix + ".csv");
        final File xml = new File(prefix + ".xml");
        txt.delete();
        csv.delete();
        xml.delete();

        // Create file
        Gpr.toFile(prefix + ".txt", "TEST");

        // Run this code before checkpoint recovery
        Runnable runBeforeRecovery = new Runnable() {

            @Override
            public void run() {
                // Create the file
                if (verbose) Gpr.debug("Deleting files: " + csv + " and " + xml);
                csv.delete();
                xml.delete();
            }
        };

        // Run pipeline and test checkpoint
        runAndCheckpoint(prefix + ".bds", prefix + ".chp", "num", "2", runBeforeRecovery);
    }

    /**
     * Test a checkpoint recovering after 'dep' is capable of
     * executing 'goal' (which is defined after the checkpoint)
     */
    @Test
    public void test09() {
        // Remove old entries
        String prefix = dir + "checkpoint_09";
        File txt = new File(prefix + ".txt");
        final File csv = new File(prefix + ".csv");
        final File xml = new File(prefix + ".xml");
        txt.delete();
        csv.delete();
        xml.delete();

        // Create file
        Gpr.toFile(prefix + ".txt", "TEST");

        // Run this code before checkpoint recovery
        Runnable runBeforeRecovery = new Runnable() {

            @Override
            public void run() {
                // Create the file
                if (verbose) Gpr.debug("Deleting file: " + csv);
                csv.delete();
            }
        };

        // Run pipeline and test checkpoint
        runAndCheckpoint(prefix + ".bds", prefix + ".chp", "num", "0", runBeforeRecovery);
    }

    /**
     * Test a 'par' function is recovered after a checkpoint
     */
    @Test
    public void test10() {
        // Run pipeline and test checkpoint
        runAndCheckpoint(dir + "checkpoint_10.bds", dir + "checkpoint_10.chp", "sumMain", "55");
    }

    /**
     * Test a 'par' function is recovered after a checkpoint
     */
    @Test
    public void test11() {
        // Run pipeline and test checkpoint
        runAndCheckpoint(dir + "checkpoint_11.bds", dir + "checkpoint_11.chp", "sumPar", "110");
    }

    /**
     * Test checkpoint with an "empty" include
     */
    @Test
    public void test12_serializationOfEmptyIncludes() {
        runAndCheckpoint(dir + "checkpoint_12.bds", dir + "checkpoint_12.chp", "ok", "true");
    }

    /**
     * Test checkpoint with functions having no arguments
     */
    @Test
    public void test13_checkPoint_function_with_empty_Args() {
        runAndCheckpoint(dir + "checkpoint_13.bds", dir + "checkpoint_13.chp", "ok", "true");
    }

    /**
     * Test checkpoint calling function with non-empty arguments
     */
    @Test
    public void test14_serialize_method_call_args() {
        runAndCheckpoint(dir + "checkpoint_14.bds", dir + "checkpoint_14.chp", "ok", "true");
    }

    /**
     * Test checkpoint a 'par' thread while executing
     */
    @Test
    public void test15_checkpoint_par_function_call() {
        runAndCheckpoint(dir + "checkpoint_15.bds", dir + "checkpoint_15.chp", "ok", "true");
    }

    /**
     * Test checkpoint within a recursive function call
     */
    @Test
    public void test16_checkpoint_recursive() {
        runAndCheckpoint(dir + "checkpoint_16.bds", dir + "checkpoint_16.chp", "fn", "120");
    }

    /**
     * Test checkpoint within a function called as an index of a list
     * i.e.: `li[ f(12) ]`, where `f()` has a checkpoint
     */
    @Test
    public void test17_checkpoint_listIndex() {
        runAndCheckpoint(dir + "checkpoint_17.bds", dir + "checkpoint_17.chp", "res", "19");
    }

    /**
     * Test checkpoint within a function called as argument of another function
     * i.e.: `f1( f2(10) )`, where `f2()` has a checkpoint
     */
    @Test
    public void test18_checkpoint_listIndex() {
        runAndCheckpoint(dir + "checkpoint_18.bds", dir + "checkpoint_18.chp", "res", "34");
    }

    /**
     * Test checkpoint's scopes are corectly recovered (and not nested within the recovering global scope)
     */
    @Test
    public void test19_scope_global_global() {
        // Run pipeline and test checkpoint
        Bds bds = runAndCheckpoint(dir + "checkpoint_19.bds", dir + "checkpoint_19.chp", "ok", "true");

        // Get scope names
        BdsThread bdsThread = bds.getBdsRun().getBdsThread();
        String scopeNames = bdsThread.getScope().toStringScopeNames();

        // Count number of global scopes
        int count = 0;
        for (String line : scopeNames.split("\n")) {
            if (line.contains("Global")) count++;
        }

        Assert.assertTrue("There should be one and only one 'Global' scope (count = " + count + ")", count == 1);
    }

    /**
     * Test checkpoint created within `par` function call
     */
    @Test
    public void test20_checkpoint_after_main_thread_finished_execution() {
        runAndCheckpoint(dir + "checkpoint_20.bds", dir + "checkpoint_20.chp", null, null);
    }

    /**
     * Test checkpoint created within `par` function call
     */
    @Test
    public void test21_checkpoint_after_par_thread_finished_execution() {
        runAndCheckpoint(dir + "checkpoint_21.bds", dir + "checkpoint_21.chp", null, null);
    }

    /**
     * Test checkpoint created within nested `par` calls
     * i.e.: `par zzz(1)` calling `par zzz(2)` which has a checkpoint
     */
    public void test22_par_par_par() {
        runAndCheckpoint(dir + "checkpoint_22.bds", dir + "checkpoint_22.chp", "luae", "42");
    }

    /**
     * Test checkpoint bdsThread structures when recovering from nested `par` function calls with checkpoints
     */
    @Test
    public void test23_thread_structure() {

        try {
            // Run pipeline and test checkpoint
            BdsThreads.doNotRemoveThreads = true;
            Bds bds = runAndCheckpoint(dir + "checkpoint_23.bds", dir + "checkpoint_23.chp", "luae", "42");

            // Get scope names
            BdsThread bdsThread = bds.getBdsRun().getBdsThread();

            // All threads (including root thread)
            Assert.assertEquals(4, bdsThread.getBdsThreadsAll().size());

            // First 'level'
            List<BdsThread> bdsThreadsL1 = bdsThread.getBdsThreads();
            if (verbose)
                Gpr.debug("Root thread '" + bdsThread.getBdsThreadId() + "', number of child threads: " + bdsThreadsL1.size());
            Assert.assertEquals(1, bdsThreadsL1.size());

            // Second 'level'
            for (BdsThread bdsthl1 : bdsThreadsL1) {
                List<BdsThread> bdsThreadsL2 = bdsthl1.getBdsThreads();
                if (verbose)
                    Gpr.debug("Level 1 thread '" + bdsthl1.getBdsThreadId() + "', number of child threads: " + bdsThreadsL2.size());
                Assert.assertEquals(2, bdsThreadsL2.size());

                // Third 'level'
                for (BdsThread bdsthl2 : bdsThreadsL2) {
                    List<BdsThread> bdsThreadsL3 = bdsthl2.getBdsThreads();
                    if (verbose)
                        Gpr.debug("Level 2 thread '" + bdsthl2.getBdsThreadId() + "', number of child threads: " + bdsThreadsL3.size());
                    Assert.assertEquals(0, bdsThreadsL3.size());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        } finally {
            BdsThreads.doNotRemoveThreads = false;
        }
    }

    /**
     * Test checkpoint created within a `switch` statement calling a function
     */
    @Test
    public void test24_switch() {
        runAndCheckpoint(dir + "checkpoint_24.bds", dir + "checkpoint_24.chp", "out", 103);
    }

    /**
     * Test checkpoint created within a `case` function call (part of a `switch` statement)
     */
    @Test
    public void test25_switch() {
        runAndCheckpoint(dir + "checkpoint_25.bds", dir + "checkpoint_25.chp", "out", 35);
    }

    /**
     * Test checkpoint created within a `switch` statement calling a function
     * that falls through another case statement
     */
    @Test
    public void test26_switch() {
        runAndCheckpoint(dir + "checkpoint_26.bds", dir + "checkpoint_26.chp", "out", 56);
    }

    /**
     * Test checkpoint created within a `case` (`switch` statement) statement calling a function
     * The `case` falls through another case statement
     */
    @Test
    public void test27_switch() {
        runAndCheckpoint(dir + "checkpoint_27.bds", dir + "checkpoint_27.chp", "out", 35);
    }

    /**
     * Test checkpoint recovery of nested function calls: `f(g('hello'))`
     */
    @Test
    public void test28() {
        runAndCheckpoint(dir + "checkpoint_28.bds", dir + "checkpoint_28.chp", "out", 47);
    }

}
