package org.bds.test.unit;

import org.bds.lang.value.Value;
import org.bds.lang.value.ValueObject;
import org.bds.test.BdsTest;
import org.bds.test.TestCasesBase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.bds.libraries.LibraryException.EXCEPTION_FIELD_VALUE;

/**
 * Test cases Classes / Objects
 *
 * @author pcingola
 */
public class TestCasesRun3 extends TestCasesBase {

    public TestCasesRun3() {
        dir = "test/unit/run/";
    }

    @Test
    public void test200() {
        // Variable: Assign a function
        runAndCheck(dir + "run_200.bds", "z", "(int) -> int");
    }

    @Test
    public void test201() {
        // Class: Show object contents
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("z", "{ i: 0, r: 0.0, s:  }");
        expectedValues.put("z2", "{ i: 0, r: 0.0, s:  }");

        runAndCheck(dir + "run_201.bds", expectedValues);
    }

    @Test
    public void test202() {
        // Class: Field assignments
        runAndCheck(dir + "run_202.bds", "z", "{ i: 42, r: 1.234, s: Hi }");
    }

    @Test
    public void test203() {
        // Class: method using field access
        runAndCheck(dir + "run_203.bds", "j", "42");
    }

    @Test
    public void test204() {
        // Class: method using field access, shadowing variable ('this' keyword needed)
        runAndCheck(dir + "run_204.bds", "j", "42");
    }

    @Test
    public void test205() {
        // Class: method using field access, shadowing variable ('this' keyword needed)
        runAndCheck(dir + "run_205.bds", "z", null);
    }

    @Test
    public void test206() {
        // Class: Field is list, access to list element
        runAndCheck(dir + "run_206.bds", "j", "44");
    }

    @Test
    public void test207() {
        // Class: Field is map, static initialization (literal map), access to map element
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("j", "42");
        expectedValues.put("s", "bye");
        expectedValues.put("s2", "chau");
        runAndCheck(dir + "run_207.bds", expectedValues);
    }

    @Test
    public void test208() {
        // Class: Field list and is map, static initialization, field access
        runAndCheck(dir + "run_208.bds", "z", "{ i: 7, l: [one, dos, three], m: { one => uno, three => tres, two => deux } }");
    }

    @Test
    public void test209() {
        // Class: Field is access
        runAndCheckStderr(dir + "run_209.bds", "Null pointer. Trying to access field 'i' in null object.");
    }

    @Test
    public void test210() {
        // Class: Invoke method on un-initialized object
        runAndCheckStderr(dir + "run_210.bds", "Null pointer: Cannot call method 'Zzz.set(Zzz,int) -> void' on null object.");
    }

    @Test
    public void test211() {
        // Class: Object's initializer method
        runAndCheck(dir + "run_211.bds", "z", "{ i: 7 }");
    }

    @Test
    public void test212() {
        // Class: Object's initializer method + setter
        runAndCheck(dir + "run_212.bds", "z", "{ i: 42 }");
    }

    @Test
    public void test213() {
        // Class: Multiple initializer methods
        runAndCheck(dir + "run_213.bds", "z", "{ i: 7 }");
    }

    @Test
    public void test214() {
        // Class: Multiple initializer methods
        runAndCheck(dir + "run_214.bds", "z", "{ i: 42 }");
    }

    @Test
    public void test215() {
        // Class: Extending classes
        runAndCheck(dir + "run_215.bds", "z", "{ i: 42, j: 7 }");
    }

    @Test
    public void test216() {
        // Class: Extending classes, inherited fields
        runAndCheck(dir + "run_216.bds", "z", "{ i: 21, j: 17, next: { i: 42, next: null } }");
    }

    @Test
    public void test217() {
        // Class: Extending classes, inherited methods
        runAndCheck(dir + "run_217.bds", "x", "43");
    }

    @Test
    public void test218() {
        // Class: Extending classes, inherited methods
        runAndCheck(dir + "run_218.bds", "x", "50");
    }

    @Test
    public void test219() {
        // Class: Extending classes, methods
        runAndCheck(dir + "run_219.bds", "x", "50");
    }

    @Test
    public void test220() {
        // Class: Extending classes, inherited methods
        runAndCheck(dir + "run_220.bds", "x", "50");
    }

    @Test
    public void test221() {
        // Class: Extending classes, overriding methods
        runAndCheck(dir + "run_221.bds", "z", "46");
    }

    @Test
    public void test222() {
        // Class: Casting extended classes
        runAndCheck(dir + "run_222.bds", "z", "7");
    }

    @Test
    public void test223_list_of_list() {
        // List of lists
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l", "[[hi, bye], [hola, adios]]");
        expectedValues.put("typel", "string[][]");
        expectedValues.put("typel0", "string[]");

        runAndCheck(dir + "run_223.bds", expectedValues);
    }

    @Test
    public void test224_map_of_lists() {
        // Map of lists
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("m", "{ en => [hi, bye, hello], sp => [hola, adios] }");
        expectedValues.put("typem", "string[]{string}");
        expectedValues.put("typemen", "string[]");

        runAndCheck(dir + "run_224.bds", expectedValues);
    }

    @Test
    public void test225_super() {
        // Class: super method invocation
        runAndCheck(dir + "run_225.bds", "ret", "2");
    }

    @Test
    public void test226_refref() {
        // Class: field of field reference
        runAndCheck(dir + "run_226.bds", "ret", "42");
    }

    @Test
    public void test227_refref() {
        // Class: local variable (class) reference
        runAndCheck(dir + "run_227.bds", "ret", "42");
    }

    @Test
    public void test228_method_call() {
        // Class: Method invocation in subclass
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("ret1", "1");
        expectedValues.put("ret2", "2");

        runAndCheck(dir + "run_228.bds", expectedValues);
    }

    @Test
    public void test229_super() {
        // Class: super in constructor
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("af", "1");
        expectedValues.put("ag", "2");
        expectedValues.put("ax", "41");
        expectedValues.put("bf", "11");
        expectedValues.put("bg", "12");
        expectedValues.put("bx", "42");

        runAndCheck(dir + "run_229.bds", expectedValues);
    }

    @Test
    public void test230_tryCatch() {
        // Try/Catch/Finally
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "false");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "run_230.bds", expectedValues);
    }

    @Test
    public void test231_tryCatch() {
        // Try/Catch/Finally: Throw exception within try
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck(dir + "run_231.bds", expectedValues);
    }

    @Test
    public void test232_tryCatch() {
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
    public void test233_tryCatch() {
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
    public void test234_tryCatch() {
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
    public void test235_tryCatch() {
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
    public void test235_tryCatch_rev() {
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
    public void test236_tryCatch() {
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
    public void test237_tryCatch() {
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
    public void test238_tryCatch() {
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
    public void test239_tryCatch() {
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
    public void test240_tryCatch() {
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
    public void test241_tryCatch() {
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
    public void test242_derivedMethodParamNames() {
        // Class: Overriding method, different parameter names
        runAndCheck(dir + "run_242.bds", "ret", "n:hi");
    }

    @Test
    public void test243_downCasting() {
        // Class: Down-casting
        runAndCheck(dir + "run_243.bds", "ret", "42");
    }

    @Test
    public void test244_concurrent_modification() {
        // For loop List:ConcurrentModificationException
        runAndCheckException(dir + "run_244.bds", "ConcurrentModificationException");
    }

    @Test
    public void test245NegativeIndex() {
        // List: Negative index
        runAndCheck(dir + "run_245.bds", "ret", "5");
    }

    @Test
    public void test246NegativeIndex() {
        // List: Negative index
        runAndCheck(dir + "run_246.bds", "ret", "1");
    }

    @Test
    public void test247NegativeIndex() {
        // List: Negative index
        runAndCheck(dir + "run_247.bds", "ret", "5");
    }

    @Test
    public void test248_out_of_bounds() {
        // List: out of bounds index
        runAndCheck(dir + "run_248.bds", "ret", "1");
    }

    @Test
    public void test249_concurrent_modification_hash() {
        // For loop Map: ConcurrentModificationException
        runAndCheckException(dir + "run_249.bds", "ConcurrentModificationException");
    }

    @Test
    public void test250_super_super_method_call() {
        // Class: Method override multiple childs
        runAndCheckStdout(dir + "run_250.bds", "GrandParent\nParent\nChild\n");
    }

    @Test
    public void test251_super_super_constructor_call() {
        // Class: Constructor invoke super -> super ...
        runAndCheckStdout(dir + "run_251.bds", "GrandParent\nParent\nChild\n");
    }

    @Test
    public void test252WrongSuperConstructorCall() {
        // Class: Constructor invoke super -> super ...
        compileErrors(dir + "run_252.bds", "WRONG CONSTRUCTOR SOMETHING...");
    }

    @Test
    public void test253_getvar() {
        // Function: getVar()
        HashMap<String, Object> expectedValues = new HashMap<>();

        expectedValues.put("shome", System.getenv().get("HOME"));
        expectedValues.put("bhome", true);

        expectedValues.put("szzz", "");
        expectedValues.put("bzzz", false);

        runAndCheck(dir + "run_253.bds", expectedValues);
    }

    @Test
    public void test254_getvar() {
        // Function: getVar() with default value
        HashMap<String, Object> expectedValues = new HashMap<>();

        expectedValues.put("shome", System.getenv().get("HOME"));
        expectedValues.put("szzz", "VALUE_DEFAULT_2");
        expectedValues.put("szzzxxxzzz", "VALUE_DEFAULT_3");

        runAndCheck(dir + "run_254.bds", expectedValues);
    }

    @Test
    public void test255_getModuleName() {
        // Function: getModulePath()
        runAndCheck(dir + "run_255.bds", "b", "run_255.bds");
    }

    @Test
    public void test256_getModuleName() {
        // Function: getModulePath()
        runAndCheck(dir + "run_256.bds", "db", "run");
    }

    @Test
    public void test257_randIntDivisionByZero() {
        // Function: randInt(0)
        runAndCheck(dir + "run_257.bds", "r", "0");
    }

    @Test
    public void test258_infiniteRecursionPrint() {
        // Class: Class includes a reference to a list of the same type of the class
        runOk(dir + "run_258.bds");
    }

    @Test
    public void test259_infiniteRecursionFor() {
        // Class: Class includes a reference to a list of the same type of the class
        runOk(dir + "run_259.bds");
    }

    @Test
    public void test260_emptyListIndex() {
        // List: Invalid index from empty list
        runAndCheckError(dir + "run_260.bds", "Runtime error StatementExpr test/unit/run/run_260.bds:4,1: Cannot get element '0' from an empty list");
    }

    @Test
    public void test261_invalidKey() {
        // Map: Invalid index
        runAndCheckError(dir + "run_261.bds", "Invalid key 'hi' in map.");
    }

    @Test
    public void test262_simpleClassCast() {
        // Class: casting
        runOk(dir + "run_262.bds");
    }

    @Test
    public void test263_invokeMethodOnNullObject() {
        // Class: Invoke a method on a null object
        runAndCheckError(dir + "run_263.bds", "Null pointer: Invoking method 'z' on null object type 'Z', signature z(Z this) -> void");
    }

    @Test
    public void test264_typeOfClass() {
        // Class: type() for object
        runAndCheck(dir + "run_264.bds", "objType", "A");
    }

    @Test
    public void test265_try_catch_parent_exception_class() {
        // Try/Catch: Catching a derived exception type
        runAndCheck(dir + "run_265.bds", "out", "try_start catch finally");
    }

    @Test
    public void test266_try_catch_exception_class() {
        // Try/Catch: Catching a derived exception type
        runAndCheck(dir + "run_266.bds", "out", "try_start catch finally");
    }

    @Test
    public void test267_try_catch_exception_class_defined_after() {
        // Try/Catch: Catching a derived exception type defined after the try/catch statement
        runOk(dir + "run_267.bds");
    }

    @Test
    public void test268_try_catch_nested() {
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
    public void test269_rmOnExit() {
        // File: rmOnExit()
        var tmpFile = new File("/tmp/run_269.tmp");
        var txtFile = new File("/tmp/run_269.txt");
        runOk(dir + "run_269.bds");
        Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test270_rmOnExit_error() {
        // File: rmOnExit() on error statement
        var tmpFile = new File("/tmp/run_270.tmp");
        var txtFile = new File("/tmp/run_270.txt");
        runAndCheckExit(dir + "run_270.bds", 1);
        Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test271_rmOnExit_dirWithFiles() {
        // File: rmOnExit() for directories
        runOk(dir + "run_271.bds");

        var tmpDir = new File("/tmp/run_271/rm_on_exit");
        var txtDir = new File("/tmp/run_271/no_rm_on_exit");

        Assert.assertFalse("Tmp dir " + tmpDir + " should not exists", tmpDir.exists());

        Assert.assertTrue("Tmp dir " + txtDir + " should exists", txtDir.exists());
        Assert.assertTrue("Tmp dir " + txtDir + " should be a directory", txtDir.isDirectory());

        for (int i = 0; i < 10; i++) {
            var tmpFile = new File("/tmp/run_271/rm_on_exit/" + i + ".tmp");
            var txtFile = new File("/tmp/run_271/no_rm_on_exit/" + i + ".txt");
            Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
            Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
        }
    }

    @Test
    public void test272_rmOnExitCancel() {
        // File: rmOnExitCancel()
        var tmpFile = new File("/tmp/run_272.tmp");
        var txtFile = new File("/tmp/run_272.txt");
        runOk(dir + "run_272.bds");
        Assert.assertTrue("Tmp file " + tmpFile + " should exist", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test273_rmOnExitCancel_error() {
        // File: rmOnExitCancel()
        var tmpFile = new File("/tmp/run_273.tmp");
        var txtFile = new File("/tmp/run_273.txt");
        runAndCheckExit(dir + "run_273.bds", 1);
        Assert.assertTrue("Tmp file " + tmpFile + " should exist", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test274_rmOnExitCancelList() {
        // File: rmOnExitCancel() with a list of files
        var tmpFile1 = new File("/tmp/run_274.1.tmp");
        var tmpFile2 = new File("/tmp/run_274.2.tmp");
        var tmpFile3 = new File("/tmp/run_274.3.tmp");
        var txtFile = new File("/tmp/run_274.txt");
        runOk(dir + "run_274.bds");
        Assert.assertTrue("Tmp file " + tmpFile1 + " should exist", tmpFile1.exists());
        Assert.assertTrue("Tmp file " + tmpFile2 + " should exist", tmpFile2.exists());
        Assert.assertTrue("Tmp file " + tmpFile3 + " should exist", tmpFile3.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test275_rmOnExitCancelList_error() {
        // File: rmOnExitCancel() with a list of files, error statement forcing exit
        var tmpFile1 = new File("/tmp/run_275.1.tmp");
        var tmpFile2 = new File("/tmp/run_275.2.tmp");
        var tmpFile3 = new File("/tmp/run_275.3.tmp");
        var txtFile = new File("/tmp/run_275.txt");
        runAndCheckExit(dir + "run_275.bds", 1);
        Assert.assertTrue("Tmp file " + tmpFile1 + " should exist", tmpFile1.exists());
        Assert.assertTrue("Tmp file " + tmpFile2 + " should exist", tmpFile2.exists());
        Assert.assertTrue("Tmp file " + tmpFile3 + " should exist", tmpFile3.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
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

    @Test
    public void test277_throw_string() {
        // Try/Catch: Throwing a string instead of an Exception object
        BdsTest bdsTets = runAndCheckException(dir + "run_277.bds", "Exception");
        ValueObject exceptionObject = (ValueObject) bdsTets.getBds().getBdsRun().getVm().getException();
        Value exceptionValue = exceptionObject.getFieldValue(EXCEPTION_FIELD_VALUE);
        Assert.assertEquals("You can also throw a string, but it's a bit weird...", exceptionValue.asString());
    }

    @Test
    public void test278_auto_casting() {
        // Variables: Casting and automatic type casts
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("outs1", "s1 = '5', type: string");
        expectedValues.put("outs2", "s2 = 'true', type: string");
        expectedValues.put("outi", "i = true, type: bool");
        runAndCheck(1, dir + "run_278_auto_casting.bds", expectedValues);
    }

    @Test
    public void test278_type_any() {
        // Variables: type 'any'
        HashMap<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("outz1", "z1 = 5, type: int");
        expectedValues.put("outz2", "z2 = hi, type: string");
        expectedValues.put("outz3", "z3 = 1.234, type: real");
        expectedValues.put("outz4", "z4 = true, type: bool");
        runAndCheck(1, dir + "run_279_type_any.bds", expectedValues);
    }

}