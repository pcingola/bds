package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeNextUpReal extends FunctionNative {

	private static final long serialVersionUID = 5673116246338469888L;

	public FunctionNativeNextUpReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "nextUp";
		returnType = Types.REAL;

		String[] argNames = { "d" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.nextUp(bdsThread.getReal("d"));
	}
}
