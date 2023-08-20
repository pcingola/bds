package org.bds.lang.nativeMethods.string;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeList;
import org.bds.lang.type.Types;
import org.bds.lang.value.Value;
import org.bds.run.BdsThread;

public class MethodNativeStringLines extends MethodNativeString {

	private static final long serialVersionUID = 9075645229466768314L;

	public MethodNativeStringLines() {
		super();
	}

	@Override
	protected void initMethod() {
		functionName = "lines";
		classType = Types.STRING;
		returnType = TypeList.get(Types.STRING);

		String[] argNames = { "this" };
		Type[] argTypes = { Types.STRING };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	public Value runMethod(BdsThread bdsThread, Value vThis) {
		return arrayString2valuelist(vThis.asString().split("\n"));
	}

	@Override
	protected Object runMethodNative(BdsThread bdsThread, Object objThis) {
		bdsThread.runtimeError("This method should never be invoked!");
		return null;
	}

}
