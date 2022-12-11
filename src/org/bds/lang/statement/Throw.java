package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.Expression;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeClass;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

import static org.bds.libraries.LibraryException.CLASS_NAME_THROWABLE;

/**
 * Throw statement
 *
 * @author pcingola
 */
public class Throw extends StatementWithScope {

    private static final long serialVersionUID = -861450592187243401L;

    Expression expr;

    public Throw(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    /**
     * Is the type derived from 'Throwable'?
     */
    public static boolean isThrowableClass(Type t) {
        if (t == null) return false;
        if (!t.isClass()) return false;

        for (TypeClass tc = (TypeClass) t; tc != null; tc = tc.getClassDeclaration().getClassTypeParent()) {
            if (tc.getCanonicalName().equals(CLASS_NAME_THROWABLE)) return true;
        }

        return false;
    }

//    /**
//     * Is the expression derived from 'Exception'?
//     */
//    boolean isExceptionClass() {
//        return isExceptionClass(expr.getReturnType());
//    }

    @Override
    protected void parse(ParseTree tree) {
        int idx = 0;
        if (isTerminal(tree, idx, "throw")) idx++;
        expr = (Expression) factory(tree, idx);
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (returnType != null) return returnType;
        returnType = expr.returnType(symtab, compilerMessages);
        return returnType;
    }

    @Override
    public String toAsm() {
        return expr.toAsm() + OpCode.THROW + "\n";
    }

    @Override
    public String toString() {
        return "throw " + (expr != null ? expr.toString() : "");
    }

    @Override
    public void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        // We no longer require the class to be a subclass of 'Exception' because now
        // we wrap it in an 'Exception' object automatically
    }

}
