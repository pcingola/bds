package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for custom resource allocation
 *
 * @author pcingola
 */
public class TestCasesTaskCustomResources extends TestCasesBase {

    public TestCasesTaskCustomResources() {
        dir = "test/unit/task/custom_resources/";
    }

    @Test
    public void test01OnlyOneTask() {
        var expectedOut = "Start task 0\n" +
                "End task 0\n" +
                "Start task 1\n" +
                "End task 1\n" +
                "Start task 2\n" +
                "End task 2";
        runAndCheckStdout(dir + "run_custom_resources_01.bds", expectedOut);
    }

    @Test
    public void test02AllTasksInParallel() {
        var expectedOut = "Start\n" +
                "Start\n" +
                "Start\n" +
                "End\n" +
                "End\n" +
                "End";
        runAndCheckStdout(dir + "run_custom_resources_02.bds", expectedOut);
    }

    @Test
    public void test03NotEnoughResources() {
        runAndCheckError(dir + "run_custom_resources_03.bds", "WaitException thrown: Error in wait statement, file test/unit/task/custom_resources/run_custom_resources_03.bds, line 13\n");
    }
}
