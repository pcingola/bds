package org.bds.lang.nativeMethods.list;

import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeList;
import org.bds.lang.type.Types;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueList;
import org.bds.run.BdsThread;

/**
 * RmOnExit: Mark all files to be deleted on exit
 *
 * @author pcingola
 */
public class MethodNativeListRmOnExitCancel extends MethodNativeList {

	private static final long serialVersionUID = 8336660191571690823L;

	public MethodNativeListRmOnExitCancel(TypeList listType) {
		super(listType);
	}

	@Override
	protected void initMethod(Type baseType) {
		functionName = "rmOnExitCancel";
		returnType = Types.VOID;

		String[] argNames = { "this" };
		Type[] argTypes = { classType };
		parameters = Parameters.get(argTypes, argNames);
		addNativeMethodToClassScope();
	}

	@Override
	public Value runMethod(BdsThread bdsThread, ValueList vthis) {
		bdsThread.rmOnExitCancel(vthis);
		return vthis;
	}
}
