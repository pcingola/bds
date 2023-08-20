package org.bds.test.integration;

import junit.framework.Assert;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

import java.io.File;

/**
 * Test cases on graph construction and 'implicit wait' commands
 * Running bds code: Task graphs and dependencies (local, long running tests)
 *
 * @author pcingola
 */
public class TestCasesIntegrationGraph extends TestCasesBase {

    public TestCasesIntegrationGraph() {
        dir = "test/integration/graph/";
    }

    /**
     * Test simple task dependency: task_1 -> task_2
     */
    @Test
    public void test01() {
        runAndCheck(dir + "graph_01.bds", "output", "IN\nTASK 1\nTASK 2\n");
    }

    /**
     * Test simple task dependency: task_1 -> task_2 -> task_3
     */
    @Test
    public void test02() {
        runAndCheck(dir + "graph_02.bds", "output", "IN\nTASK 1\nTASK 2\nTASK 3\n");
    }

    /**
     * Test checkpoint during task execution: must recover the task
     */
    @Test
    public void test03() {
        runAndCheckpoint(dir + "graph_03.bds", dir + "graph_03.chp", "out", "Task start\nTask end\n");
    }

    /**
     * Test checkpoint during execution of two dependent tasks.
     * Must recover both tasks and their dependencies
     */
    @Test
    public void test04() {
        runAndCheckpoint(dir + "graph_04.bds", dir + "graph_04.chp", "out", "IN\nTASK 1\nTASK 2\n");
    }

    /**
     * Test task dependencies of two tasks with multiple input files scenarios
     */
    @Test
    public void test05() {
        // Remove old entries
        File txt = new File(dir + "graph_05.txt");
        File csv = new File(dir + "graph_05.csv");
        File xml = new File(dir + "graph_05.xml");
        txt.delete();
        csv.delete();
        xml.delete();

        // Create file
        Gpr.toFile(dir + "graph_05.txt", "TEST");

        // Run pipeline first
        if (verbose) System.out.println("Run first time:");
        String out = runAndCheckStdout(dir + "graph_05.bds", "copying to csv\ncopying to xml");
        if (verbose) System.out.println(out);

        // Remove CSV file
        csv.delete();

        // Run pipeline again
        if (verbose) System.out.println("Run second time:");
        out = runAndCheckStdout(dir + "graph_05.bds", "copying to csv\ncopying to xml");
        if (verbose) System.out.println(out);
    }

    /**
     * Test `dep` and `goal` dependencies of two tasks with multiple input files scenarios
     */
    @Test
    public void test06() {
        // Remove old entries
        String prefix = dir + "graph_06";
        File txt = new File(prefix + ".txt");
        File csv = new File(prefix + ".csv");
        File xml = new File(prefix + ".xml");
        txt.delete();
        csv.delete();
        xml.delete();

        // Create file
        Gpr.toFile(prefix + ".txt", "TEST");

        // Run pipeline first
        if (verbose) System.out.println("Run first time:");
        String out = runAndCheckStdout(prefix + ".bds", "copying to csv\ncopying to xml");
        if (verbose) System.out.println(out);

        // Remove CSV file
        csv.delete();

        // Run pipeline again (nothing should happen, since XML is 'up to date' with respect to TXT)
        if (verbose) System.out.println("Run second time:");
        out = runAndCheckStdout(prefix + ".bds", "copying", true);
        if (verbose) System.out.println(out);
    }

    /**
     * Check `dep` defined backwards (last task defined first)
     */
    @Test
    public void test08() {
        runAndCheckStdout(dir + "graph_08.bds", "MID1\nMID2\nOUT");
    }

    /**
     * Check `dep` definitions not sorted
     */
    @Test
    public void test09() {
        runAndCheckStderr(dir + "graph_09.bds", "Circular dependency");
    }

    /**
     * Test four `dep` tasks defined with lists of dependencies
     */
    @Test
    public void test10() {
        runAndCheck(dir + "graph_10.bds", "num", "2");
    }

    /**
     * Test two `dep` tasks defined with lists of dependencies
     */
    @Test
    public void test11_circularDependencyCheck() {
        runAndCheck(dir + "graph_11.bds", "ok", "true");
    }

    /**
     * Test `dep` using a circular dependency, it must error during execution
     */
    @Test
    public void test12_circularDependency() {
        runAndCheckStderr(dir + "graph_12.bds", "Circular dependency");
    }

    /**
     * Test `goal` using a taskIf instead of a file
     */
    @Test
    public void test13_goal_using_taskId() {
        runAndCheckStdout(dir + "graph_13.bds", "out1_2.txt\nout2_1.txt");
    }

    /**
     * Test `dep` and `goal` not having any input file in firts task
     */
    @Test
    public void test14_dep_using_taskId() {
        runAndCheckStdout(dir + "graph_14.bds", "Hello\nBye");
    }

    /**
     * Test `goal` with a list
     */
    @Test
    public void test15_goal_using_list() {
        String out = runAndReturnStdout(dir + "graph_15.bds");
        Assert.assertTrue(out.contains("Hi 0\n"));
        Assert.assertTrue(out.contains("Hi 1\n"));
        Assert.assertTrue(out.contains("Hi 2\n"));
    }

    /**
     * Test `dep` with no inputs and intermediate taskIds dependencies
     */
    @Test
    public void test16_dep_no_input() {
        runAndCheckStdout(dir + "graph_16.bds", "DEP_1\nDEP_2\nDEP_3");
    }

    /**
     * Test "circular dependency error in `dep` with no inputs and
     * intermediate taskIds dependencies
     */
    @Test
    public void test17_circular_taskid() {
        runAndCheckStderr(dir + "graph_17.bds", "Circular dependency on task");
    }

    /**
     * Test error having a taskId as a dependency output
     */
    @Test
    public void test18_out_tasksId() {
        runAndCheckStderr(dir + "graph_18.bds", "Cannot have task as a dependency output");
    }

    /**
     * Test mixture of `task` and `dep` on taskIds
     */
    @Test
    public void test19_dep_task_already_executed() {
        runAndCheckStdout(dir + "graph_19.bds", "TASK_1\nDEP_2");
    }

    /**
     * Test chain of `dep` and `goal`, all of them with taskIds (no files)
     */
    @Test
    public void test20_dep_goal_taskid() {
        runAndCheckStdout(dir + "graph_20.bds", "DEP_1\nDEP_2\nDEP_3");
    }

    /**
     * Test early goal in chain of `dep` and `goal`, all of them with taskIds (no files)
     */
    @Test
    public void test21_dep_goal_taskid_early() {
        runAndCheckStdout(dir + "graph_21.bds", "DEP_1\nDEP_2");
    }

}
