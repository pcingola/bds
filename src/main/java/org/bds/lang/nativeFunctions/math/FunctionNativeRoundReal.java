package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeRoundReal extends FunctionNative {

	private static final long serialVersionUID = 6728167491505258496L;

	public FunctionNativeRoundReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "round";
		returnType = Types.INT;

		String[] argNames = { "a" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.round(bdsThread.getReal("a"));
	}
}
