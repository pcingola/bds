package org.bds.test;

import org.bds.Bds;
import org.bds.Config;
import org.bds.run.BdsRun;
import org.bds.util.Gpr;
import org.junit.Before;
import org.junit.Test;

/**
 * Quick test cases when creating a new feature...
 *
 * @author pcingola
 *
 */
public class TestCasesZzz extends TestCasesBaseAws {

	@Before
	public void beforeEachTest() {
		BdsRun.reset();
		Config.get().load();
	}

	@Test
	public void testTestCasesCoverage06() {
		// Check that coverage is correctly computed: 100% coverage
		Gpr.debug("Test");
		verbose = true;
		Bds bds = runTestCasesPassCoverage("test/test_case_run_06.bds", 0.95);
		checkCoverageRatio(bds, 1.0);
	}

}
