package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeLog1pReal extends FunctionNative {

	private static final long serialVersionUID = 8688334612713209856L;

	public FunctionNativeLog1pReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "log1p";
		returnType = Types.REAL;

		String[] argNames = { "x" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.log1p(bdsThread.getReal("x"));
	}
}
