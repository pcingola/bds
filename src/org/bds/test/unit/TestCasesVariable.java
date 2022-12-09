package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;

/**
 * Test cases for varaible definitions, initialization, assignments, etc.
 *
 * @author pcingola
 */
public class TestCasesVariable extends TestCasesBase {

    public TestCasesVariable() {
        dir = "test/unit/variables/";
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

    @Test
    public void test06() {
        // Cast in Variable assignment: Real from bool and int
        runAndCheck(dir + "run_06.bds", "i", 1L);
    }

    @Test
    public void test06_2() {
        runAndCheck(dir + "run_06.bds", "r1", 1.0);
    }

    @Test
    public void test06_3() {
        runAndCheck(dir + "run_06.bds", "r2", 5.0);
    }

    @Test
    public void test39() {
        // Variables: Inheriting from environment
        String home = System.getenv("HOME");
        runAndCheck(dir + "run_39.bds", "home", home);
    }
    @Test
    public void test41() {
        // Global variable 'programName'
        runAndCheck(dir + "run_01.bds", "programName", "run_01.bds");
    }

    @Test
    public void test65() {
        // Variable initialization
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("bsfalse", false);
        expectedValues.put("bstrue", true);
        expectedValues.put("bifalse", false);
        expectedValues.put("bitrue", true);
        expectedValues.put("brfalse", false);
        expectedValues.put("brtrue", true);
        expectedValues.put("blfalse", false);
        expectedValues.put("bltrue", true);
        expectedValues.put("bmfalse", false);
        expectedValues.put("bmtrue", true);
        runAndCheck(dir + "run_65.bds", expectedValues);
    }

    @Test
    public void test85() {
        // Global variables: K, M, G, T, P
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("m", 60L);
        expectedValues.put("h", 3600L);
        expectedValues.put("d", 86400L);
        expectedValues.put("oneK", 1024L);
        expectedValues.put("oneM", 1048576L);
        expectedValues.put("oneG", 1073741824L);
        expectedValues.put("oneT", 1099511627776L);
        expectedValues.put("oneP", 1125899906842624L);
        runAndCheck(dir + "run_85.bds", expectedValues);
    }
    @Test
    public void test101() {
        // Variables: Multiple assignment
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("a", 1);
        expectedValues.put("b", 3);
        expectedValues.put("c", 5);

        runAndCheck(dir + "run_101.bds", expectedValues);
    }

    @Test
    public void test102() {
        // Variables: Multiple assignment
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("a", 1);
        expectedValues.put("b", 3);
        expectedValues.put("c", 5);
        expectedValues.put("d", 1);

        runAndCheck(dir + "run_102.bds", expectedValues);
    }

    @Test
    public void test200() {
        // Variable: Assign a function
        runAndCheck(dir + "run_200.bds", "z", "(int) -> int");
    }

    @Test
    public void test278AutoCasting() {
        // Variables: Casting and automatic type casts
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("outs1", "s1 = '5', type: string");
        expectedValues.put("outs2", "s2 = 'true', type: string");
        expectedValues.put("outi", "i = true, type: bool");
        runAndCheck(1, dir + "run_278_auto_casting.bds", expectedValues);
    }

    @Test
    public void test278TypeAny() {
        // Variables: type 'any'
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("outz1", "z1 = 5, type: int");
        expectedValues.put("outz2", "z2 = hi, type: string");
        expectedValues.put("outz3", "z3 = 1.234, type: real");
        expectedValues.put("outz4", "z4 = true, type: bool");
        runAndCheck(1, dir + "run_279_type_any.bds", expectedValues);
    }
}
