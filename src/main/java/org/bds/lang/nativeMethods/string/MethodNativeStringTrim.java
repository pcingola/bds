package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringTrim extends MethodNativeString {

	private static final long serialVersionUID = 4167570505026535424L;

	public MethodNativeStringTrim() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "trim";
		classType = Types.STRING;
		returnType = Types.STRING;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread csThread, Object objThis) {
		return objThis.toString().trim();
	}
}
