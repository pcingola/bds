package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for JSON parsing
 *
 * @author pcingola
 */
public class TestCasesJson extends TestCasesBase {

    public TestCasesJson() {
        dir = "test/unit/json/";
    }

    @Test
    public void test01ScopeGlobalVariables() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("firstName", "John");
        expectedValues.put("lastName", "Smith");
        expectedValues.put("isbool", "true");
        expectedValues.put("age", "25");

        runAndCheck(dir + "json_01.bds", expectedValues);
    }

    @Test
    public void test02ScopeFunctionVariables() {
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

        runAndCheck(dir + "json_02.bds", expectedValues);
    }

    @Test
    public void test03ScopeGlobalObject() {
        Map<String, Object> expectedValues = new HashMap<>();

        // Function's private variables should be set
        expectedValues.put("a_streetAddress", "21 2nd Street");
        expectedValues.put("a_city", "New York");
        expectedValues.put("a_state", "NY");
        expectedValues.put("a_postalCode", "10021");

        runAndCheck(dir + "json_03.bds", expectedValues);
    }

    @Test
    public void test04ScopeFunctionObject() {
        Map<String, Object> expectedValues = new HashMap<>();

        // Function's private variables should be set
        expectedValues.put("address_private", "{ city: New York, postalCode: 10021, state: NY, streetAddress: 21 2nd Street }");
        expectedValues.put("address", "{ city: , postalCode: 0, state: , streetAddress:  }");

        runAndCheck(dir + "json_04.bds", expectedValues);
    }

    @Test
    public void test05ScopeGlobalList() {
        runAndCheck(dir + "json_05.bds", "phoneNumbers", "[{ number: 212 555-1234, type: home }, { number: 646 555-4567, type: fax }]");
    }

    @Test
    public void test06Object() {
        var expVal = "{ address: { city: New York, postalCode: 10021, state: NY, streetAddress: 21 2nd Street }, age: 25, firstName: John, isbool: true, lastName: Smith, phone: { number: 212 555-1234, type: home }, phoneNumbers: [{ number: 212 555-1234, type: home }, { number: 646 555-4567, type: fax }] }";
        runAndCheck(dir + "json_06.bds", "person", expVal);
    }

    @Test
    public void test07ScopeGlobalVariablesMatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("firstName", "John");
        expectedValues.put("lastName", "Smith");
        expectedValues.put("anotherName", "Joe");
        expectedValues.put("anotherName2", "Bill");

        runAndCheck(dir + "json_07.bds", expectedValues);
    }

    @Test
    public void test08FieldNamesMatch() {
        runAndCheck(dir + "json_08.bds", "a", "{ anotherName: Joe, anotherName2: Bill, firstName: John, last_Name: Smith }");
    }

    @Test
    public void test09JsonSetSideEffects() {
        Map<String, Object> expected = Map.of("var2", "default_value", "globalVar", "VALUE_FROM_JSON");
        runAndCheck(dir + "json_09.bds", expected);
    }

    @Test
    public void test10SubJson() {
        runAndCheck(dir + "json_10.bds", "a", "{ firstName: John, last_Name: Smith }");
    }

    @Test
    public void test11SubsubJson() {
        runAndCheck(dir + "json_11.bds", "a", "{ firstName: John, last_Name: Smith }");
    }

    @Test
    public void test12SubsubJsonErrorKey() {
        runAndCheckError(dir + "json_12.bds", "JSON object from file 'test/unit/json/json_11.json' does not contain field 'sub_b.sub_ii.sub_Z'");
    }

    @Test
    public void test13SubsubJsonErrorType() {
        runAndCheckError(dir + "json_13.bds", "JSON object from file 'test/unit/json/json_11.json', field 'sub_b.sub_i' is not an Object (value type 'NUMBER')");
    }


}
