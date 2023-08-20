package org.bds.lang.nativeMethods.list;

import org.bds.lang.type.Type;
import org.bds.lang.type.TypeList;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueList;
import org.bds.run.BdsThread;

/**
 * Filter elements form a list by applying a function that returns a 'bool'
 *
 * @author pcingola
 */
public class MethodNativeListForEach extends MethodNativeList {

	private static final long serialVersionUID = 9104415099027143132L;

	public MethodNativeListForEach(TypeList listType) {
		super(listType);
	}

	@Override
	protected void initMethod(Type baseType) {
		functionName = "foreach";
		classType = TypeList.get(baseType);
		returnType = TypeList.get(baseType);

        // Functional methods not implemented
		//		TypeFunction typeFunc = TypeFunction.get(Parameters.get(baseType, ""), Types.BOOL);
		//		String argNames[] = { "this", "f" };
		//		Type argTypes[] = { classType, typeFunc };
		//		parameters = Parameters.get(argTypes, argNames);

		addNativeMethodToClassScope();
	}

	@Override
	public Value runMethod(BdsThread bdsThread, ValueList vthis) {
		// Functional methods not implemented
		bdsThread.runtimeError("Unimplemented!");
		return null;
	}

}
