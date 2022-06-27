package org.bds.test.unit;

import org.bds.lang.type.Types;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

/**
 * Test cases for language & compilation
 *
 * Note: These test cases just check language parsing and compilation (what is supposed to compile OK, and what is not).
 *
 * @author pcingola
 *
 */
public class TestCasesVm extends TestCasesBase {

	@Test
	public void test00() {
		runVmAndCheck("test/vm00.asm", "a", "42");
	}

	@Test
	public void test01() {
		Types.reset();
		runVmAndCheck("test/vm01.asm", "z", "8");
	}

	@Test
	public void test02() {
		runVmAndCheck("test/vm02.asm", "z", "hi");
	}

	@Test
	public void test03() {
		runVmAndCheck("test/vm03.asm", "z", "bye");
	}

}
