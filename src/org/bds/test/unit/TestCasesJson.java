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
	public void test_01_scopeGlobalVariables() {
		Gpr.debug("Test");
		Map<String, Object> expectedValues = new HashMap<>();
		expectedValues.put("firstName", "John");
		expectedValues.put("lastName", "Smith");
		expectedValues.put("isbool", "true");
		expectedValues.put("age", "25");

		runAndCheck("test/json_01.bds", expectedValues);
	}

	@Test
	public void test_02_scopeFunctionVariables() {
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
	public void test_03_scopeGlobalObject() {
		Gpr.debug("Test");
		Map<String, Object> expectedValues = new HashMap<>();

		// Function's private variables should be set
		expectedValues.put("a_streetAddress", "21 2nd Street");
		expectedValues.put("a_city", "New York");
		expectedValues.put("a_state", "NY");
		expectedValues.put("a_postalCode", "10021");

		runAndCheck("test/json_03.bds", expectedValues);
	}

	@Test
	public void test_04_scopeFunctionObject() {
		Gpr.debug("Test");
		Map<String, Object> expectedValues = new HashMap<>();

		// Function's private variables should be set
		expectedValues.put("address_private", "{ city: New York, postalCode: 10021, state: NY, streetAddress: 21 2nd Street }");
		expectedValues.put("address", "{ city: , postalCode: 0, state: , streetAddress:  }");

		runAndCheck("test/json_04.bds", expectedValues);
	}

	@Test
	public void test_05_scopeGlobalList() {
		Gpr.debug("Test");
		runAndCheck("test/json_05.bds", "phoneNumbers", "[{ number: 212 555-1234, type: home }, { number: 646 555-4567, type: fax }]");
	}

	@Test
	public void test_06_object() {
		Gpr.debug("Test");
		var expVal = "{ address: { city: New York, postalCode: 10021, state: NY, streetAddress: 21 2nd Street }, age: 25, firstName: John, isbool: true, lastName: Smith, phone: { number: 212 555-1234, type: home }, phoneNumbers: [{ number: 212 555-1234, type: home }, { number: 646 555-4567, type: fax }] }";
		runAndCheck("test/json_06.bds", "person", expVal);
	}

}
