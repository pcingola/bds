package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for language & compilation
 * <p>
 * Note: These test cases just check language parsing and compilation (what is supposed to compile OK, and what is not).
 *
 * @author pcingola
 */
public class TestCasesLang extends TestCasesBase {

    public TestCasesLang() {
        dir = "test/lang/";
    }

    @Test
    public void test00() {
        // EOF test: No '\n' or ';' at the end of last line
        compileOk(dir + "test00.bds");
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
        String errs = "ERROR [ file 'test/lang/test08.bds', line 11 ] :	Only variable reference can be used with ++ or -- operators\n";
        compileErrors(dir + "test08.bds", errs);
    }

    @Test
    public void test09() {
        // For loop
        compileOk(dir + "test09.bds");
    }

    @Test
    public void test10() {
        // Compile error: Undefined variable, direct reference
        String err = "ERROR [ file 'test/lang/test10.bds', line 2 ] :	Symbol 'j' cannot be resolved\n";
        compileErrors(dir + "test10.bds", err);
    }

    @Test
    public void test11() {
        // Compile error: Undefined variable, within expression
        String err = "ERROR [ file 'test/lang/test11.bds', line 2 ] :	Symbol 'j' cannot be resolved\n";
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
    public void test14() {
        // For loop using an undefined variable in 'for' definition
        String errs = "ERROR [ file 'test/lang/test14.bds', line 3 ] :	Symbol 'i' cannot be resolved\n"//
                + "ERROR [ file 'test/lang/test14.bds', line 4 ] :	Symbol 'i' cannot be resolved\n";

        compileErrors(dir + "test14.bds", errs);
    }

    @Test
    public void test15() {
        // For loop using an undefined variable in loop block
        String errs = "ERROR [ file 'test/lang/test15.bds', line 4 ] :	Symbol 'j' cannot be resolved\n";
        compileErrors(dir + "test15.bds", errs);
    }

    @Test
    public void test16() {
        // For loop with variables defined in loop block
        compileOk(dir + "test16.bds");
    }

    @Test
    public void test17() {
        // Function definition and function call
        compileOk(dir + "test17.bds");
    }

    @Test
    public void test18() {
        // Function definition with default value
        compileOk(dir + "test18.bds");
    }

    @Test
    public void test19() {
        // Duplicate variable definition
        String errs = "ERROR [ file 'test/lang/test19.bds', line 4 ] :	Duplicate local name 'i'\n";
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
    public void test22() {
        // Function definition within function
        compileOk(dir + "test22.bds");
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
    public void test25() {
        // Multi-line strings
        compileOk(dir + "test25.bds");
    }

    @Test
    public void test26() {
        // sys statements, parsing escaped characters and multi-line sys statements
        compileOk(dir + "test26.bds");
    }

    @Test
    public void test27() {
        // Type casting error: real to int
        String errs = "ERROR [ file 'test/lang/test27.bds', line 2 ] :	Cannot cast real to int\n";
        compileErrors(dir + "test27.bds", errs);
    }

    @Test
    public void test28() {
        // For loop using multiple pre and post statements
        compileOk(dir + "test28.bds");
    }

    @Test
    public void test29() {
        // For loops with non-bool condition
        String errs = "ERROR [ file 'test/lang/test29.bds', line 3 ] :	For loop condition must be a bool expression\n";
        compileErrors(dir + "test29.bds", errs);
    }

    @Test
    public void test30() {
        // Function definition: Wrong casting in 'return' statement
        String errs = "ERROR [ file 'test/lang/test30.bds', line 4 ] :	Cannot cast real to int\n";
        compileErrors(dir + "test30.bds", errs);
    }

    @Test
    public void test31() {
        // Function definition: Missing return statement
        String errs = "ERROR [ file 'test/lang/test31.bds', line 4 ] :	Function has no return statement\n";
        compileErrors(dir + "test31.bds", errs);
    }

    @Test
    public void test32() {
        // Task definition
        compileOk(dir + "test32.bds");
    }

    @Test
    public void test33() {
        // Task definition: Improper tasks
        compileOk(dir + "test33.bds");
    }

    @Test
    public void test34() {
        // Function call: Undefined function
        String errs = "ERROR [ file 'test/lang/test34.bds', line 5 ] :	Function f(int) cannot be resolved\n";
        compileErrors(dir + "test34.bds", errs);
    }

    @Test
    public void test35() {
        // String interpolation
        compileOk(dir + "test35.bds");
    }

    @Test
    public void test36() {
        // String interpolation: Missing variable
        String errs = "ERROR [ file 'test/lang/test36.bds', line 3 ] :	Symbol 'j' cannot be resolved\n";
        compileErrors(dir + "test36.bds", errs);
    }

    @Test
    public void test37() {
        // List definition: List literal and missing variable
        String errs = "ERROR [ file 'test/lang/test37.bds', line 16 ] :	Symbol 'fruit' cannot be resolved\n";
        compileErrors(dir + "test37.bds", errs);
    }

    @Test
    public void test38() {
        // For loop: Iterating over a list
        String errs = "ERROR [ file 'test/lang/test38.bds', line 6 ] :	Cannot cast string to int\n";
        compileErrors(dir + "test38.bds", errs);
    }

    @Test
    public void test39() {
        // List: Incorrect type assignment
        String errs = "ERROR [ file 'test/lang/test39.bds', line 6 ] :	Cannot cast string to int\n";
        compileErrors(dir + "test39.bds", errs);
    }

    @Test
    public void test40() {
        // List: Push method with wrong type
        String errs = "ERROR [ file 'test/lang/test40.bds', line 6 ] :	Method int[].push(string) cannot be resolved\n";
        compileErrors(dir + "test40.bds", errs);
    }

    @Test
    public void test41() {
        // Task definition syntax
        compileOk(dir + "test41.bds");
    }

    @Test
    public void test42() {
        // Includes
        compileOk(dir + "test42.bds");
    }

    @Test
    public void test43() {
        // Variable type void not allowed (assignment via function)
        String errs = "ERROR [ file 'test/lang/test43.bds', line 8 ] :	Cannot declare variable 'res' type 'void'";
        compileErrors(dir + "test43.bds", errs);
    }

    @Test
    public void test44() {
        // List plus operator, wrong type
        String errs = "ERROR [ file 'test/lang/test44.bds', line 2 ] :	Cannot append int[] to string[]";
        compileErrors(dir + "test44.bds", errs);
    }

    @Test
    public void test45() {
        // List plus operator
        compileOk(dir + "test45.bds");
    }

    @Test
    public void test46() {
        // List element access
        compileOk(dir + "test46.bds");
    }

    @Test
    public void test47() {
        // Function has the same name as a variable
        String errs = "ERROR [ file 'test/lang/test47.bds', line 5 ] :	Duplicate local name 'gsea'";
        compileErrors(dir + "test47.bds", errs);
    }

    @Test
    public void test48() {
        // Variable name using a reserved word
        // String errs = "ERROR [ file 'test/lang/test48.bds', line 5 ] :	extraneous input ':=' expecting {<EOF>, 'while', '{', 'void', 'for', 'error', 'debug', 'int', 'include', 'task', '(', 'kill', '\n', 'println', 'exit', '++', '~', 'wait', 'dep', '+', 'goal', 'continue', 'return', ';', 'if', 'warning', 'break', 'print', 'switch', 'parallel', 'par', '[', '--', 'bool', '!', 'string', 'checkpoint', 'breakpoint', '-', 'real', BOOL_LITERAL, INT_LITERAL, REAL_LITERAL, STRING_LITERAL, STRING_LITERAL_SINGLE, HELP_LITERAL, SYS_LITERAL, TASK_LITERAL, ID}";
        String errs = "ERROR [ file 'test/lang/test48.bds', line 5 ] :	extraneous input ':=' expecting ";
        compileErrors(dir + "test48.bds", errs);
    }

    @Test
    public void test49() {
        // Task definition: Using two lines without braces
        String errs = "ERROR [ file 'test/lang/test49.bds', line 4 ] :\tTask has empty statement";
        compileErrors(dir + "test49.bds", errs);
    }

    @Test
    public void test50() {
        // List: Incorrect assignment (trying to assign a value to a list returned from a function)
        String errs = "ERROR [ file 'test/lang/test50.bds', line 6 ] :\tCannot assign to non-variable 'f(  )[0]'";
        compileErrors(dir + "test50.bds", errs);
    }

    @Test
    public void test51() {
        // Map: Incorrect assignment (trying to assign a value to a map returned from a function)
        String errs = "ERROR [ file 'test/lang/test51.bds', line 6 ] :	Cannot assign to non-variable 'f(  ){\"hi\"}'";
        compileErrors(dir + "test51.bds", errs);
    }

    @Test
    public void test52() {
        // Switch statement, 'case' fall-through and 'default'
        compileOk(dir + "test52.bds");
    }

    @Test
    public void test53() {
        // Switch statement, 'default' not at the end
        compileOk(dir + "test53.bds");
    }

    @Test
    public void test54() {
        // Switch statement, Incorrect type in 'case' expression
        String errs = "ERROR [ file 'test/lang/test54.bds', line 9 ] :	Switch expression and case expression types do not match (string vs int): case 7";
        compileErrors(dir + "test54.bds", errs);
    }

    @Test
    public void test55() {
        // Switch: Missing variable
        String errs = "ERROR [ file 'test/lang/test55.bds', line 15 ] :	Symbol 'b' cannot be resolved";
        compileErrors(dir + "test55.bds", errs);
    }

    @Test
    public void test56() {
        // Class: 'new' operator missing class definition
        compileErrors(dir + "test56.bds", "Cannot find class 'A'");
    }

    @Test
    public void test57() {
        // Class: 'new' operator, incorrect class
        compileErrors(dir + "test57.bds", "Cannot cast A to B");
    }

    @Test
    public void test58() {
        // Class definition: Missing variable in methods
        compileErrors(dir + "test58.bds", "Symbol 'out' cannot be resolved");
    }

    @Test
    public void test59() {
        // Task: Using a method name instead of a variable
        compileErrors(dir + "test59.bds", "Expression should be string or string[], got '(A) -> string'");
    }

    @Test
    public void test60() {
        // Include statements: Multiple includes. Include order should not affect compilation
        compileOk(dir + "test60.bds");
    }

    @Test
    public void test61() {
        // Class definition: Class extends another class
        compileOk(dir + "test61.bds");
    }

    @Test
    public void test62() {
        // Class definition: Method inheritance
        compileOk(dir + "test62.bds");
    }

    @Test
    public void test63() {
        // Function definition: Duplicate name for variable and function
        compileErrors(dir + "test63.bds", "ERROR [ file 'test/lang/test63.bds', line 5 ] :	Duplicate local name 'zzz'");
    }

    @Test
    public void test64() {
        // Function definition: Duplicate function names
        compileErrors(dir + "test64.bds", "Duplicate function 'zzz() -> void'");
    }

    @Test
    public void test65() {
        // Class: Method definition, duplicate method name
        compileErrors(dir + "test65.bds", "Duplicate method 'A.zzz(A) -> void'");
    }

    @Test
    public void test66() {
        // Class: Implicit casting objects
        compileOk(dir + "test66.bds");
        compileOk(dir + "test66b.bds");
    }

    @Test
    public void test67() {
        // Class: Explicit casting objects
        compileOk(dir + "test67.bds");
    }

    @Test
    public void test67b() {
        // Class: implicit down-casting error
        compileErrors(dir + "test67b.bds", "Cannot cast A to C");
    }

    @Test
    public void test68() {
        // Switch: Empty statement error
        compileErrors(dir + "test68.bds", "Empty switch statement");
    }

    @Test
    public void test69() {
        // Assign result from 'void' function
        compileErrors(dir + "test69.bds", "Cannot cast void to string[]");
    }

    @Test
    public void test70() {
        // Function: Return different type of array (class)
        compileErrors(dir + "test70.bds", "Cannot cast A[] to B[]");
    }

    @Test
    public void test71_castEmptyListOfObjects() {
        // Initialize with an empty list of objects
        compileOk(dir + "test71.bds");
    }

    @Test
    public void test72_castEmptyMapOfObjects() {
        // Initialize with an empty map of objects
        compileOk(dir + "test72.bds");
    }

    @Test
    public void test73_plus_minus_typo_string() {
        // Unary operator: '+-' does not exits
        //
        // Using '+-' instead of '+=' creates a "This should never happen" error instead of compilation error
        //		$ bds -v test/lang/test73.bds
        //		2022/09/07 08:34:05 Verbose mode set
        //		00:00:00.000	Compile error ExpressionUnaryPlusMinus test/lang/test73.bds:4,3: Cannot cast to 'int' or 'real'. This should never happen!
        //				java.lang.RuntimeException: Compile error ExpressionUnaryPlusMinus test/lang/test73.bds:4,3: Cannot cast to 'int' or 'real'. This should never happen!
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
        //		$ bds -v test/lang/test74.bds
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
