package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.PrimitiveType;
import org.bds.lang.type.Type;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * A binary expression
 *
 * @author pcingola
 */
public class ExpressionBinary extends Expression {

    private static final long serialVersionUID = -9057903233688463643L;

    Expression left;
    Expression right;

    public ExpressionBinary(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public boolean isReturnTypesNotNull() {
        if (left == null) return false;
        if (right == null) return (left.getReturnType() != null);
        return left.isReturnTypesNotNull() && right.isReturnTypesNotNull();
    }

    /**
     * Operator to show when printing this expression
     */
    protected String op() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    protected void parse(ParseTree tree) {
        left = (Expression) factory(tree, 0);
        right = (Expression) factory(tree, 2); // Child 1 has the operator, we use child 2 here
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (returnType != null) return returnType;

        if (left != null) returnType = left.returnType(symtab, compilerMessages); // Left could be empty when there are some compile error (e.g. empty "switch" statement)
        if (right != null) right.returnType(symtab, compilerMessages); // Only assign this to show that calculation was already performed

        return returnType;
    }

    @Override
    public String toAsm() {
        return super.toAsm() //
                + left.toAsm() //
                + right.toAsm() //
                ;
    }

    /**
     * When an expression can have multiple OpCodes depending on the 'type', this method resolves it
     * E.g.: 'DIVI', 'DIVR' are both division opcodes that are specific to the divition type
     */
    protected OpCode toAsmOp(PrimitiveType type) {
        throw new RuntimeException("Unimplemented! This method must be overridden by child classes using it.");
    }

    public String prettyPrint(String sep) {
        return sep + left.prettyPrint("") + " " + op() + " " + right.prettyPrint("");
    }

}
