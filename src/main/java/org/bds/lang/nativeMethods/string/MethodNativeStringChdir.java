package org.bds.lang.nativeMethods.string;

import org.bds.data.Data;
import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;

public class MethodNativeStringChdir extends MethodNativeString {

	private static final long serialVersionUID = 2008621469747150848L;

	public MethodNativeStringChdir() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "chdir";
		classType = Types.STRING;
		returnType = Types.VOID;

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	protected Object runMethodNative(BdsThread bdsThread, Object objThis) {
		String dirName = objThis.toString();
		Data dir = bdsThread.data(dirName);

		// Is it remote?
		if (dir.isRemote()) bdsThread.runtimeError("Cannot chdir to remote directory '" + dirName + "'");

		// Local dir processing
		String path = dir.getPath();
		if (!dir.exists()) bdsThread.runtimeError("Directory '" + path + "' does not exists");
		if (!dir.isDirectory()) bdsThread.runtimeError("Cannot chdir to '" + path + "', not a directory.");

		// OK change dir
		bdsThread.setCurrentDir(path);
		return null;
	}
}
