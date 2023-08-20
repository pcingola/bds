package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;

/**
 * A binary expression: Either plus or minus
 *
 * @author pcingola
 */
public class ExpressionPlusMinus extends ExpressionDelegateBinary {

	private static final long serialVersionUID = 1589639689434269454L;

	public ExpressionPlusMinus(BdsNode parent, ParseTree tree) {
		super(parent, tree);
	}

	@Override
	protected void parse(ParseTree tree) {
		String op = tree.getChild(1).getText();
		switch (op) {
		case "+":
			expr = new ExpressionPlus(this, tree);
			break;

		case "-":
			expr = new ExpressionMinus(this, tree);
			break;

		default:
			runtimeError("Unsuported operator '" + op + "'. This should never happen!");
		}
	}

}
