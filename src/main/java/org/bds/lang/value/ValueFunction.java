package org.bds.lang.value;

import java.util.Set;

import org.bds.lang.Parameters;
import org.bds.lang.statement.FunctionDeclaration;

/**
 * Define a value of type 'function'
 * @author pcingola
 */
public class ValueFunction extends ValueComposite {

	private static final long serialVersionUID = -823063546180378116L;

	// Note: returnType already exists in BdsNode
	FunctionDeclaration functionDecl;

	public ValueFunction(FunctionDeclaration functionDecl) {
		super(functionDecl.getType());
		this.functionDecl = functionDecl;
	}

	@Override
	public Value clone() {
		return this; // Function types are not cloned
	}

	public FunctionDeclaration getFunctionDeclaration() {
		return functionDecl;
	}

	public Parameters getParameters() {
		return functionDecl.getParameters();
	}

	@Override
	public int hashCode() {
		return functionDecl.hashCode();
	}

	@Override
	public void parse(String str) {
		runtimeError("String parsing unimplemented for type '" + this + "'");
	}

	@Override
	public void setValue(Value v) {
		// Ignored: Value cannot be set.
	}

	@Override
	public String toString() {
		return functionDecl.signature();
	}

	@Override
	protected void toString(StringBuilder sb, Set<Value> done) {
		sb.append(functionDecl.toString());
	}

}
