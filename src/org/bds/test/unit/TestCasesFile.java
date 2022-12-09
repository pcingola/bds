package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for file methods
 *
 * @author pcingola
 */
public class TestCasesFile extends TestCasesBase {

    public TestCasesFile() {
        dir = "test/unit/run/";
    }

    @Test
    public void test21() {
        // File readLines
        runAndCheck(dir + "run_21.bds", "l1", "line 1\nline 2\nline 3\n");
    }

    @Test
    public void test21_2() {
        // File: readLines
        runAndCheck(dir + "run_21.bds", "l2", "line 2");
    }

    @Test
    public void test22() {
        // Files: Dir
        runAndCheck(dir + "run_22.bds", "l2", "file_3.txt");
    }

    @Test
    public void test69() {
        // File: removeExt
        runAndCheck(dir + "run_69.bds", "bgz", "path/to/file.txt");
    }

    @Test
    public void test69b() {
        // File: removeExt
        runAndCheck(dir + "run_69.bds", "btxt", "path/to/file");
    }


}
