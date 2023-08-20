package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * A subtraction
 *
 * @author pcingola
 */
public class ExpressionMinus extends ExpressionMath {

    private static final long serialVersionUID = 4071972960119042793L;

    public ExpressionMinus(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected String op() {
        return "-";
    }

    @Override
    public OpCode toAsmOp(PrimitiveType type) {
        switch (type) {
            case INT:
                return OpCode.SUBI;
            case REAL:
                return OpCode.SUBR;
            default:
                compileError("Could not find operator '" + op() + "' for type '" + type + "'");
        }
        return null;
    }

    @Override
    public void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (left != null) left.checkCanCastToNumeric(compilerMessages);
        if (right != null) right.checkCanCastToNumeric(compilerMessages);
    }

}
