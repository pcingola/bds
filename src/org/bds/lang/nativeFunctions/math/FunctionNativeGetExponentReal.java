package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeGetExponentReal extends FunctionNative {

	private static final long serialVersionUID = 2541206880559071232L;

	public FunctionNativeGetExponentReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "getExponent";
		returnType = Types.INT;

		String[] argNames = { "d" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return (long) Math.getExponent(bdsThread.getReal("d"));
	}
}
