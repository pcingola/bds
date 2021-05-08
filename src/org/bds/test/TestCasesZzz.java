package org.bds.test;

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
	public void test261_invalidKey() {
		Gpr.debug("Test");
		runAndCheckError("test/run_261.bds", "Invalid key 'hi' in map.");
	}

}
