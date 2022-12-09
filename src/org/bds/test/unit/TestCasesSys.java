package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for 'sys' statements
 *
 * @author pcingola
 */
public class TestCasesSys extends TestCasesBase {

    public TestCasesSys() {
        dir = "test/unit/run/";
    }

    @Test
    public void test26() {
        // sys statements, parsing escaped characters and multi-line sys statements
        compileOk(dir + "test26.bds");
    }


}
