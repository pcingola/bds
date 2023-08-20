package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeToRadiansReal extends FunctionNative {

	private static final long serialVersionUID = 6024701543339556864L;

	public FunctionNativeToRadiansReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "toRadians";
		returnType = Types.REAL;

		String[] argNames = { "angdeg" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.toRadians(bdsThread.getReal("angdeg"));
	}
}
