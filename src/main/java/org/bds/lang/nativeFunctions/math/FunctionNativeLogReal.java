package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeLogReal extends FunctionNative {

	private static final long serialVersionUID = 6232224010063609856L;

	public FunctionNativeLogReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "log";
		returnType = Types.REAL;

		String[] argNames = { "a" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.log(bdsThread.getReal("a"));
	}
}
