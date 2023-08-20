package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.Type;
import org.bds.symbol.SymbolTable;

/**
 * A binary expression wrapper
 *
 * @author pcingola
 */
public class ExpressionDelegateBinary extends ExpressionBinary {

    private static final long serialVersionUID = 6807552145516884040L;

    ExpressionBinary expr;

    public ExpressionDelegateBinary(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    public Expression getLeft() {
        return expr.getLeft();
    }

    @Override
    public void setLeft(Expression left) {
        expr.setLeft(left);
    }

    @Override
    public Expression getRight() {
        return expr.getRight();
    }

    @Override
    public void setRight(Expression right) {
        expr.setRight(right);
    }

    @Override
    public boolean isReturnTypesNotNull() {
        return expr.isReturnTypesNotNull();
    }

    @Override
    protected String op() {
        return expr.op();
    }

    @Override
    protected void parse(ParseTree tree) {
        compileError("Unimplemented parsing. This should never happen!");
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        returnType = expr.returnType(symtab, compilerMessages);
        return returnType;
    }

    @Override
    public String toAsm() {
        return expr.toAsm();
    }

    public String prettyPrint(String sep) {
        return expr.prettyPrint("");
    }

    @Override
    public void typeCheck(SymbolTable symtab, CompilerMessages compilerMessages) {
        expr.typeCheck(symtab, compilerMessages);
    }

    @Override
    public void typeChecking(SymbolTable symtab, CompilerMessages compilerMessages) {
        expr.typeChecking(symtab, compilerMessages);
    }
}
