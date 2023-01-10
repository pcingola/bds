package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.type.TypeClass;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * try / catch / finally statements
 *
 * @author pcingola
 */
public class Catch extends StatementWithScope {

    private static final long serialVersionUID = -2978341443887136421L;

    VarDeclaration declareExceptionVar; // This node has to be type-checked before the others, so the name should be (alphabetically) before
    Statement statement;
    TypeClass typeClassException;
    String varName;

    public Catch(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    String getLabel() {
        return baseLabelName() + "catch";
    }

    @Override
    protected void parse(ParseTree tree) {
    }

    public int parse(ParseTree tree, int idx) {
        if (isTerminal(tree, idx, "catch")) idx++;
        if (isTerminal(tree, idx, "(")) idx++;
        typeClassException = (TypeClass) factory(tree, idx++);
        varName = tree.getChild(idx++).getText();
        declareExceptionVar = VarDeclaration.get(this, typeClassException, varName, null);
        if (isTerminal(tree, idx, ")")) idx++;
        statement = (Statement) factory(tree, idx++);
        return idx;
    }

    public String toAsm(String finallyLabel) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());
        sb.append(getLabel() + ":\n");
        // Reset Exception handler
        // Note: If another exception is thrown within the 'catch' block, this
        // exception handler should not handle it (it should be handled by a surrounding try/catch)
        sb.append(OpCode.EHCSTART + "\n");
        if (statement != null) {
            if (isNeedsScope()) sb.append(OpCode.SCOPEPUSH + "\n");
            sb.append(statement.toAsm());
            if (isNeedsScope()) sb.append(OpCode.SCOPEPOP + "\n");
        }
        sb.append(OpCode.JMP + " '" + finallyLabel + "'\n");
        return sb.toString();
    }

    /**
     * Add this 'catch' block to the current Exception handler
     */
    public String toAsmAddToExceptionHandler() {
        return OpCode.PUSHS + " '" + varName + "'\n" //
                + OpCode.PUSHS + " '" + typeClassException.getClassName() + "'\n" //
                + OpCode.EHADD + " '" + getLabel() + "'\n" //
                ;
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append("catch ("); // Note: We don't start with 'sep' since this come from a 'TryCatchFinally' block
        if (typeClassException != null && varName != null) sb.append(typeClassException.prettyPrint("") + " " + varName);
        sb.append(") {\n");
        if (statement != null) sb.append(statement.prettyPrint(sep + SEP));
        sb.append(sep + "}"); // Note: We don't end with a trailing '\n', it is handled in the 'TryCatchFinally' block
        return sb.toString();
    }

    @Override
    public void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (statement == null) {
            compilerMessages.add(this, "Empty catch statement", MessageType.ERROR);
        }
    }

}
