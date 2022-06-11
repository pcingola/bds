package org.bds.lang.nativeFunctions;

import org.bds.executioner.Executioners;
import org.bds.lang.Parameters;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;
import org.bds.scope.GlobalScope;
import org.bds.scope.Scope;

/**
 * Native function "add" a resource (e.g. GPU, FFPGA, etc.)
 * 
 * @author pcingola
 */
public class FunctionNativeAddResource extends FunctionNative {

	private static final long serialVersionUID = 8456092320198223591L;


	public FunctionNativeAddResource() {
		super();
	}

	@Override
	protected void initFunction() {
		functionName = "addResource";
		returnType = Types.BOOL;

		String[] argNames = { "resourceName", "count" };
		Type[] argTypes = { Types.STRING, Types.INT };
		parameters = Parameters.get(argTypes, argNames);
		addNativeFunction();
	}

	@Override
	protected Object runFunctionNative(BdsThread bdsThread) {
		String resourceName = bdsThread.getString("resourceName");
		long count = bdsThread.getInt("count");

		// Add resource current system
		var systemName = bdsThread.getString(GlobalScope.GLOBAL_VAR_TASK_OPTION_SYSTEM);
		var exType = Executioners.ExecutionerType.parseSafe(systemName);

		// Add resource to executioners type
		Executioners.getInstance().registerCustomResource(exType, resourceName, count);

		// Add resource to every host in the current executioner's system
		var executioner = Executioners.getInstance().get(exType, bdsThread);
		executioner.addCustomResource(resourceName, count);

		// Add global variable: Find global scope and add it
		Scope scope = bdsThread.getScope();
		for( ; scope.getParent()!=null ; scope = scope.getParent()) ;
		scope.add(resourceName, Types.INT);

		return true;
	}
}
