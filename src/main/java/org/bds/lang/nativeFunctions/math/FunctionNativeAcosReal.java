package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeAcosReal extends FunctionNative {

	private static final long serialVersionUID = 4045569815794974720L;

	public FunctionNativeAcosReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "acos";
		returnType = Types.REAL;

		String[] argNames = { "a" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.acos(bdsThread.getReal("a"));
	}
}
