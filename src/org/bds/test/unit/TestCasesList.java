package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;

/**
 * Test cases for List
 *
 * @author pcingola
 */
public class TestCasesList extends TestCasesBase {

    public TestCasesList() {
        dir = "test/unit/run/";
    }

    @Test
    public void test37() {
        // List definition: List literal and missing variable
        String errs = "ERROR [ file 'test/unit/lang/test37.bds', line 16 ] :	Symbol 'fruit' cannot be resolved\n";
        compileErrors(dir + "test37.bds", errs);
    }

    @Test
    public void test39() {
        // List: Incorrect type assignment
        String errs = "ERROR [ file 'test/unit/lang/test39.bds', line 6 ] :	Cannot cast string to int\n";
        compileErrors(dir + "test39.bds", errs);
    }

    @Test
    public void test40() {
        // List: Push method with wrong type
        String errs = "ERROR [ file 'test/unit/lang/test40.bds', line 6 ] :	Method int[].push(string) cannot be resolved\n";
        compileErrors(dir + "test40.bds", errs);
    }

    @Test
    public void test44() {
        // List plus operator, wrong type
        String errs = "ERROR [ file 'test/unit/lang/test44.bds', line 2 ] :	Cannot append int[] to string[]";
        compileErrors(dir + "test44.bds", errs);
    }

    @Test
    public void test45() {
        // List plus operator
        compileOk(dir + "test45.bds");
    }

    @Test
    public void test46() {
        // List element access
        compileOk(dir + "test46.bds");
    }


    @Test
    public void test50() {
        // List: Incorrect assignment (trying to assign a value to a list returned from a function)
        String errs = "ERROR [ file 'test/unit/lang/test50.bds', line 6 ] :\tCannot assign to non-variable 'f(  )[0]'";
        compileErrors(dir + "test50.bds", errs);
    }


    @Test
    public void test71_castEmptyListOfObjects() {
        // Initialize with an empty list of objects
        compileOk(dir + "test71.bds");
    }
    @Test
    public void test15_2() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li2", "[apple, orange, bannana]");
    }

    @Test
    public void test15_3() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li3", "[apple, orange, 1]");
    }

    @Test
    public void test15_4() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li4", "[apple, orange, 3.14]");
    }

    @Test
    public void test15_5() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li5", "[apple, orange, false]");
    }

    @Test
    public void test15_6() {
        // List initialization
        runAndCheck(dir + "run_15.bds", "li6", "[apple, orange, i=10hihihi]");
    }

    @Test
    public void test19() {
        // List method head
        runAndCheck(dir + "run_19.bds", "h", "one");
    }

    @Test
    public void test20() {
        // List assign
        runAndCheck(dir + "run_20.bds", "h", "dos");
    }

    @Test
    public void test23() {
        // List: head, tail
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("ltsize", 2);
        expectedValues.put("lh", "one");
        expectedValues.put("lt0", "two");
        expectedValues.put("lt1", "three");
        runAndCheck(dir + "run_23.bds", expectedValues);
    }

    @Test
    public void test24() {
        // List: pop
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("p", 3);
        expectedValues.put("s", 3);
        expectedValues.put("l0", 1);
        expectedValues.put("l1", 2);
        expectedValues.put("l2", 4);
        runAndCheck(dir + "run_24.bds", expectedValues);
    }

    @Test
    public void test25() {
        // List: add
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l0", 1);
        expectedValues.put("l1", 2);
        expectedValues.put("l2", 3);
        runAndCheck(dir + "run_25.bds", expectedValues);
    }

    @Test
    public void test26() {
        // List: sort and element access
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l0", 1);
        expectedValues.put("l1", 2);
        expectedValues.put("l2", 3);
        expectedValues.put("l3", 4);
        expectedValues.put("l4", 5);
        expectedValues.put("s", 5);
        runAndCheck(dir + "run_26.bds", expectedValues);
    }

    @Test
    public void test28() {
        // List: add
        runAndCheck(dir + "run_28.bds", "events", "[done]");
    }

    @Test
    public void test63() {
        // List: Empty list literals
        runAndCheck(dir + "run_63.bds", "l", "[]");
    }

    @Test
    public void test76() {
        // For loop: List: add
        runAndCheck(dir + "run_76.bds", "list", "[0, 2, 4, 6, 8, 10]");
    }

    @Test
    public void test77() {
        // For loop: List: add
        runAndCheck(dir + "run_77.bds", "list", "[10, 8, 6, 4, 2, 0]");
    }

    @Test
    public void test78() {
        // For loop: List: add
        runAndCheck(dir + "run_78.bds", "list", "[1, 2, 4, 8, 16, 32, 64]");
    }

    @Test
    public void test79() {
        // For loop: List: add
        runAndCheck(dir + "run_79.bds", "list", "[128, 64, 32, 16, 8, 4, 2, 1]");
    }

    @Test
    public void test95() {
        // List: Concatenation
        runAndCheck(dir + "run_95.bds", "ll", "[zero, one, two, three, four, 5]");
    }

    @Test
    public void test96() {
        // Operator '+=': List
        runAndCheck(dir + "run_96.bds", "l", "[one, two, three, four]");
    }
    @Test
    public void test120_split_empty_string() {
        // List: String split, list size(), list isEmpty()
        runAndCheck(dir + "run_120.bds", "len", "0");
    }

    @Test
    public void test121_split_fail_regex() {
        // List: split with invalid regex
        runAndCheck(dir + "run_121.bds", "len", "1");
    }
    @Test
    public void test135() {
        // List: Variable assignment
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l", "[1, 2, 3, 99]");
        expectedValues.put("lc1", "[1, 2, 3, 99]");
        expectedValues.put("lc2", "[1, 2, 3, 99]");
        expectedValues.put("lc3", "[1, 2, 3, 99]");

        runAndCheck(dir + "run_135.bds", expectedValues);
    }

    @Test
    public void test135_clone() {
        // List: Clone
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l", "[1, 2, 3, 99]");
        expectedValues.put("lc1", "[1, 2, 3]");
        expectedValues.put("lc2", "[1, 2, 3]");
        expectedValues.put("lc3", "[1, 2, 3]");

        runAndCheck(dir + "run_135_clone.bds", expectedValues);
    }

    @Test
    public void test136() {
        // List Methods: Reverse, add, clone, count, indexOf, removeIdx
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l", "[1, 99, 2, 3]");
        expectedValues.put("l2", "[3, 2, 99, 1]");
        expectedValues.put("l3", "[3, 2, 99, 1, 99]");
        expectedValues.put("l3count", "2");
        expectedValues.put("l3idx", "2");
        expectedValues.put("l4", "[3, 2, 1, 99]");
        expectedValues.put("l5", "[3, 2, 1, 99]");

        runAndCheck(dir + "run_136.bds", expectedValues);
    }

    @Test
    public void test137() {
        // List: has() method
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l", "[1, 2, 3]");
        expectedValues.put("has2", "true");
        expectedValues.put("has7", "false");

        runAndCheck(dir + "run_137.bds", expectedValues);
    }

    @Test
    public void test140_list_nonvariable() {
        // List: Assignment from function return value
        runAndCheck(dir + "run_140.bds", "i", "2");
    }
    @Test
    public void test155_list_sort() {
        // List: sort string
        runAndCheck(dir + "run_155.bds", "sl", "[a, a, a+a, a_a, aa, aaa, aaaa, aaaaaa, x, y, z]");
    }

    @Test
    public void test156_list_int_sort() {
        // List: sort int
        runAndCheck(dir + "run_156.bds", "sl", "[-99, -1, 1, 2, 3, 9, 23, 99, 101]");
    }


}
