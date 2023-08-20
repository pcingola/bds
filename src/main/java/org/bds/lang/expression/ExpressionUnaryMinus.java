package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.lang.value.LiteralInt;
import org.bds.lang.value.LiteralReal;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * A arithmetic negation
 *
 * @author pcingola
 */
public class ExpressionUnaryMinus extends ExpressionUnary {

    private static final long serialVersionUID = -5030008162832737754L;

    public ExpressionUnaryMinus(BdsNode parent, ParseTree tree) {
        super(parent, tree);
        op = "-";
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (returnType != null) return returnType;

        expr.returnType(symtab, compilerMessages);

        if (expr.canCastToInt()) returnType = Types.INT;
        else if (expr.canCastToReal()) returnType = Types.REAL;
        else return null; // Cannot cast to 'int' or 'real'. This should never happen!"

        return returnType;
    }

    @Override
    public String toAsm() {
        // Int expression
        if (isInt()) {
            if (isLiteralInt()) {
                // If it's a literal, just use the minus sign in the literal
                return ((LiteralInt) expr).toAsm(true);
            }
            return OpCode.PUSHI + " 0\n" //
                    + expr.toAsm() //
                    + OpCode.SUBI + "\n";
        }

        // Real expression
        if (isReal()) {
            if (isLiteralReal()) {
                // If it's a literal, just use the minus sign in the literal
                return ((LiteralReal) expr).toAsm(true);
            }
            return OpCode.PUSHR + " 0.0\n" //
                    + expr.toAsm() //
                    + OpCode.SUBR + "\n";
        }

        compileError("Cannot cast to 'int' or 'real'. This should never happen!");
        return "";
    }

    @Override
    public void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        // Can we transform to an int?
        if (!expr.canCastToInt() && !expr.canCastToReal()) compilerMessages.add(this, "Cannot cast expression to int or real", MessageType.ERROR);
    }

}
