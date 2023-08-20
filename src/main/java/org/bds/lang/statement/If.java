package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.Expression;
import org.bds.lang.type.Type;
import org.bds.symbol.SymbolTable;
import org.bds.util.Gpr;
import org.bds.vm.OpCode;

/**
 * If statement
 *
 * @author pcingola
 */
public class If extends Statement {

    private static final long serialVersionUID = 4743568468530684151L;

    Expression condition;
    Statement statement;
    Statement elseStatement;

    public If(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected void parse(ParseTree tree) {
        int idx = 0;
        if (isTerminal(tree, idx, "if")) idx++; // 'if'
        if (isTerminal(tree, idx, "(")) idx++; // '('
        if (!isTerminal(tree, idx, ")")) condition = (Expression) factory(tree, idx++);
        if (isTerminal(tree, idx, ")")) idx++; // ')'
        statement = (Statement) factory(tree, idx++);

        // Do we have an 'else' statement?
        idx = findIndex(tree, "else", idx);
        if (idx > 0) elseStatement = (Statement) factory(tree, idx + 1);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());

        String labelBase = baseLabelName();
        String ifLabel = labelBase + "if";
        String elseLabel = labelBase + "else";
        String endLabel = labelBase + "end";
        if (elseStatement == null) elseLabel = endLabel;

        sb.append(condition.toAsm());
        sb.append(OpCode.JMPF + " " + elseLabel + "\n");
        sb.append(ifLabel + ":\n");
        sb.append(statement.toAsm());

        if (elseStatement != null) {
            sb.append(OpCode.JMP + " " + endLabel + "\n");
            sb.append(elseLabel + ":\n");
            sb.append(elseStatement.toAsm());
        }
        sb.append(endLabel + ":\n");

        return sb.toString();
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();

        sb.append(sep + "if( ");
        if (condition != null) sb.append(condition.prettyPrint(""));
        sb.append(" ) {\n");
        if (statement != null) sb.append(Gpr.prependEachLine("\t", statement.prettyPrint(sep + SEP)));
        if (elseStatement != null) {
            sb.append(sep + "\n} else {\n");
            sb.append(elseStatement.prettyPrint(sep + SEP));
        }
        sb.append(sep + "}");

        return sb.toString();
    }

    @Override
    public void typeCheck(SymbolTable symtab, CompilerMessages compilerMessages) {
        Type retType = condition.returnType(symtab, compilerMessages);
        if ((condition != null) //
                && !condition.isBool() //
                && (retType != null) //
                && !retType.canCastToBool()//
        ) compilerMessages.add(this, "Condition in 'if' statement must be a bool expression", MessageType.ERROR);
    }
}
