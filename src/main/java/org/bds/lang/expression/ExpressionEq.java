package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.vm.OpCode;

/**
 * A comparison expression (==)
 *
 * @author pcingola
 */
public class ExpressionEq extends ExpressionCompare {

    private static final long serialVersionUID = -8279888930176768990L;

    public ExpressionEq(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected String op() {
        return "==";
    }

    @Override
    public OpCode toAsmOp(PrimitiveType type) {
        switch (type) {
            case BOOL:
                return OpCode.EQB;
            case INT:
                return OpCode.EQI;
            case REAL:
                return OpCode.EQR;
            case STRING:
                return OpCode.EQS;
            default:
                compileError("Could not find operator '" + op() + "' for type '" + type + "'");
        }
        return null;
    }
}
