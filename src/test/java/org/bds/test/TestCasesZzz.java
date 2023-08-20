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
        dir = "test/unit/test_cases/";
    }

    @Before
    public void beforeEachTest() {
        BdsRun.reset();
        Config.get().load();
    }

    @Test
    public void testTestCasesCoverage06() {
        // Check that coverage is correctly computed: 100% coverage
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_06.bds", 0.95);
        checkCoverageRatio(bds, 1.0);
    }
}
