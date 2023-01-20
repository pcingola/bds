package org.bds.test;

import org.bds.Bds;
import org.bds.Config;
import org.bds.run.BdsRun;
import org.junit.Before;
import org.junit.Test;

/**
 * Quick test cases when creating a new feature...
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCasesBaseAws {

    public TestCasesZzz() {
        dir = "test/";
    }

    @Before
    public void beforeEachTest() {
        BdsRun.reset();
        Config.get().load();
    }

    @Test
    public void testTestCasesCoverage06() {
        verbose = true;
        runOk(dir + "z.bds");
//        !!!!!!!!!!!!!!!!!!!
//        SERGEY's MESSAGE: WaitException shows on stderr
//        CREATE TEST
//        CHECK EMPTY STDOUT / STDERR
    }
}
