package org.bds.test.unit;

import org.bds.test.TestCasesBase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for Class definitions, methods, etc.
 *
 * @author pcingola
 */
public class TestCasesClass extends TestCasesBase {

    public TestCasesClass() {
        dir = "test/unit/class/";
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
    public void test161() {
        // Class: field access
        runAndCheck(dir + "run_161.bds", "out", "a.x = 42");
    }

    @Test
    public void test162() {
        // Class: Method in derived class
        runAndCheck(dir + "run_162.bds", "out", "B: A: Hi");
    }

    @Test
    public void test163() {
        // Class: Defining a class inside a class, shadowing class names
        runAndCheck(dir + "run_163.bds", "out", "B: A: Hi");
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
    public void test225Super() {
        // Class: super method invocation
        runAndCheck(dir + "run_225.bds", "ret", "2");
    }

    @Test
    public void test226Refref() {
        // Class: field of field reference
        runAndCheck(dir + "run_226.bds", "ret", "42");
    }

    @Test
    public void test227Refref() {
        // Class: local variable (class) reference
        runAndCheck(dir + "run_227.bds", "ret", "42");
    }

    @Test
    public void test228MethodCall() {
        // Class: Method invocation in subclass
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("ret1", "1");
        expectedValues.put("ret2", "2");

        runAndCheck(dir + "run_228.bds", expectedValues);
    }

    @Test
    public void test229Super() {
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
    public void test242DerivedMethodParamNames() {
        // Class: Overriding method, different parameter names
        runAndCheck(dir + "run_242.bds", "ret", "n:hi");
    }

    @Test
    public void test243DownCasting() {
        // Class: Down-casting
        runAndCheck(dir + "run_243.bds", "ret", "42");
    }


    @Test
    public void test250SuperSuperMethodCall() {
        // Class: Method override multiple childs
        runAndCheckStdout(dir + "run_250.bds", "GrandParent\nParent\nChild\n");
    }

    @Test
    public void test251SuperSuperConstructorCall() {
        // Class: Constructor invoke super -> super ...
        runAndCheckStdout(dir + "run_251.bds", "GrandParent\nParent\nChild\n");
    }

    @Test
    public void test252WrongSuperConstructorCall() {
        // Class: Constructor invoke super -> super ...
        compileErrors(dir + "run_252.bds", "WRONG CONSTRUCTOR SOMETHING...");
    }

    @Test
    public void test258InfiniteRecursionPrint() {
        // Class: Class includes a reference to a list of the same type of the class
        runOk(dir + "run_258.bds");
    }

    @Test
    public void test259InfiniteRecursionFor() {
        // Class: Class includes a reference to a list of the same type of the class
        runOk(dir + "run_259.bds");
    }

    @Test
    public void test262SimpleClassCast() {
        // Class: casting
        runOk(dir + "run_262.bds");
    }

    @Test
    public void test263InvokeMethodOnNullObject() {
        // Class: Invoke a method on a null object
        runAndCheckError(dir + "run_263.bds", "Null pointer: Invoking method 'z' on null object type 'Z', signature z(Z this) -> void");
    }

    @Test
    public void test264TypeOfClass() {
        // Class: type() for object
        runAndCheck(dir + "run_264.bds", "objType", "A");
    }
}
