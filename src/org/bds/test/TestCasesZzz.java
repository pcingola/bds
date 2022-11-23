package org.bds.test;

import org.bds.Bds;
import org.bds.Config;
import org.bds.run.BdsRun;
import org.bds.run.Coverage;
import org.bds.util.Gpr;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Quick test cases when creating a new feature...
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCasesBaseAws {

    @Before
    public void beforeEachTest() {
        BdsRun.reset();
        Config.get().load();
    }


    @Test
    public void testTestCasesCoverage20() {
        // Check that coverage is correctly computed, excluding test case code from the stats.
        // Case: One-liners
        var coverageFileName = "test/test_case_run_20.coverage";
        Bds bds = runTestCasesSaveCoverage("test/test_case_run_20_1.bds", coverageFileName, true, 0.0);
        bds = runTestCasesSaveCoverage("test/test_case_run_20_2.bds", coverageFileName, false, 0.99);

        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(2, coverage.getCountLines());
        Assert.assertEquals(2, coverage.getCountCovered());
        Assert.assertEquals(2, coverage.getCountTestCodeLines());
        Assert.assertEquals(2, coverage.getCountTestCoveredLines());
    }
}
