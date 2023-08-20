package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeUlpReal extends FunctionNative {

	private static final long serialVersionUID = 7793435274163290112L;

	public FunctionNativeUlpReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "ulp";
		returnType = Types.REAL;

		String[] argNames = { "a" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.ulp(bdsThread.getReal("d"));
	}
}
