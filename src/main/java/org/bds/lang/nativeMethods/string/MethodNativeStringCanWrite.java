package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringCanWrite extends MethodNativeString {

	private static final long serialVersionUID = 20751364950884352L;

	public MethodNativeStringCanWrite() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "canWrite";
		classType = Types.STRING;
		returnType = Types.BOOL;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread bdsThread, Object objThis) {
		return (bdsThread.data(objThis.toString())).canWrite();
	}

}
