package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.vm.OpCode;

/**
 * A comparison expression (<)
 *
 * @author pcingola
 */
public class ExpressionGe extends ExpressionCompare {

	private static final long serialVersionUID = 2734424457270910249L;

	public ExpressionGe(BdsNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected String op() {
		return ">=";
	}

	@Override
	public OpCode toAsmOp(PrimitiveType type) {
		switch (type) {
			case BOOL:
				return OpCode.GEB;
			case INT:
				return OpCode.GEI;
			case REAL:
				return OpCode.GER;
			case STRING:
				return OpCode.GES;
			default:
				compileError("Could not find operator '" + op() + "' for type '" + type + "'");
		}
		return null;
	}

}
