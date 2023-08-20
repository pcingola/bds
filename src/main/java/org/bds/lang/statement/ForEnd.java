package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.ExpressionList;
import org.bds.vm.OpCode;

/**
 * for( ForInit ; ForCondition ; ForEnd ) Statements
 *
 * @author pcingola
 */
public class ForEnd extends ExpressionList {

    private static final long serialVersionUID = 3169463903451551181L;

    public ForEnd(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expressions.length; i++) {
            sb.append(expressions[i].toAsm());
            sb.append(OpCode.POP + "\n");
        }
        return sb.toString();
    }

}
