package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for Map
 *
 * @author pcingola
 */
public class TestCasesMap extends TestCasesBase {

    public TestCasesMap() {
        dir = "test/unit/run/";
    }


    @Test
    public void test51() {
        // Map: Incorrect assignment (trying to assign a value to a map returned from a function)
        String errs = "ERROR [ file 'test/unit/lang/test51.bds', line 6 ] :	Cannot assign to non-variable 'f(  ){\"hi\"}'";
        compileErrors(dir + "test51.bds", errs);
    }

    @Test
    public void test72_castEmptyMapOfObjects() {
        // Initialize with an empty map of objects
        compileOk(dir + "test72.bds");
    }
    @Test
    public void test52() {
        // Hash assignment
        runAndCheck(dir + "run_52.bds", "hash", "{ one => 1 }");
    }
    @Test
    public void test55() {
        // Hash: hasValue, size, hasKey
        runAndCheck(dir + "run_55.bds", "hk1", "true");
        runAndCheck(dir + "run_55.bds", "hk2", "false");
        runAndCheck(dir + "run_55.bds", "hv1", "true");
        runAndCheck(dir + "run_55.bds", "hv2", "false");
        runAndCheck(dir + "run_55.bds", "hk3", "false");
    }

    @Test
    public void test64() {
        // Hash: Empty hash literal
        runAndCheck(dir + "run_64.bds", "m", "{}");
    }


}
