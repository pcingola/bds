package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeLog10Real extends FunctionNative {

	private static final long serialVersionUID = 967900161772650496L;

	public FunctionNativeLog10Real() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "log10";
		returnType = Types.REAL;

		String[] argNames = { "a" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.log10(bdsThread.getReal("a"));
	}
}
