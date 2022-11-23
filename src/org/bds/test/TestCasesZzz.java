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
    public void testTestCasesCoverage21() {
        // Check that coverage is correctly computed, excluding test case code from the stats
        Bds bds = runTestCasesPassCoverage("test/z.bds", 0.99);
        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(5, coverage.getCountLines());
        Assert.assertEquals(5, coverage.getCountCovered());
        Assert.assertEquals(17, coverage.getCountTestCodeLines());
        Assert.assertEquals(7, coverage.getCountTestCoveredLines());
    }

}
