package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.vm.OpCode;

/**
 * A comparison expression (!=)
 *
 * @author pcingola
 */
public class ExpressionNe extends ExpressionCompare {

	private static final long serialVersionUID = 680361809643102250L;

	public ExpressionNe(BdsNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected String op() {
		return "!=";
	}

	@Override
	public OpCode toAsmOp(PrimitiveType type) {
		switch (type) {
			case BOOL:
				return OpCode.NEB;
			case INT:
				return OpCode.NEI;
			case REAL:
				return OpCode.NER;
			case STRING:
				return OpCode.NES;
			default:
				compileError("Could not find operator '" + op() + "' for type '" + type + "'");
		}
		return null;
	}

}
