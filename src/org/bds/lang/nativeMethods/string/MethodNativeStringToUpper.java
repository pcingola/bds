package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringToUpper extends MethodNativeString {

	private static final long serialVersionUID = 4418172619010899968L;

	public MethodNativeStringToUpper() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "toUpper";
		classType = Types.STRING;
		returnType = Types.STRING;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread csThread, Object objThis) {
		return objThis.toString().toUpperCase();
	}
}
