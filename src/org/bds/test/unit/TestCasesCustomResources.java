package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for custom resource allocation
 *
 * @author pcingola
 */
public class TestCasesCustomResources extends TestCasesBase {

	@Test
	public void test01_onlyOneTask() {
		var expectedOut = "Start task 0\n" +
				"End task 0\n" +
				"Start task 1\n" +
				"End task 1\n" +
				"Start task 2\n" +
				"End task 2";
		runAndCheckStdout("test/run_custom_resources_01.bds", expectedOut);
	}

	@Test
	public void test02_allTasksInParallel() {
		var expectedOut = "Start\n" +
				"Start\n" +
				"Start\n" +
				"End\n" +
				"End\n" +
				"End";
		runAndCheckStdout("test/run_custom_resources_02.bds", expectedOut);
	}

	@Test
	public void test03_notEnoughResources() {
		runAndCheckError("test/run_custom_resources_03.bds", "Error in wait statement, file test/run_custom_resources_03.bds, line 13");
	}
}
