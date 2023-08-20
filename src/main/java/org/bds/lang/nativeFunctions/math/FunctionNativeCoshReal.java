package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeCoshReal extends FunctionNative {

	private static final long serialVersionUID = 935525119202197504L;

	public FunctionNativeCoshReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "cosh";
		returnType = Types.REAL;

		String[] argNames = { "x" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.cosh(bdsThread.getReal("x"));
	}
}
