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
public class ExpressionGt extends ExpressionCompare {

    private static final long serialVersionUID = 4293517244590722796L;

    public ExpressionGt(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected String op() {
        return ">";
    }

    @Override
    public OpCode toAsmOp(PrimitiveType type) {
        switch (type) {
            case BOOL:
                return OpCode.GTB;
            case INT:
                return OpCode.GTI;
            case REAL:
                return OpCode.GTR;
            case STRING:
                return OpCode.GTS;
            default:
                compileError("Could not find operator '" + op() + "' for type '" + type + "'");
        }
        return null;
    }
}
