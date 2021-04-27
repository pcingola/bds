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
	public void test51() {
		Gpr.debug("Test");
		verbose = true;
		runAndCheck("test/run_51.bds", "hash", "{ hi => bye }");
	}

	//	@Test
	//	public void test52() {
	//		Gpr.debug("Test");
	//		runAndCheck("test/run_52.bds", "hash", "{ one => 1 }");
	//	}
	//
	//	@Test
	//	public void test53() {
	//		Gpr.debug("Test");
	//		runAndCheck("test/run_53.bds", "vals", "[bye, chau]");
	//	}
	//
	//	@Test
	//	public void test54() {
	//		Gpr.debug("Test");
	//		runAndCheck("test/run_54.bds", "vals", "[hi, hola]");
	//	}
	//
	//	@Test
	//	public void test55() {
	//		Gpr.debug("Test");
	//		runAndCheck("test/run_55.bds", "hk1", "true");
	//		runAndCheck("test/run_55.bds", "hk2", "false");
	//		runAndCheck("test/run_55.bds", "hv1", "true");
	//		runAndCheck("test/run_55.bds", "hv2", "false");
	//		runAndCheck("test/run_55.bds", "hk3", "false");
	//	}
	//
	//	@Test
	//	public void test56() {
	//		Gpr.debug("Test");
	//		runAndCheck("test/run_56.bds", "out", "Adios;Au revoir;Bye;");
	//		runAndCheck("test/run_56.bds", "str", "map = { Bonjour => Au revoir, Hello => Bye, Hola => Adios }");
	//	}

}
