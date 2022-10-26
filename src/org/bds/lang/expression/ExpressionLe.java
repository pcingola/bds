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
public class ExpressionLe extends ExpressionCompare {

	private static final long serialVersionUID = -9094126571337959268L;

	public ExpressionLe(BdsNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected String op() {
		return "<=";
	}

	@Override
	public OpCode toAsmOp(PrimitiveType type) {
		switch (type) {
			case BOOL:
				return OpCode.LEB;
			case INT:
				return OpCode.LEI;
			case REAL:
				return OpCode.LER;
			case STRING:
				return OpCode.LES;
			default:
				compileError("Could not find operator '" + op() + "' for type '" + type + "'");
		}
		return null;
	}

}
