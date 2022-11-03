package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeTanhReal extends FunctionNative {

	private static final long serialVersionUID = 2972204570232520704L;

	public FunctionNativeTanhReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "tanh";
		returnType = Types.REAL;

		String[] argNames = { "x" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.tanh(bdsThread.getReal("x"));
	}
}
