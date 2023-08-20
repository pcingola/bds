package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeMaxRealReal extends FunctionNative {

	private static final long serialVersionUID = 7177585859972071424L;

	public FunctionNativeMaxRealReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "max";
		returnType = Types.REAL;

		String[] argNames = { "a", "b" };
		Type[] argTypes = { Types.REAL, Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.max(bdsThread.getReal("a"), bdsThread.getReal("b"));
	}
}
