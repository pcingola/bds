package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeSinhReal extends FunctionNative {

	private static final long serialVersionUID = 7884288383822888960L;

	public FunctionNativeSinhReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "sinh";
		returnType = Types.REAL;

		String[] argNames = { "x" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.sinh(bdsThread.getReal("x"));
	}
}
