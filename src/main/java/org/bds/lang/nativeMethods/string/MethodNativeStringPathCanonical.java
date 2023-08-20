package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringPathCanonical extends MethodNativeString {

	private static final long serialVersionUID = 489842672203235328L;

	public MethodNativeStringPathCanonical() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "pathCanonical";
		classType = Types.STRING;
		returnType = Types.STRING;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread bdsThread, Object objThis) {
		try {
			return (bdsThread.data(objThis.toString())).getCanonicalPath();
		} catch (Exception e) {
			return "";
		}
	}
}
