package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases Classes / Objects
 *
 * @author pcingola
 */
public class TestCasesRun3 extends TestCasesBase {

    @Test
    public void test201() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("z", "{ i: 0, r: 0.0, s:  }");
        expectedValues.put("z2", "{ i: 0, r: 0.0, s:  }");

        runAndCheck("test/run_201.bds", expectedValues);
    }

    @Test
    public void test202() {
        runAndCheck("test/run_202.bds", "z", "{ i: 42, r: 1.234, s: Hi }");
    }

    @Test
    public void test203() {
        runAndCheck("test/run_203.bds", "j", "42");
    }

    @Test
    public void test204() {
        runAndCheck("test/run_204.bds", "j", "42");
    }

    @Test
    public void test205() {
        runAndCheck("test/run_205.bds", "z", null);
    }

    @Test
    public void test206() {
        runAndCheck("test/run_206.bds", "j", "44");
    }

    @Test
    public void test207() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("j", "42");
        expectedValues.put("s", "bye");
        expectedValues.put("s2", "chau");
        runAndCheck("test/run_207.bds", expectedValues);
    }

    @Test
    public void test208() {
        runAndCheck("test/run_208.bds", "z", "{ i: 7, l: [one, dos, three], m: { one => uno, three => tres, two => deux } }");
    }

    @Test
    public void test209() {
        runAndCheckStderr("test/run_209.bds", "Null pointer. Trying to access field 'i' in null object.");
    }

    @Test
    public void test210() {
        runAndCheckStderr("test/run_210.bds", "Null pointer: Cannot call method 'Zzz.set(Zzz,int) -> void' on null object.");
    }

    @Test
    public void test211() {
        runAndCheck("test/run_211.bds", "z", "{ i: 7 }");
    }

    @Test
    public void test212() {
        runAndCheck("test/run_212.bds", "z", "{ i: 42 }");
    }

    @Test
    public void test213() {
        runAndCheck("test/run_213.bds", "z", "{ i: 7 }");
    }

    @Test
    public void test214() {
        runAndCheck("test/run_214.bds", "z", "{ i: 42 }");
    }

    @Test
    public void test215() {
        runAndCheck("test/run_215.bds", "z", "{ i: 42, j: 7 }");
    }

    @Test
    public void test216() {
        runAndCheck("test/run_216.bds", "z", "{ i: 21, j: 17, next: { i: 42, next: null } }");
    }

    @Test
    public void test217() {
        runAndCheck("test/run_217.bds", "x", "43");
    }

    @Test
    public void test218() {
        runAndCheck("test/run_218.bds", "x", "50");
    }

    @Test
    public void test219() {
        runAndCheck("test/run_219.bds", "x", "50");
    }

    @Test
    public void test220() {
        runAndCheck("test/run_220.bds", "x", "50");
    }

    @Test
    public void test221() {
        runAndCheck("test/run_221.bds", "z", "46");
    }

    @Test
    public void test222() {
        runAndCheck("test/run_222.bds", "z", "7");
    }

    @Test
    public void test223_list_of_list() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("l", "[[hi, bye], [hola, adios]]");
        expectedValues.put("typel", "string[][]");
        expectedValues.put("typel0", "string[]");

        runAndCheck("test/run_223.bds", expectedValues);
    }

    @Test
    public void test224_map_of_lists() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("m", "{ en => [hi, bye, hello], sp => [hola, adios] }");
        expectedValues.put("typem", "string[]{string}");
        expectedValues.put("typemen", "string[]");

        runAndCheck("test/run_224.bds", expectedValues);
    }

    @Test
    public void test225_super() {
        runAndCheck("test/run_225.bds", "ret", "2");
    }

    @Test
    public void test226_refref() {
        runAndCheck("test/run_226.bds", "ret", "42");
    }

    @Test
    public void test227_refref() {
        runAndCheck("test/run_227.bds", "ret", "42");
    }

    @Test
    public void test228_method_call() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("ret1", "1");
        expectedValues.put("ret2", "2");

        runAndCheck("test/run_228.bds", expectedValues);
    }

    @Test
    public void test229_super() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("af", "1");
        expectedValues.put("ag", "2");
        expectedValues.put("ax", "41");
        expectedValues.put("bf", "11");
        expectedValues.put("bg", "12");
        expectedValues.put("bx", "42");

        runAndCheck("test/run_229.bds", expectedValues);
    }

    @Test
    public void test230_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "false");
        expectedValues.put("finally1", "true");

        runAndCheck("test/run_230.bds", expectedValues);
    }

    @Test
    public void test231_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try1", "true");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck("test/run_231.bds", expectedValues);
    }

    @Test
    public void test232_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f1", "true");
        expectedValues.put("f2", "false");
        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck("test/run_232.bds", expectedValues);
    }

    @Test
    public void test233_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("f1", "true");
        expectedValues.put("f2", "true");
        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");
        expectedValues.put("finally1", "true");

        runAndCheck("test/run_233.bds", expectedValues);
    }

    @Test
    public void test234_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("try11", "true");
        expectedValues.put("try12", "false");
        expectedValues.put("catch11", "true");
        expectedValues.put("finally11", "true");

        expectedValues.put("try21", "true");
        expectedValues.put("try22", "true");
        expectedValues.put("catch21", "false");
        expectedValues.put("finally21", "true");

        runAndCheck("test/run_234.bds", expectedValues);
    }

    @Test
    public void test235_tryCatch() {
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

        runAndCheck("test/run_235.bds", expectedValues);
    }

    @Test
    public void test235_tryCatch_rev() {
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

        runAndCheck("test/run_235_rev.bds", expectedValues);
    }

    @Test
    public void test236_tryCatch() {
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

        runAndCheck("test/run_236.bds", expectedValues);
    }

    @Test
    public void test237_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "true");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("catch1", "true");

        runAndCheck("test/run_237.bds", expectedValues);
    }

    @Test
    public void test238_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "true");

        runAndCheck(1, "test/run_238.bds", expectedValues);
    }

    @Test
    public void test239_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(1, "test/run_239.bds", expectedValues);
    }

    @Test
    public void test240_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(1, "test/run_240.bds", expectedValues);
    }

    @Test
    public void test241_tryCatch() {
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("main1", "true");
        expectedValues.put("main2", "false");
        expectedValues.put("g1", "true");
        expectedValues.put("g2", "false");

        expectedValues.put("try1", "true");
        expectedValues.put("try2", "false");
        expectedValues.put("finally1", "true");
        expectedValues.put("finally2", "false");

        runAndCheck(1, "test/run_241.bds", expectedValues);
    }

    @Test
    public void test242_derivedMethodParamNames() {
        runAndCheck("test/run_242.bds", "ret", "n:hi");
    }

    @Test
    public void test243_downCasting() {
        runAndCheck("test/run_243.bds", "ret", "42");
    }

    @Test
    public void test244_concurrent_modification() {
        runAndCheckException("test/run_244.bds", "ConcurrentModificationException");
    }

    @Test
    public void test245_out_of_bounds() {
        runAndCheck("test/run_245.bds", "ret", "5");
    }

    @Test
    public void test246_out_of_bounds() {
        runAndCheck("test/run_246.bds", "ret", "1");
    }

    @Test
    public void test247_out_of_bounds() {
        runAndCheck("test/run_247.bds", "ret", "5");
    }

    @Test
    public void test248_out_of_bounds() {
        runAndCheck("test/run_248.bds", "ret", "1");
    }

    @Test
    public void test249_concurrent_modification_hash() {
        runAndCheckException("test/run_249.bds", "ConcurrentModificationException");
    }

    @Test
    public void test250_super_super_method_call() {
        runAndCheckStdout("test/run_250.bds", "GrandParent\nParent\nChild\n");
    }

    @Test
    public void test251_super_super_constructor_call() {
        runAndCheckStdout("test/run_251.bds", "GrandParent\nParent\nChild\n");
    }

    @Test
    public void test253_getvar() {
        HashMap<String, Object> expectedValues = new HashMap<>();

        expectedValues.put("shome", System.getenv().get("HOME"));
        expectedValues.put("bhome", true);

        expectedValues.put("szzz", "");
        expectedValues.put("bzzz", false);

        runAndCheck("test/run_253.bds", expectedValues);
    }

    @Test
    public void test254_getvar() {
        HashMap<String, Object> expectedValues = new HashMap<>();

        expectedValues.put("shome", System.getenv().get("HOME"));
        expectedValues.put("szzz", "VALUE_DEFAULT_2");
        expectedValues.put("szzzxxxzzz", "VALUE_DEFAULT_3");

        runAndCheck("test/run_254.bds", expectedValues);
    }

    @Test
    public void test255_getModuleName() {
        runAndCheck("test/run_255.bds", "b", "run_255.bds");
    }

    @Test
    public void test256_getModuleName() {
        runAndCheck("test/run_256.bds", "db", "test");
    }

    @Test
    public void test257_randIntDivisionByZero() {
        runAndCheck("test/run_257.bds", "r", "0");
    }

    @Test
    public void test258_infiniteRecursionPrint() {
        runOk("test/run_258.bds");
    }

    @Test
    public void test259_infiniteRecursionFor() {
        runOk("test/run_259.bds");
    }

    @Test
    public void test260_emptyListIndex() {
        runAndCheckError("test/run_260.bds", "Runtime error StatementExpr test/run_260.bds:4,1: Cannot get element '0' from an empty list");
    }

    @Test
    public void test261_invalidKey() {
        runAndCheckError("test/run_261.bds", "Invalid key 'hi' in map.");
    }

    @Test
    public void test262_simpleClassCast() {
        runOk("test/run_262.bds");
    }

    @Test
    public void test263_invokeMethodOnNullObject() {
        // Invoke a method on a null object
        runAndCheckError("test/run_263.bds", "Null pointer: Invoking method 'z' on null object type 'Z', signature z(Z this) -> void");
    }

    @Test
    public void test264_typeOfClass() {
        // Invoke a method on a null object
        runAndCheck("test/run_264.bds", "objType", "A");
    }

    @Test
    public void test265_try_catch_parent_exception_class() {
        runAndCheck("test/run_265.bds", "out", "try_start catch finally");
    }

    @Test
    public void test266_try_catch_exception_class() {
        runAndCheck("test/run_266.bds", "out", "try_start catch finally");
    }

    @Test
    public void test267_try_catch_exception_class_defined_after() {
        runOk("test/run_267.bds");
    }

    @Test
    public void test268_try_catch_nested() {
        var expectedStdout = "START\n" //
                + "TRY 1: Start\n" //
                + "TRY 2: Start\n" //
                + "CATCH 2\n" //
                + "FINALLY 2\n" //
                + "TRY 1: End\n" //
                + "FINALLY 1\n" //
                + "END\n";
        runAndCheckStdout("test/run_268.bds", expectedStdout);
    }

    @Test
    public void test269_rmOnExit() {
        var tmpFile = new File("/tmp/run_269.tmp");
        var txtFile = new File("/tmp/run_269.txt");
        runOk("test/run_269.bds");
        Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test270_rmOnExit_error() {
        var tmpFile = new File("/tmp/run_270.tmp");
        var txtFile = new File("/tmp/run_270.txt");
        runAndCheckExit("test/run_270.bds", 1);
        Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test271_rmOnExit_dirWithFiles() {
        runOk("test/run_271.bds");

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
        var tmpFile = new File("/tmp/run_272.tmp");
        var txtFile = new File("/tmp/run_272.txt");
        runOk("test/run_272.bds");
        Assert.assertTrue("Tmp file " + tmpFile + " should exist", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test273_rmOnExitCancel_error() {
        var tmpFile = new File("/tmp/run_273.tmp");
        var txtFile = new File("/tmp/run_273.txt");
        runAndCheckExit("test/run_273.bds", 1);
        Assert.assertTrue("Tmp file " + tmpFile + " should exist", tmpFile.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test274_rmOnExitCancelList() {
        var tmpFile1 = new File("/tmp/run_274.1.tmp");
        var tmpFile2 = new File("/tmp/run_274.2.tmp");
        var tmpFile3 = new File("/tmp/run_274.3.tmp");
        var txtFile = new File("/tmp/run_274.txt");
        runOk("test/run_274.bds");
        Assert.assertTrue("Tmp file " + tmpFile1 + " should exist", tmpFile1.exists());
        Assert.assertTrue("Tmp file " + tmpFile2 + " should exist", tmpFile2.exists());
        Assert.assertTrue("Tmp file " + tmpFile3 + " should exist", tmpFile3.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test275_rmOnExitCancelList_error() {
        var tmpFile1 = new File("/tmp/run_275.1.tmp");
        var tmpFile2 = new File("/tmp/run_275.2.tmp");
        var tmpFile3 = new File("/tmp/run_275.3.tmp");
        var txtFile = new File("/tmp/run_275.txt");
        runAndCheckExit("test/run_275.bds", 1);
        Assert.assertTrue("Tmp file " + tmpFile1 + " should exist", tmpFile1.exists());
        Assert.assertTrue("Tmp file " + tmpFile2 + " should exist", tmpFile2.exists());
        Assert.assertTrue("Tmp file " + tmpFile3 + " should exist", tmpFile3.exists());
        Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }
}