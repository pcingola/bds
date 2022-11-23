package org.bds.lang.nativeFunctions;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeAssertStringNoMsg extends FunctionNativeAssert {

    private static final long serialVersionUID = 5665572123398144000L;


    public FunctionNativeAssertStringNoMsg() {
        super();
    }

    @Override
    protected void initFunction() {
        functionName = "assert";
        returnType = Types.BOOL;

        String[] argNames = {"expected", "map"};
        Type[] argTypes = {Types.STRING, Types.STRING};
        parameters = Parameters.get(argTypes, argNames);
        addNativeFunction();
    }

    /**
     * Return null if assertion succeeds.
     * If assertion fails, return the assertion failed message to be shown on STDERR when bds exits
     */
    @Override
    protected Object runFunctionNative(BdsThread bdsThread) {
        String expected = bdsThread.getString("expected");
        String value = bdsThread.getString("map");
        return expected.equals(value) ? null : "Expecting '" + expected + "', but was '" + value + "'";
    }
}
