package org.bds.test;

import org.bds.Bds;
import org.bds.Config;
import org.bds.run.BdsRun;
import org.bds.run.Coverage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public void testTestCasesCoverage23() {
        // Check that coverage is correctly computed, excluding test case code from the stats.
        // Testing coverage after failed assert
        verbose = true;
        Bds bds = runTestCasesFailCoverage("test/z.bds", 0.5);

        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(5, coverage.getCountLines());
        Assert.assertEquals(5, coverage.getCountCovered());
        Assert.assertEquals(21, coverage.getCountTestCodeLines());
        Assert.assertEquals(11, coverage.getCountTestCoveredLines());
    }

}
