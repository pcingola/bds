package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringSwapExtExt extends MethodNativeString {

	private static final long serialVersionUID = 4218465334908190720L;

	public MethodNativeStringSwapExtExt() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "swapExt";
		classType = Types.STRING;
		returnType = Types.STRING;

		String[] argNames = { "this", "ext" };
		Type[] argTypes = { Types.STRING, Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread csThread, Object objThis) {
		String ext = csThread.getString("ext");
		String b = objThis.toString();
		if (b.endsWith(ext)) return b.substring(0, b.length() - ext.length());
		return b;
	}
}
