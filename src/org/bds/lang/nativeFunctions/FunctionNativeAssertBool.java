package org.bds.lang.nativeFunctions;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeAssertBool extends FunctionNativeAssert {

    private static final long serialVersionUID = 1197608743773175808L;

    public FunctionNativeAssertBool() {
        super();
    }

    @Override
    protected void initFunction() {
        functionName = "assert";
        returnType = Types.BOOL;

        String[] argNames = {"msg", "cond"};
        Type[] argTypes = {Types.STRING, Types.BOOL};
        parameters = Parameters.get(argTypes, argNames);
        addNativeFunction();
    }

    /**
     * Return null if assertion succeeds.
     * If assertion fails, return the assertion failed message to be shown on STDERR when bds exits
     */
    @Override
    protected Object runFunctionNative(BdsThread bdsThread) {
        return bdsThread.getBool("cond") ? null : bdsThread.getString("msg");
    }
}
