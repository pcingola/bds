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
public class ExpressionLt extends ExpressionCompare {

	private static final long serialVersionUID = -4628853302241456425L;

	public ExpressionLt(BdsNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected String op() {
		return "<";
	}

	@Override
	public OpCode toAsmOp(PrimitiveType type) {
		switch (type) {
			case BOOL:
				return OpCode.LTB;
			case INT:
				return OpCode.LTI;
			case REAL:
				return OpCode.LTR;
			case STRING:
				return OpCode.LTS;
			default:
				compileError("Could not find operator '" + op() + "' for type '" + type + "'");
		}
		return null;
	}

}
