package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for native functions and methods
 *
 * @author pcingola
 */
public class TestCasesFunctionsMethodsNative extends TestCasesBase {

    public TestCasesFunctionsMethodsNative() {
        dir = "test/unit/run/";
    }

    @Test
    public void test89() {
        // String: swapExt
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f", "file.txt");
        expectedValues.put("f2", "file.vcf");
        expectedValues.put("f3", "file.vcf");
        expectedValues.put("f4", "file.txt.vcf");
        runAndCheck(dir + "run_89.bds", expectedValues);
    }

    @Test
    public void test17() {
        // String methods, int and bool operators
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("s", " HEllo ");
        expectedValues.put("s1", "HEllo");
        expectedValues.put("s2", " hello ");
        expectedValues.put("s3", " HELLO ");
        expectedValues.put("s4", "Ello ");
        expectedValues.put("s5", "Ell");
        expectedValues.put("s6", " HEllo ");
        expectedValues.put("s7", "");
        expectedValues.put("s8", " HEzo ");
        expectedValues.put("i1", 7);
        expectedValues.put("i2", 3);
        expectedValues.put("i3", 4);
        expectedValues.put("i4", -1);
        expectedValues.put("i5", -1);
        expectedValues.put("ss", "hello");
        expectedValues.put("b1", true);
        expectedValues.put("b2", false);
        expectedValues.put("b3", true);
        expectedValues.put("b4", false);
        runAndCheck(dir + "run_17.bds", expectedValues);
    }

}
