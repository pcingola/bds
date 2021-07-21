package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for JSON parsing
 *
 * @author pcingola
 */
public class TestCasesJson extends TestCasesBase {

	@Test
	public void test_01_globalVariables() {
		Gpr.debug("Test");
		Map<String, Object> expectedValues = new HashMap<>();
		expectedValues.put("firstName", "John");
		expectedValues.put("lastName", "Smith");
		expectedValues.put("isbool", "true");
		expectedValues.put("age", "25");

		runAndCheck("test/json_01.bds", expectedValues);
	}

	@Test
	public void test_02_functionVariables() {
		Gpr.debug("Test");
		Map<String, Object> expectedValues = new HashMap<>();

		// Function's private variables should be set
		expectedValues.put("firstName_private", "John");
		expectedValues.put("lastName_private", "Smith");
		expectedValues.put("isbool_private", "true");
		expectedValues.put("age_private", "25");

		// Global variables should be unset
		expectedValues.put("firstName", "");
		expectedValues.put("lastName", "");
		expectedValues.put("isbool", "false");
		expectedValues.put("age", "1");

		runAndCheck("test/json_02.bds", expectedValues);
	}

	@Test
	public void test_03_object() {
		Gpr.debug("Test");
		Map<String, Object> expectedValues = new HashMap<>();

		// Function's private variables should be set
		expectedValues.put("a_streetAddress", "21 2nd Street");
		expectedValues.put("a_city", "New York");
		expectedValues.put("a_state", "NY");
		expectedValues.put("a_postalCode", "10021");

		runAndCheck("test/json_03.bds", expectedValues);
	}

}
