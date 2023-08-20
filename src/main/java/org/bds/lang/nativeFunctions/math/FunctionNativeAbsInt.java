package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeAbsInt extends FunctionNative {

	private static final long serialVersionUID = 8364859515887190016L;


	public FunctionNativeAbsInt() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "abs";
		returnType = Types.INT;

		String[] argNames = { "x" };
		Type[] argTypes = { Types.INT };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.abs(bdsThread.getInt("x"));
	}
}
