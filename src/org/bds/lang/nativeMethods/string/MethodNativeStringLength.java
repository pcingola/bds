package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringLength extends MethodNativeString {

	private static final long serialVersionUID = 8990581552358391808L;

	public MethodNativeStringLength() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "length";
		classType = Types.STRING;
		returnType = Types.INT;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread csThread, Object objThis) {
		return (long) objThis.toString().length();
	}
}
