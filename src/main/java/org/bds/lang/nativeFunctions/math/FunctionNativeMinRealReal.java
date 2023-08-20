package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeMinRealReal extends FunctionNative {

	private static final long serialVersionUID = 8395755079663124480L;

	public FunctionNativeMinRealReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "min";
		returnType = Types.REAL;

		String[] argNames = { "a", "b" };
		Type[] argTypes = { Types.REAL, Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.min(bdsThread.getReal("a"), bdsThread.getReal("b"));
	}
}
