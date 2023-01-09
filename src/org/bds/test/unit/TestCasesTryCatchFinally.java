package org.bds.test.unit;

import org.bds.lang.value.Value;
import org.bds.lang.value.ValueObject;
import org.bds.run.BdsThread;
import org.bds.test.BdsTest;
import org.bds.test.TestCasesBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.bds.libraries.LibraryException.THROWABLE_FIELD_VALUE;

/**
 * Test cases for 'try/catch/finally' statements
 *
 * @author pcingola
 */
public class TestCasesTryCatchFinally extends TestCasesBase {

    public TestCasesTryCatchFinally() {
        dir = "test/unit/try_catch_finally/";
    }

    @Test
    public void test00TryCatch() {
        // Try/Catch/Finally
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "false");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "try_catch_finally_00.bds", expectedValues);
    }

    @Test
    public void test01TryCatch() {
        // Try/Catch/Finally: Throw exception within try
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "try_catch_finally_01.bds", expectedValues);
    }

    @Test
    public void test02TryCatch() {
        // Try/Catch/Finally: Throw exception within function
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f1", "true");
        expectedValues.put("f2", "false");
        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "try_catch_finally_02.bds", expectedValues);
    }

    @Test
    public void test03TryCatch() {
        // Try/Catch/Finally: Throw exception with multiple catch clauses within a function
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f1", "true");
        expectedValues.put("f2", "true");
        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "try_catch_finally_03.bds", expectedValues);
    }

    @Test
    public void test04TryCatch() {
        // Try/Catch/Finally: Nestes try/catch
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try11", "true");
        expectedValues.put("try12", "false");
        expectedValues.put("catch11", "true");
        expectedValues.put("finally11", "true");

        expectedValues.put("try21", "true");
        expectedValues.put("try22", "true");
        expectedValues.put("catch21", "false");
        expectedValues.put("finally21", "true");

        runAndCheck(dir + "try_catch_finally_04.bds", expectedValues);
    }

    @Test
    public void test05TryCatch() {
        // Try/Catch/Finally: Nested try/catch in nested functions
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f11", "true");
        expectedValues.put("f12", "true");
        expectedValues.put("f21", "true");
        expectedValues.put("f22", "false");

        expectedValues.put("try11", "true");
        expectedValues.put("try12", "false");
        expectedValues.put("catch11", "true");
        expectedValues.put("finally11", "true");

        expectedValues.put("try21", "true");
        expectedValues.put("try22", "true");
        expectedValues.put("catch21", "false");
        expectedValues.put("finally21", "true");

        runAndCheck(dir + "try_catch_finally_05.bds", expectedValues);
    }

    @Test
    public void test05RevTryCatch() {
        // Try/Catch/Finally: Custom exception classes
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f11", "true");
        expectedValues.put("f12", "false");
        expectedValues.put("f21", "true");
        expectedValues.put("f22", "false");

        expectedValues.put("try11", "true");
        expectedValues.put("try12", "false");
        expectedValues.put("catch11", "false");
        expectedValues.put("finally11", "true");

        expectedValues.put("try21", "true");
        expectedValues.put("try22", "false");
        expectedValues.put("catch21", "true");
        expectedValues.put("finally21", "true");

        runAndCheck(dir + "try_catch_finally_05_rev.bds", expectedValues);
    }

    @Test
    public void test06TryCatch() {
        // Try/Catch/Finally: Custom exception classes, nested try/catch in nested functions
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f11", "true");
        expectedValues.put("f12", "false");
        expectedValues.put("f21", "true");
        expectedValues.put("f22", "false");

        expectedValues.put("try11", "true");
        expectedValues.put("try12", "false");
        expectedValues.put("catch11", "true");
        expectedValues.put("finally11", "true");

        expectedValues.put("try21", "true");
        expectedValues.put("try22", "false");
        expectedValues.put("catch21", "true");
        expectedValues.put("finally21", "true");

        runAndCheck(dir + "try_catch_finally_06.bds", expectedValues);
    }

    @Test
    public void test07TryCatch() {
        // Try/Catch/Finally: Catching exceptions (no finally clause)
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "true");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");

        runAndCheck(dir + "try_catch_finally_07.bds", expectedValues);
    }

    @Test
    public void test08TryCatch() {
        // Try/Catch/Finally: Finally clause without any catch clause
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "true");

        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "try_catch_finally_08.bds", expectedValues);
    }

    @Test
    public void test09TryCatch() {
        // Try/Catch/Finally: Finally clause throw an exc exception
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "try_catch_finally_09.bds", expectedValues);
    }

    @Test
    public void test10TryCatch() {
        // Try/Catch/Finally: Finally clause without any catch clause invokes a function that throws an exception
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "try_catch_finally_10.bds", expectedValues);
    }

    @Test
    public void test11TryCatch() {
        // Try/Catch/Finally: Within a function, finally clause without any catch clause invokes a function that throws an exception
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");
        expectedValues.put("g1", "true");
        expectedValues.put("g2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(BdsThread.EXITCODE_FATAL_ERROR, dir + "try_catch_finally_11.bds", expectedValues);
    }

    @Test
    public void test12TryCatchParentExceptionClass() {
        // Try/Catch: Catching a derived exception type
        runAndCheck(dir + "try_catch_finally_12.bds", "out", "try_start catch finally");
    }

    @Test
    public void test13TryCatchExceptionClass() {
        // Try/Catch: Catching a derived exception type
        runAndCheck(dir + "try_catch_finally_13.bds", "out", "try_start catch finally");
    }

    @Test
    public void test14TryCatchExceptionClassDefinedAfter() {
        // Try/Catch: Catching a derived exception type defined after the try/catch statement
        runOk(dir + "try_catch_finally_14.bds");
    }

    @Test
    public void test15TryCatchNested() {
        // Try/Catch: Catching exceptions in nested try/catch clauses
        var expectedStdout = "START\n" //
                + "TRY 1: Start\n" //
                + "TRY 2: Start\n" //
                + "CATCH 2\n" //
                + "FINALLY 2\n" //
                + "TRY 1: End\n" //
                + "FINALLY 1\n" //
                + "END\n";
        runAndCheckStdout(dir + "try_catch_finally_15.bds", expectedStdout);
    }

    @Test
    public void test16ThrowString() {
        // Try/Catch: Throwing a string instead of an Exception object
        BdsTest bdsTets = runAndCheckException(dir + "try_catch_finally_16.bds", "Exception");
        ValueObject exceptionObject = (ValueObject) bdsTets.getBds().getBdsRun().getVm().getException();
        Value exceptionValue = exceptionObject.getFieldValue(THROWABLE_FIELD_VALUE);
        Assert.assertEquals("You can also throw a string, but it's a bit weird...", exceptionValue.asString());
    }

    @Test
    public void test17TescCaseThrowSimpleException() {
        verbose = debug = true;
        runTestCasesPass(dir + "try_catch_finally_17.bds");
    }

    @Test
    public void test18TescCaseThrowCustomException() {
        verbose = true;
        runTestCasesPass(dir + "try_catch_finally_18.bds");
    }

    @Test
    public void test19TescCaseThrowCustomException() {
        verbose = true;
        runTestCasesPass(dir + "try_catch_finally_19.bds");
    }

    @Test
    public void test20NewCustomException() {
        verbose = true;
        runTestCasesPass(dir + "try_catch_finally_20.bds");
    }

}