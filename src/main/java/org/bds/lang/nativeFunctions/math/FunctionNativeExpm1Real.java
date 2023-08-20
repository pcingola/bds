package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeExpm1Real extends FunctionNative {

	private static final long serialVersionUID = 8510425404329197568L;

	public FunctionNativeExpm1Real() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "expm1";
		returnType = Types.REAL;

		String[] argNames = { "x" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.expm1(bdsThread.getReal("x"));
	}
}
