package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringCanExec extends MethodNativeString {

	private static final long serialVersionUID = 3196241901321748480L;

	public MethodNativeStringCanExec() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "canExec";
		classType = Types.STRING;
		returnType = Types.BOOL;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread bdsThread, Object objThis) {
		return (bdsThread.data(objThis.toString())).canExecute();
	}
}
