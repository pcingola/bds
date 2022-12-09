package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for 'switch' statements
 *
 * @author pcingola
 */
public class TestCasesOperatorsAndMath extends TestCasesBase {

    public TestCasesOperatorsAndMath() {
        dir = "test/unit/run/";
    }

    @Test
    public void test04() {
        // Variable definitions and assignment, int
        // Binary math operators, hex numbers, binary bit operators
        compileOk(dir + "test04.bds");
    }

    @Test
    public void test05() {
        // Variable definitions and assignment, real
        // Binary math operators
        compileOk(dir + "test05.bds");
    }

    @Test
    public void test06() {
        // String addition (concatenation) and multiplication
        compileOk(dir + "test06.bds");
    }

    @Test
    public void test07() {
        // Boolean binary operators
        compileOk(dir + "test07.bds");
    }

    @Test
    public void test08() {
        // Increment and decrement operators
        // Compile error trying to increment an expression
        String errs = "ERROR [ file 'test/unit/lang/test08.bds', line 11 ] :	Only variable reference can be used with ++ or -- operators\n";
        compileErrors(dir + "test08.bds", errs);
    }

    @Test
    public void test73_plus_minus_typo_string() {
        // Unary operator: '+-' does not exits
        //
        // Using '+-' instead of '+=' creates a "This should never happen" error instead of compilation error
        //		$ bds -v test/unit/lang/test73.bds
        //		2022/09/07 08:34:05 Verbose mode set
        //		00:00:00.000	Compile error ExpressionUnaryPlusMinus test/unit/lang/test73.bds:4,3: Cannot cast to 'int' or 'real'. This should never happen!
        //				java.lang.RuntimeException: Compile error ExpressionUnaryPlusMinus test/unit/lang/test73.bds:4,3: Cannot cast to 'int' or 'real'. This should never happen!
        //				at org.bds.BdsLog.compileError(BdsLog.java:21)
        //		at org.bds.BdsLog.compileError(BdsLog.java:14)
        //		at org.bds.lang.expression.ExpressionUnaryPlusMinus.toAsmUnaryMinus(ExpressionUnaryPlusMinus.java:79)
        //		at org.bds.lang.expression.ExpressionUnaryPlusMinus.toAsm(ExpressionUnaryPlusMinus.java:53)
        //		at org.bds.lang.expression.ExpressionBinary.toAsm(ExpressionBinary.java:74)
        //		at org.bds.lang.expression.ExpressionPlus.toAsm(ExpressionPlus.java:91)
        //		at org.bds.lang.expression.ExpressionDelegateBinary.toAsm(ExpressionDelegateBinary.java:67)
        //		at org.bds.lang.statement.StatementExpr.toAsm(StatementExpr.java:31)
        //		at org.bds.lang.ProgramUnit.toAsm(ProgramUnit.java:127)
        //		at org.bds.run.BdsRun.compileAsm(BdsRun.java:165)
        //		at org.bds.run.BdsRun.compile(BdsRun.java:154)
        //		at org.bds.run.BdsRun.runCompile(BdsRun.java:532)
        //		at org.bds.run.BdsRun.run(BdsRun.java:440)
        //		at org.bds.Bds.run(Bds.java:405)
        //		at org.bds.Bds.main(Bds.java:56)
        compileErrors(dir + "test73.bds", "Unary expression '-' unknown return type");
    }

    @Test
    public void test74_plus_minus_typo_string() {
        // Unary operator: '+-' does not exits
        //
        // Using '+-' instead of '+=' creates a null pointer error instead of compilation error
        //		$ bds -v test/unit/lang/test74.bds
        //		2022/09/07 08:34:10 Verbose mode set
        //		java.lang.NullPointerException
        //		at org.bds.lang.expression.ExpressionPlus.toAsm(ExpressionPlus.java:87)
        //		at org.bds.lang.expression.ExpressionDelegateBinary.toAsm(ExpressionDelegateBinary.java:67)
        //		at org.bds.lang.statement.StatementExpr.toAsm(StatementExpr.java:31)
        //		at org.bds.lang.ProgramUnit.toAsm(ProgramUnit.java:127)
        //		at org.bds.run.BdsRun.compileAsm(BdsRun.java:165)
        //		at org.bds.run.BdsRun.compile(BdsRun.java:154)
        //		at org.bds.run.BdsRun.runCompile(BdsRun.java:532)
        //		at org.bds.run.BdsRun.run(BdsRun.java:440)
        //		at org.bds.Bds.run(Bds.java:405)
        //		at org.bds.Bds.main(Bds.java:56)
        compileErrors(dir + "test74.bds", "Unary expression '-' unknown return type");
    }

    @Test
    public void test75_plus_minus_typo_string() {
        // Unary operator: '+-' does not exits
        //
        // Using '+-' instead of '+=' creates a null pointer error instead of compilation error
        compileErrors(dir + "test75.bds", "Unary expression '-' unknown return type");
    }
    @Test
    public void test00_plus_int() {
        // Binary operator: Integer addition
        runAndCheck(dir + "run_00_plus_int.bds", "i", 42L);
    }

    @Test
    public void test00_plus_real() {
        // Binary operator: Real addition
        runAndCheck(dir + "run_00_plus_real.bds", "i", 42.0);
    }

    @Test
    public void test00_plus_string() {
        // Binary operator: string addition (concatenation)
        runAndCheck(dir + "run_00_plus_string.bds", "i", "42 life universe, and everything");
    }

    @Test
    public void test00_minus_int() {
        // Binary operator: Integer subtraction
        runAndCheck(dir + "run_00_minus_int.bds", "i", 42L);
    }

    @Test
    public void test00_minus_real() {
        // Binary operator: Real subtraction
        runAndCheck(dir + "run_00_minus_real.bds", "i", 42.0);
    }

    @Test
    public void test00_div_int() {
        // Binary operator: Integer division
        runAndCheck(dir + "run_00_div_int.bds", "i", 3L);
    }

    @Test
    public void test00_div_real() {
        // Binary operator: Real division
        runAndCheck(dir + "run_00_div_real.bds", "i", 3.3333333333333335);
    }

    @Test
    public void test80() {
        // Binary bit operator: '&'
        runAndCheck(dir + "run_80.bds", "j", 2L);
    }

    @Test
    public void test81() {
        // Binary bit operator: '|'
        runAndCheck(dir + "run_81.bds", "j", 19L);
    }

    @Test
    public void test82() {
        // Operator '+=': String
        runAndCheck(dir + "run_82.bds", "s", "Hi.Bye.");
    }

    @Test
    public void test83() {
        // Operator '+=': String, real
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("i", 3L);
        expectedValues.put("f", 5.85);
        expectedValues.put("s", "hibye");
        expectedValues.put("l", "[hi, bye, world]");
        runAndCheck(dir + "run_83.bds", expectedValues);
    }

    @Test
    public void test98() {
        // Operator '++' and '--' on list element
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("h1", "1");
        expectedValues.put("h2", "3");
        expectedValues.put("h3", "2");
        expectedValues.put("h4", "2");
        expectedValues.put("h5", "1");
        runAndCheck(dir + "run_98.bds", expectedValues);
    }
    @Test
    public void test100() {
        // Operator: Ternary
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("s", 1);
        expectedValues.put("s2", -1);

        runAndCheck(dir + "run_100.bds", expectedValues);
    }
    @Test
    public void test138() {
        // Math functions: cos(), acos()
        runAndCheck(dir + "run_138.bds", "hasErr", "false");
    }

    @Test
    public void test139() {
        // Math functions: sin(), asin()
        runAndCheck(dir + "run_139.bds", "hasErr", "false");
    }

    @Test
    public void test149_div() {
        // Math operators: Real and int division
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("val0", 50);
        expectedValues.put("val1", 50.0);
        expectedValues.put("val2", 50.0);
        expectedValues.put("val3", 50.0);

        runAndCheck(dir + "run_149.bds", expectedValues);
    }

    @Test
    public void test150_mult() {
        // Math operators: Real and int multiplication
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("val0", 200);
        expectedValues.put("val1", 200.0);
        expectedValues.put("val2", 200.0);
        expectedValues.put("val3", 200.0);

        runAndCheck(dir + "run_150.bds", expectedValues);
    }

    @Test
    public void test151_plus() {
        // Math operators: Real and int addition
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("val0", 102);
        expectedValues.put("val1", 102.0);
        expectedValues.put("val2", 102.0);
        expectedValues.put("val3", 102.0);

        runAndCheck(dir + "run_151.bds", expectedValues);
    }

    @Test
    public void test152_minus() {
        // Math operators: Real and int substraction
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("val0", 98);
        expectedValues.put("val1", 98.0);
        expectedValues.put("val2", 98.0);
        expectedValues.put("val3", 98.0);

        runAndCheck(dir + "run_152.bds", expectedValues);
    }


    @Test
    public void test165() {
        // Math: Grouping operators div, mult
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("n1", "500");
        expectedValues.put("n2", "500");
        runAndCheck(dir + "run_165.bds", expectedValues);
    }

    @Test
    public void test167_binary_expression_assign_bool() {
        // Operators: boolean '&=', '|='
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("band1", "true");
        expectedValues.put("band2", "false");
        expectedValues.put("bor1", "true");
        expectedValues.put("bor2", "false");
        runAndCheck(dir + "run_167.bds", expectedValues);
    }
    @Test
    public void test276_modulo_bug() {
        // Math operators: Modulo test cases
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("a", 0);
        expectedValues.put("b", 0);
        expectedValues.put("c", 17);
        expectedValues.put("d", 17);
        expectedValues.put("e", 1);
        expectedValues.put("f", 0);
        runAndCheck(dir + "run_276.bds", expectedValues);
    }


}
