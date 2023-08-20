package org.bds.test.unit;

import org.bds.lang.type.Types;
import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for language & compilation
 * <p>
 * Note: These test cases just check language parsing and compilation (what is supposed to compile OK, and what is not).
 *
 * @author pcingola
 */
public class TestCasesVm extends TestCasesBase {

    public TestCasesVm() {
        dir = "test/unit/vm/";
    }

    @Test
    public void test00() {
        runVmAndCheck(dir + "vm00.asm", "a", "42");
    }

    @Test
    public void test01() {
        Types.reset();
        runVmAndCheck(dir + "vm01.asm", "z", "8");
    }

    @Test
    public void test02() {
        runVmAndCheck(dir + "vm02.asm", "z", "hi");
    }

    @Test
    public void test03() {
        runVmAndCheck(dir + "vm03.asm", "z", "bye");
    }

}
