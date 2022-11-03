package org.bds.lang.nativeFunctions;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class FunctionNativeCloneInt extends FunctionNative {

	private static final long serialVersionUID = 20818224706781184L;

	public FunctionNativeCloneInt() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "clone";
		returnType = Types.INT;

		String[] argNames = { "x" };
		Type[] argTypes = { Types.INT };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		return bdsThread.getInt("x");
	}
}
