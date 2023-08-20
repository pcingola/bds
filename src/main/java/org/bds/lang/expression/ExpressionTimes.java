package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * A multiplication
 *
 * @author pcingola
 */
public class ExpressionTimes extends ExpressionMath {

    private static final long serialVersionUID = 4728016127160486111L;

    public ExpressionTimes(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected String op() {
        return "*";
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (returnType != null) return returnType;

        super.returnType(symtab, compilerMessages);

        if (isReturnTypesNotNull()) {
            if (left.isString() || right.isString()) returnType = Types.STRING;
            else if (left.canCastToInt() && right.canCastToInt()) returnType = Types.INT;
            else if (left.canCastToReal() && right.canCastToReal()) returnType = Types.REAL;
        }
        return returnType;
    }

    @Override
    public OpCode toAsmOp(PrimitiveType type) {
        switch (type) {
            case INT:
                return OpCode.MULI;
            case REAL:
                return OpCode.MULR;
            case STRING:
                return OpCode.MULS;
            default:
                compileError("Could not find operator '" + op() + "' for type '" + type + "'");
        }
        return null;
    }

    @Override
    public void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (left.isString() && right.canCastToInt()) {
            // string * int: OK
        } else if (left.canCastToInt() && right.isString()) {
            // int * string : OK
        } else {
            // Normal 'math'
            left.checkCanCastToNumeric(compilerMessages);
            right.checkCanCastToNumeric(compilerMessages);
        }
    }

}
