package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeRintReal extends FunctionNative {

	private static final long serialVersionUID = 6864390105944064000L;

	public FunctionNativeRintReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "rint";
		returnType = Types.REAL;

		String[] argNames = { "a" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.rint(bdsThread.getReal("a"));
	}
}
