package org.bds.test.unit;

import org.junit.Assert;
import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Test cases for function definitions, invocations, etc.
 *
 * @author pcingola
 */
public class TestCasesFunctions extends TestCasesBase {

    public TestCasesFunctions() {
        dir = "test/unit/functions/";
    }

    @Test
    public void test17() {
        // Function definition and function call
        compileOk(dir + "test17.bds");
    }

    @Test
    public void test18() {
        // Function definition with default value
        compileOk(dir + "test18.bds");
    }

    @Test
    public void test22() {
        // Function definition within function
        compileOk(dir + "test22.bds");
    }

    @Test
    public void test30() {
        // Function definition: Wrong casting in 'return' statement
        String errs = "ERROR [ file 'test/unit/functions/test30.bds', line 4 ] :	Cannot cast real to int\n";
        compileErrors(dir + "test30.bds", errs);
    }

    @Test
    public void test31() {
        // Function definition: Missing return statement
        String errs = "ERROR [ file 'test/unit/functions/test31.bds', line 4 ] :	Function has no return statement\n";
        compileErrors(dir + "test31.bds", errs);
    }

    @Test
    public void test34() {
        // Function call: Undefined function
        String errs = "ERROR [ file 'test/unit/functions/test34.bds', line 5 ] :	Function f(int) cannot be resolved\n";
        compileErrors(dir + "test34.bds", errs);
    }

    @Test
    public void test47() {
        // Function has the same name as a variable
        String errs = "ERROR [ file 'test/unit/functions/test47.bds', line 5 ] :	Duplicate local name 'gsea'";
        compileErrors(dir + "test47.bds", errs);
    }

    @Test
    public void test63() {
        // Function definition: Duplicate name for variable and function
        compileErrors(dir + "test63.bds", "ERROR [ file 'test/unit/functions/test63.bds', line 5 ] :	Duplicate local name 'zzz'");
    }

    @Test
    public void test64() {
        // Function definition: Duplicate function names
        compileErrors(dir + "test64.bds", "Duplicate function 'zzz() -> void'");
    }
    @Test
    public void test70() {
        // Function: Return different type of array (class)
        compileErrors(dir + "test70.bds", "Cannot cast A[] to B[]");
    }
    @Test
    public void test07() {
        // Function definition
        runAndCheck(dir + "run_07.bds", "j", 4L);
    }

    @Test
    public void test08() {
        // Function definition
        runAndCheck(dir + "run_08.bds", "j", 5L);
    }

    @Test
    public void test09() {
        // Function with two return statements
        runAndCheck(dir + "run_09.bds", "i", 5L);
    }
    @Test
    public void test12() {
        // Function: Variable inside a function shadowing a global variable
        runAndCheck(dir + "run_12.bds", "i", 1L);
    }

    @Test
    public void test13() {
        // Function: Variable inside a function shadowing a global variable
        runAndCheck(dir + "run_13.bds", "h", 8L);
    }

    @Test
    public void test57() {
        // Function: If with return
        runAndCheck(dir + "run_57.bds", "z", 0L);
    }

    @Test
    public void test58() {
        // Function: recursion
        runAndCheck(dir + "run_58.bds", "z", 0L);
    }

    @Test
    public void test59() {
        // Function: recursion
        runAndCheck(dir + "run_59.bds", "z", -1L);
    }

    @Test
    public void test107() {
        // Config function
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("paramName", "parameter_value");
        expectedValues.put("file1", "/path/to/file_1.txt");
        expectedValues.put("file2", "/path/to/file_2.txt");
        expectedValues.put("file3", "/path/to/file_3.txt");
        expectedValues.put("file4", "/path/to/file_4.txt");
        expectedValues.put("file5", "/path/to/file_5.txt");

        runAndCheck(dir + "run_107.bds", expectedValues);
    }

    @Test
    public void test108() {
        // Config function
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("paramName", "parameter_value");
        expectedValues.put("file1", "/path/to/file_1.txt");
        expectedValues.put("file2", "/path/to/file_2.txt");
        expectedValues.put("file3", "/path/to/file_3.NEW.txt");
        expectedValues.put("file4", "/path/to/file_4.txt");
        expectedValues.put("file5", "/path/to/file_5.NEW.txt");

        runAndCheck(dir + "run_108.bds", expectedValues);
    }

    @Test
    public void test109() {
        // Random seed
        runAndCheck(dir + "run_109.bds", "r1", "4027146782649399912");
    }

    @Test
    public void test113ParallelFunctionCalls() {
        // Parallel function calls 'par'
        String stdout = runAndReturnStdout(dir + "run_113.bds");

        Set<String> linesPar = new HashSet<>();
        for (String line : stdout.split("\n")) {
            if (line.startsWith("Par:")) {
                if (linesPar.contains(line)) throw new RuntimeException("Line repeated (this should never happen): '" + line + "'");
                linesPar.add(line);
            }
        }
    }

    @Test
    public void test116LineWrapBackslashId() {
        // Function definition multiple lines continued
        String stdout = runAndReturnStdout(dir + "run_116.bds");
        Assert.assertEquals("hi bye\nThe answer\t\tis: 42", stdout);
    }
}
