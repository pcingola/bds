package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * A module operation
 *
 * @author pcingola
 */
public class ExpressionModulo extends ExpressionMath {

    private static final long serialVersionUID = 8708379553154399665L;

    public ExpressionModulo(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected String op() {
        return "%";
    }

    @Override
    public OpCode toAsmOp(PrimitiveType type) {
        switch (type) {
            case INT:
                return OpCode.MODI;
            default:
                compileError("Could not find operator '" + op() + "' for type '" + type + "'");
        }
        return null;
    }

    @Override
    public void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        left.checkCanCastToInt(compilerMessages);
        right.checkCanCastToInt(compilerMessages);
    }

}
