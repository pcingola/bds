package org.bds.lang.nativeFunctions;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeAssertBoolNoMsg extends FunctionNativeAssert {

    private static final long serialVersionUID = 1586200183371235328L;

    public FunctionNativeAssertBoolNoMsg() {
        super();
    }

    @Override
    protected void initFunction() {
        functionName = "assert";
        returnType = Types.BOOL;

        String[] argNames = {"cond"};
        Type[] argTypes = {Types.BOOL};
        parameters = Parameters.get(argTypes, argNames);
        addNativeFunction();
    }

    /**
     * Return null if assertion succeeds.
     * If assertion fails, return the assertion failed message to be shown on STDERR when bds exits
     */
    @Override
    protected Object runFunctionNative(BdsThread bdsThread) {
        boolean ok = bdsThread.getBool("cond");
        return ok ? null : "Expecting 'true', got 'false'";
    }
}
