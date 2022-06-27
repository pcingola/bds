package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.lang.value.Value;
import org.bds.run.BdsThread;

public class MethodNative_string_rmOnExitCancel extends MethodNativeString {

	private static final long serialVersionUID = 2465790869796244402L;

	public MethodNative_string_rmOnExitCancel() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "rmOnExitCancel";
		classType = Types.STRING;
		returnType = Types.VOID;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	public Value runMethod(BdsThread bdsThread, Value vthis) {
		bdsThread.rmOnExitCancel(vthis);
		return vthis;
	}

	@Override
	protected Object runMethodNative(BdsThread bdsThread, Object objThis) {
		bdsThread.runtimeError("This method should never be invoked!");
		return null;
	}
}
