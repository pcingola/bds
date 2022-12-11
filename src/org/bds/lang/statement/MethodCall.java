package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.Expression;
import org.bds.lang.expression.ReferenceVar;
import org.bds.lang.type.Type;
import org.bds.symbol.SymbolTable;

/**
 * Method invocation
 *
 * @author pcingola
 */
public class MethodCall extends FunctionCall {

	private static final long serialVersionUID = -2090332737988933898L;

	// Object that calls the method: obj.method(args)
	// This object is also the used as the first argument in
	// the function call (i.e. 'this' argument).
	protected Expression expresionThis;

	public MethodCall(BdsNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected boolean isMethodCall() {
		return true;
	}

	/**
	 * Is this a 'super.f()' method call?
	 */
	@Override
	protected boolean isSuper() {
		return expresionThis instanceof ReferenceVar && ((ReferenceVar) expresionThis).isSuper();
	}

	@Override
	protected void parse(ParseTree tree) {
		// Parsing code example: 'object.methodName( arguments... )'

		// First child is 'object'
		expresionThis = (Expression) factory(tree, 0);

		// Next child is the dot; child[1] = '.', no need to parse it

		// Next child is methodName
		functionName = tree.getChild(2).getText();

		// Followed by open parenthesis: child[3] = '(', we don't need to parse it

		// Next nodes (until the last one) are method arguments
		args = new Args(this, null);
		args.parse(tree, 4, tree.getChildCount() - 1);

		// Last node is a closing parenthesis
		// child[tree.getChildCount()] = ')'
		// We don't need to parse it

		// In our method implementation, the first argument is always 'this'
		// So we need to add 'expresionObj' as first argument
		args = Args.getArgsThis(args, expresionThis);
	}

	@Override
	public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
		if (returnType != null) return returnType;

		// Calculate return types for expr and args
		// Note that expresionThis is null in ExpressionNew (which is a MethodCall)
		Type exprType = (expresionThis != null ? expresionThis.returnType(symtab, compilerMessages) : null);
		args.returnType(symtab, compilerMessages);

		// Find method
		functionDeclaration = findMethod(symtab, exprType, args);
		if (functionDeclaration != null) returnType = functionDeclaration.getReturnType();

		return returnType;
	}

	@Override
	protected String signature() {
		StringBuilder sig = new StringBuilder();

		Type classType = expresionThis.getReturnType();
		sig.append(classType != null ? classType : "null");
		sig.append(".");
		sig.append(functionName);
		sig.append("(");
		for (int i = 1; i < args.size(); i++) {
			sig.append(args.getArguments()[i].getReturnType());
			if (i < (args.size() - 1)) sig.append(",");
		}
		sig.append(")");
		return sig.toString();
	}

	@Override
	protected void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
		// Could not find the function?
		if (functionDeclaration == null) {
			compilerMessages.add(this, "Method " + signature() + " cannot be resolved", MessageType.ERROR);
		}
	}

}
