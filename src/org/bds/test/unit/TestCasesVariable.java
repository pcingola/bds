package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for varaible definitions, initialization, assignments, etc.
 *
 * @author pcingola
 */
public class TestCasesVariable extends TestCasesBase {

    public TestCasesVariable() {
        dir = "test/unit/run/";
    }

    @Test
    public void test01() {
        // Variable definition, int
        compileOk(dir + "test01.bds");
    }

    @Test
    public void test02() {
        // Variable definition and assignment, int
        compileOk(dir + "test02.bds");
    }

    @Test
    public void test03() {
        // Variable definition and assignment, string
        compileOk(dir + "test03.bds");
    }

    @Test
    public void test10() {
        // Compile error: Undefined variable, direct reference
        String err = "ERROR [ file 'test/unit/lang/test10.bds', line 2 ] :	Symbol 'j' cannot be resolved\n";
        compileErrors(dir + "test10.bds", err);
    }

    @Test
    public void test11() {
        // Compile error: Undefined variable, within expression
        String err = "ERROR [ file 'test/unit/lang/test11.bds', line 2 ] :	Symbol 'j' cannot be resolved\n";
        compileErrors(dir + "test11.bds", err);
    }

    @Test
    public void test12() {
        // Variable definition, multiple definitions in one line, int
        compileOk(dir + "test12.bds");
    }

    @Test
    public void test13() {
        // Variable definition, multiple definitions in one line, string
        compileOk(dir + "test13.bds");
    }

    @Test
    public void test19() {
        // Duplicate variable definition
        String errs = "ERROR [ file 'test/unit/lang/test19.bds', line 4 ] :	Duplicate local name 'i'\n";
        compileErrors(dir + "test19.bds", errs);
    }

    @Test
    public void test20() {
        // Variable shadowing in function definition
        compileOk(dir + "test20.bds");
    }

    @Test
    public void test21() {
        // Variable shadowing in function definition and function block
        compileOk(dir + "test21.bds");
    }

    @Test
    public void test23() {
        // Variable definition using type inference
        compileOk(dir + "test23.bds");
    }

    @Test
    public void test24() {
        // Binary operator in variable definition
        compileOk(dir + "test24.bds");
    }

    @Test
    public void test43() {
        // Variable type void not allowed (assignment via function)
        String errs = "ERROR [ file 'test/unit/lang/test43.bds', line 8 ] :	Cannot declare variable 'res' type 'void'";
        compileErrors(dir + "test43.bds", errs);
    }

    @Test
    public void test48() {
        // Variable name using a reserved word
        // String errs = "ERROR [ file 'test/unit/lang/test48.bds', line 5 ] :	extraneous input ':=' expecting {<EOF>, 'while', '{', 'void', 'for', 'error', 'debug', 'int', 'include', 'task', '(', 'kill', '\n', 'println', 'exit', '++', '~', 'wait', 'dep', '+', 'goal', 'continue', 'return', ';', 'if', 'warning', 'break', 'print', 'switch', 'parallel', 'par', '[', '--', 'bool', '!', 'string', 'checkpoint', 'breakpoint', '-', 'real', BOOL_LITERAL, INT_LITERAL, REAL_LITERAL, STRING_LITERAL, STRING_LITERAL_SINGLE, HELP_LITERAL, SYS_LITERAL, TASK_LITERAL, ID}";
        String errs = "ERROR [ file 'test/unit/lang/test48.bds', line 5 ] :	extraneous input ':=' expecting ";
        compileErrors(dir + "test48.bds", errs);
    }
    @Test
    public void test69() {
        // Assign result from 'void' function
        compileErrors(dir + "test69.bds", "Cannot cast void to string[]");
    }



}
