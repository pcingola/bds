package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for custom resource allocation
 *
 * @author pcingola
 */
public class TestCasesCustomResources extends TestCasesBase {

    public TestCasesCustomResources() {
        dir = "test/unit/task/custom_resources/";
    }

    @Test
    public void test01_onlyOneTask() {
        var expectedOut = "Start task 0\n" +
                "End task 0\n" +
                "Start task 1\n" +
                "End task 1\n" +
                "Start task 2\n" +
                "End task 2";
        runAndCheckStdout(dir + "run_custom_resources_01.bds", expectedOut);
    }

    @Test
    public void test02_allTasksInParallel() {
        var expectedOut = "Start\n" +
                "Start\n" +
                "Start\n" +
                "End\n" +
                "End\n" +
                "End";
        runAndCheckStdout(dir + "run_custom_resources_02.bds", expectedOut);
    }

    @Test
    public void test03_notEnoughResources() {
        runAndCheckError(dir + "run_custom_resources_03.bds", "Error in wait statement, file test/task/custom_resources/run_custom_resources_03.bds, line 13");
    }
}
