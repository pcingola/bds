package org.bds.test.unit;

import org.bds.lang.value.Value;
import org.bds.lang.value.ValueObject;
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
    public void test230TryCatch() {
        // Try/Catch/Finally
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "false");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "run_230.bds", expectedValues);
    }

    @Test
    public void test231TryCatch() {
        // Try/Catch/Finally: Throw exception within try
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "run_231.bds", expectedValues);
    }

    @Test
    public void test232TryCatch() {
        // Try/Catch/Finally: Throw exception within function
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f1", "true");
        expectedValues.put("f2", "false");
        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "run_232.bds", expectedValues);
    }

    @Test
    public void test233TryCatch() {
        // Try/Catch/Finally: Throw exception with multiple catch clauses within a function
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f1", "true");
        expectedValues.put("f2", "true");
        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "run_233.bds", expectedValues);
    }

    @Test
    public void test234TryCatch() {
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

        runAndCheck(dir + "run_234.bds", expectedValues);
    }

    @Test
    public void test235TryCatch() {
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

        runAndCheck(dir + "run_235.bds", expectedValues);
    }

    @Test
    public void test235TryCatchRev() {
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

        runAndCheck(dir + "run_235_rev.bds", expectedValues);
    }

    @Test
    public void test236TryCatch() {
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

        runAndCheck(dir + "run_236.bds", expectedValues);
    }

    @Test
    public void test237TryCatch() {
        // Try/Catch/Finally: Catching exceptions (no finally clause)
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "true");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");

        runAndCheck(dir + "run_237.bds", expectedValues);
    }

    @Test
    public void test238TryCatch() {
        // Try/Catch/Finally: Finally clause without any catch clause
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "true");

        runAndCheck(1, dir + "run_238.bds", expectedValues);
    }

    @Test
    public void test239TryCatch() {
        // Try/Catch/Finally: Finally clause throw an exc exception
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(1, dir + "run_239.bds", expectedValues);
    }

    @Test
    public void test240TryCatch() {
        // Try/Catch/Finally: Finally clause without any catch clause invokes a function that throws an exception
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(1, dir + "run_240.bds", expectedValues);
    }

    @Test
    public void test241TryCatch() {
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

        runAndCheck(1, dir + "run_241.bds", expectedValues);
    }
    @Test
    public void test265TryCatchParentExceptionClass() {
        // Try/Catch: Catching a derived exception type
        runAndCheck(dir + "run_265.bds", "out", "try_start catch finally");
    }

    @Test
    public void test266TryCatchExceptionClass() {
        // Try/Catch: Catching a derived exception type
        runAndCheck(dir + "run_266.bds", "out", "try_start catch finally");
    }

    @Test
    public void test267TryCatchExceptionClassDefinedAfter() {
        // Try/Catch: Catching a derived exception type defined after the try/catch statement
        runOk(dir + "run_267.bds");
    }

    @Test
    public void test268TryCatchNested() {
        // Try/Catch: Catching exceptions in nested try/catch clauses
        var expectedStdout = "START\n" //
                + "TRY 1: Start\n" //
                + "TRY 2: Start\n" //
                + "CATCH 2\n" //
                + "FINALLY 2\n" //
                + "TRY 1: End\n" //
                + "FINALLY 1\n" //
                + "END\n";
        runAndCheckStdout(dir + "run_268.bds", expectedStdout);
    }

    @Test
    public void test277ThrowString() {
        // Try/Catch: Throwing a string instead of an Exception object
        BdsTest bdsTets = runAndCheckException(dir + "run_277.bds", "Exception");
        ValueObject exceptionObject = (ValueObject) bdsTets.getBds().getBdsRun().getVm().getException();
        Value exceptionValue = exceptionObject.getFieldValue(THROWABLE_FIELD_VALUE);
        Assert.assertEquals("You can also throw a string, but it's a bit weird...", exceptionValue.asString());
    }
}
