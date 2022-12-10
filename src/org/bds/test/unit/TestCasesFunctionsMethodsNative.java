package org.bds.test.unit;

import junit.framework.Assert;
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
        dir = "test/unit/functions_native/";
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
    @Test
    public void test103() {
        // Range
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("is", "[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        expectedValues.put("is2", "[1, 3, 5, 7, 9]");
        expectedValues.put("rs", "[1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 9.5, 10.0]");

        runAndCheck(dir + "run_103.bds", expectedValues);
    }

    @Test
    public void test253Getvar() {
        // Function: getVar()
        HashMap<String, Object> expectedValues = new HashMap<>();

        expectedValues.put("shome", System.getenv().get("HOME"));
        expectedValues.put("bhome", true);

        expectedValues.put("szzz", "");
        expectedValues.put("bzzz", false);

        runAndCheck(dir + "run_253.bds", expectedValues);
    }

    @Test
    public void test254Getvar() {
        // Function: getVar() with default value
        HashMap<String, Object> expectedValues = new HashMap<>();

        expectedValues.put("shome", System.getenv().get("HOME"));
        expectedValues.put("szzz", "VALUE_DEFAULT_2");
        expectedValues.put("szzzxxxzzz", "VALUE_DEFAULT_3");

        runAndCheck(dir + "run_254.bds", expectedValues);
    }

    @Test
    public void test255GetModuleName() {
        // Function: getModulePath()
        runAndCheck(dir + "run_255.bds", "b", "run_255.bds");
    }

    @Test
    public void test256GetModuleName() {
        // Function: getModulePath()
        runAndCheck(dir + "run_256.bds", "db", "functions_native");
    }

    @Test
    public void test257RandIntDivisionByZero() {
        // Function: randInt(0)
        runAndCheck(dir + "run_257.bds", "r", "0");
    }
}
