package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.ExpressionWrapper;
import org.bds.vm.OpCode;

/**
 * A statement that is actually an expression.
 * E.g.:
 * i++    # This statement is a line of code with only one expression ('i++')
 * f(42)  # Same here, the expression is a function call
 *
 * @author pcingola
 */
public class StatementExpr extends ExpressionWrapper {

    private static final long serialVersionUID = 2132767867913705734L;

    public StatementExpr(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    public String prettyPrint(String sep) {
        return sep + expression.prettyPrint(sep) + "\n";
    }

    @Override
    public String toAsm() {
        return toAsmNode() //
                + expression.toAsm() //
                + OpCode.POP + "\n" // Expression leaves result in the stack, so we remove it (the result is not used)
                ;
    }

}
