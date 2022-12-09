package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

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

}
