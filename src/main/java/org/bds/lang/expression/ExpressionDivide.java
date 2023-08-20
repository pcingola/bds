package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * A division
 *
 * @author pcingola
 */
public class ExpressionDivide extends ExpressionMath {

    private static final long serialVersionUID = 9104903229675355893L;

    public ExpressionDivide(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected String op() {
        return "/";
    }

    @Override
    public OpCode toAsmOp(PrimitiveType type) {
        switch (type) {
            case INT:
                return OpCode.DIVI;
            case REAL:
                return OpCode.DIVR;
            default:
                compileError("Could not find operator '" + op() + "' for type '" + type + "'");
        }
        return null;
    }

    @Override
    public void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        left.checkCanCastToNumeric(compilerMessages);
        right.checkCanCastToNumeric(compilerMessages);
    }

}
