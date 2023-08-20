package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.compile.BdsCompilerExpression;
import org.bds.lang.expression.Expression;
import org.bds.lang.value.InterpolateVars;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.bds.util.GprString;
import org.junit.Test;

/**
 * Quick test cases when creating a new feature...
 *
 * @author pcingola
 */
public class TestCasesInterpolate extends TestCasesBase {

    @Test
    public void test00() {
        String[] strings = {"Hello $i"};
        String[] vars = {""};

        checkInterpolate("Hello \\$i", strings, vars);
    }

    @Test
    public void test01() {
        String[] strings = {"Hello "};
        String[] vars = {"i"};

        checkInterpolate("Hello $i", strings, vars);
    }

    @Test
    public void test02() {
        String[] strings = {"Hello ", " "};
        String[] vars = {"i", "j"};

        checkInterpolate("Hello $i $j", strings, vars);
    }

    @Test
    public void test03() {
        String[] strings = {"Hello ", ""};
        String[] vars = {"i", "j"};

        checkInterpolate("Hello $i$j", strings, vars);
    }

    @Test
    public void test04() {
        String[] strings = {"l[1] : "};
        String[] vars = {"l[1]"};

        checkInterpolate("l[1] : $l[1]", strings, vars);
    }

    @Test
    public void test05() {
        String[] strings = {"m{'Helo'} : "};
        String[] vars = {"m{\"Helo\"}"};

        checkInterpolate("m{'Helo'} : $m{'Helo'}", strings, vars);
    }

    @Test
    public void test06() {
        String[] strings = {"m{'Helo'} : "};
        String[] vars = {"m{l[i]}"};

        checkInterpolate("m{'Helo'} : $m{$l[$i]}", strings, vars);
    }

    @Test
    public void test07() {
        String[] strings = {"Hello $"};
        String[] vars = {""};

        checkInterpolate("Hello $", strings, vars);
    }

    @Test
    public void test08() {
        String[] strings = {"Hello $\n"};
        String[] vars = {""};

        checkInterpolate("Hello $\n", strings, vars);
    }

    @Test
    public void test09() {
        String[] strings = {"m{'Helo'} : "};
        String[] vars = {"m{s}"};

        checkInterpolate("m{'Helo'} : $m{$s}", strings, vars);
    }

    @Test
    public void test10() {
        String[] strings = {"l[1] : '", "'\\n"};
        String[] vars = {"l[1]", ""};

        checkInterpolate("l[1] : '$l[1]'\\n", strings, vars);
    }

    @Test
    public void test11() {
        String[] strings = {"List with variable index: '", "'\\n"};
        String[] vars = {"l[s]", ""};

        checkInterpolate("List with variable index: '$l[$s]'\\n", strings, vars);
    }

    @Test
    public void test12() {
        String[] strings = {"List with variable index: {", "}\\n"};
        String[] vars = {"l[s]", ""};

        checkInterpolate("List with variable index: {$l[$s]}\\n", strings, vars);
    }

    @Test
    public void test13() {
        String[] strings = {"List with variable index: [", "]\\n"};
        String[] vars = {"l[s]", ""};

        checkInterpolate("List with variable index: [$l[$s]]\\n", strings, vars);
    }

    @Test
    public void test14() {
        String[] strings = {"Map with list and variable index: ",};
        String[] vars = {"m{l[s]}", ""};

        checkInterpolate("Map with list and variable index: $m{$l[$s]}", strings, vars);
    }

    @Test
    public void test15() {
        String[] strings = {"Map with list and variable index: {", "}\\n"};
        String[] vars = {"m{l[s]}", ""};

        checkInterpolate("Map with list and variable index: {$m{$l[$s]}}\\n", strings, vars);
    }

    @Test
    public void test16() {
        String str = "something ending in backslash \\";
        String strAfter = GprString.unescapeDollar(str);

        if (verbose) {
            Gpr.debug("str (before): " + str);
            Gpr.debug("str (after) : " + strAfter);
        }

        Assert.assertEquals("Expected string does not match", str, strAfter);
    }

    @Test
    public void test17() {
        String str = "ending in dollar $";
        String strAfter = GprString.unescapeDollar(str);

        if (verbose) {
            Gpr.debug("str (before): " + str);
            Gpr.debug("str (after) : " + strAfter);
        }

        Assert.assertEquals("Expected string does not match", str, strAfter);
    }

    @Test
    public void test18() {
        String str = "this dolalr sign '\\$VAR' should be escaped";
        String strExpected = "this dolalr sign '$VAR' should be escaped";

        String strAfter = GprString.unescapeDollar(str);
        if (verbose) {
            Gpr.debug("str (before): " + str);
            Gpr.debug("str (after) : " + strAfter);
        }

        Assert.assertEquals("Expected string does not match", strExpected, strAfter);
    }

    @Test
    public void test19() {
        String str = "hello \\t world \\n";
        String strAfter = GprString.unescapeDollar(str);

        if (verbose) {
            Gpr.debug("str (before): " + str);
            Gpr.debug("str (after) : " + strAfter);
        }

        Assert.assertEquals("Expected string does not match", str, strAfter);
    }

    @Test
    public void test20() {

        // We want to execute an inline perl script within a task
        // E.g.:
        //     task perl -e 'use English; print "PID: \$PID\n";'
        //
        // Here $PID is a perl variable and should not be interpreted
        // by bds. We need a way to escape such variables.

        String cmd = "PID: \\$PID";
        InterpolateVars intVars = new InterpolateVars(null, null);
        boolean parsed = intVars.parse(cmd);

        if (verbose) Gpr.debug("cmd: '" + cmd + "'");
        Assert.assertFalse("This string should not be parsed by interpolation", parsed);
    }

    @Test
    public void test21() {
        String[] strings = {"this is string interpolation\\n\\tint i = ", " and str = \"", "\" and both "};
        String[] vars = {"i", "str", "str", "i"};

        checkInterpolate("this is string interpolation\\n\\tint i = $i and str = \"$str\" and both $str$i", strings, vars);
    }

    @Test
    public void test22() {
        String[] strings = {"this is string interpolation: int i = ", " and str = \\\"", "\\\" and both "};
        String[] vars = {"i", "str", "str", "i"};

        checkInterpolate("this is string interpolation: int i = $i and str = \\\"$str\\\" and both $str$i", strings, vars);
    }

    @Test
    public void test23() {
        String[] strings = {"Hello "};
        String[] vars = {"a.x"};

        checkInterpolate("Hello $a.x", strings, vars);
    }

    @Test
    public void test24() {
        String exprStr = "1 + 20 * 2";
        BdsCompilerExpression be = new BdsCompilerExpression("nofile", exprStr);
        Expression expr = be.compileExpr();
        if (verbose) Gpr.debug("expr: " + expr);
        Assert.assertEquals(exprStr, expr.toString());
    }

    @Test
    public void test25() {
        String[] strings = {"Hello "};
        String[] vars = {"a.x.z[56]"};

        checkInterpolate("Hello $a.x.z[56]", strings, vars);
    }

    @Test
    public void test26() {
        String[] strings = {"Hello "};
        String[] vars = {"a.x.z[56]{\"hi\"}"};

        checkInterpolate("Hello $a.x.z[56]{'hi'}", strings, vars);
    }

}
