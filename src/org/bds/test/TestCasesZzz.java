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
	public void test268_try_catch_nested() {
		var expectedStdout = "START\n" //
				+ "TRY 1: Start\n" //
				+ "TRY 2: Start\n" //
				+ "CATCH 2\n" //
				+ "FINALLY 2\n" //
				+ "TRY 1: End\n" //
				+ "FINALLY 1\n" //
				+ "END\n";
		runAndCheckStdout("test/z.bds", expectedStdout);
	}

//	@Test
//	public void test268_try_catch_exception_class_defined_after_function() {
//		runOk("test/run_268.bds");
//	}
}
