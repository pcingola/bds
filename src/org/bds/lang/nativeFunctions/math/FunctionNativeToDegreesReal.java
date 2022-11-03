package org.bds.lang.nativeFunctions.math;

import org.bds.lang.Parameters;
import org.bds.lang.nativeFunctions.FunctionNative;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeToDegreesReal extends FunctionNative {

	private static final long serialVersionUID = 8900953259997429760L;

	public FunctionNativeToDegreesReal() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "toDegrees";
		returnType = Types.REAL;

		String[] argNames = { "angrad" };
		Type[] argTypes = { Types.REAL };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return Math.toDegrees(bdsThread.getReal("angrad"));
	}
}
