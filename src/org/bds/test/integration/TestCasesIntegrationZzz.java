package org.bds.test.integration;

import org.bds.Bds;
import org.bds.run.BdsThread;
import org.bds.run.BdsThreads;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * New integration tests are tested here
 */
public class TestCasesIntegrationZzz extends TestCasesBase {


	/**
	 * Serialize an executioner
	 */
	@Test
	public void test29() {
		Gpr.debug("Test");
		verbose = true;
		runAndCheckpoint("test/z.bds", "test/checkpoint_28.chp", "out", 47);
	}

//	/**
//	 * Do not create a checkpoint when "-noChp" command line option is used
//	 */
//	@Test
//	public void test30() {
//		Gpr.debug("Test");
//		runAndCheckpoint("test/checkpoint_30.bds", "test/checkpoint_28.chp", "out", 47);
//	}

}
