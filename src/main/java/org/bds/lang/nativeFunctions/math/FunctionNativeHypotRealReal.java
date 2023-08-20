package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeHypotRealReal extends FunctionNative {

	private static final long serialVersionUID = 4006236080096444416L;

	public FunctionNativeHypotRealReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "hypot";
		returnType = Types.REAL;

		String[] argNames = { "x", "y" };
		Type[] argTypes = { Types.REAL, Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.hypot(bdsThread.getReal("x"), bdsThread.getReal("y"));
	}
}
