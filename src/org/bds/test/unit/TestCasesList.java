package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

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

}
