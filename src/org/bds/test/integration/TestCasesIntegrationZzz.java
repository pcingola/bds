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
	 * Listing a remote HTTP directory using regex (`dir(regex)`)
	 */
	@Test
	public void test30_HttpDirRegex() {
		runAndCheck("test/z.bds", "dd", "[Homo_sapiens.GRCh37.75.dna.toplevel.fa.gz]");
	}

}
