package org.bds.test;

import org.bds.Config;
import org.bds.run.BdsRun;
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
	public void test265_try_catch_parent_exception_class() {
		runAndCheck("test/run_265.bds", "out", "try_start catch finally");
	}

	@Test
	public void test266_try_catch_exception_class() {
		verbose = true;
		runAndCheck("test/run_266.bds", "out", "try_start catch finally");
	}

}
