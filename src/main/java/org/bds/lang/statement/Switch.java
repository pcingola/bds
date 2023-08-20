package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.Expression;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

import java.util.ArrayList;
import java.util.List;

/**
 * If statement
 *
 * @author pcingola
 */
public class Switch extends Statement {

    private static final long serialVersionUID = -8726313466692867714L;

    Expression switchExpr;
    Case[] caseStatements;
    Default defaultStatement;

    public Switch(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    public Expression getSwitchExpr() {
        return switchExpr;
    }

    @Override
    protected void parse(ParseTree tree) {
        List<Case> caseSts = new ArrayList<>();

        int idx = 0;
        if (isTerminal(tree, idx, "switch")) idx++; // 'switch'
        if (isTerminal(tree, idx, "(")) idx++; // '('
        if (!isTerminal(tree, idx, ")")) switchExpr = (Expression) factory(tree, idx++);
        if (isTerminal(tree, idx, ")")) idx++; // ')'
        if (isTerminal(tree, idx, "{")) idx++; // '{'
        int idxOri = idx;

        // Parse all 'case' statements
        while (true) {
            Case caseSt = new Case(this, tree);
            idx = caseSt.parse(tree, idx);
            if (idx < 0) break;
            caseSts.add(caseSt);
        }
        caseStatements = caseSts.toArray(new Case[0]);

        // Do we have an 'default' statement?
        Default defSt = new Default(this, tree);
        idx = defSt.parse(tree, idxOri);
        if (idx < 0) return; // Default statement not found
        defaultStatement = defSt;
    }

    /**
     * Run the program
     */
    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());

        String labelSwitch = baseLabelName() + "switch";
        String labelEnd = baseLabelName() + "end";
        String labelDefault = baseLabelName() + "default";
        String labelDefaultCondition = baseLabelName() + "default_condition";
        String varSwitchExpr = baseVarName() + "expr";

        // Run switch expression
        sb.append(labelSwitch + ":\n");
        sb.append(OpCode.SCOPEPUSH + "\n");
        sb.append(toAsmSwitchExpression(varSwitchExpr));

        //---
        // Case conditions
        // Note this could be implemented using a jump table, but in bds a
        // 'case' can have arbitrary expressions, such as function calls.
        //---

        // Compare to each case expression
        for (Case caseSt : caseStatements)
            sb.append(caseSt.toAsmCondition(varSwitchExpr));

        // Is there a default statement?
        if (defaultStatement != null) {
            sb.append(labelDefaultCondition + ":\n");
            sb.append(OpCode.JMP + " " + defaultStatement.label() + "\n");
        } else {
            sb.append(OpCode.JMP + " " + labelEnd + "\n");
        }

        //---
        // Case statements
        //---
        for (Case caseSt : caseStatements)
            sb.append(caseSt.toAsm());

        // Default statement
        if (defaultStatement != null) {
            sb.append(labelDefault + ":\n");
            sb.append(defaultStatement.toAsm());
        }

        // We are done
        sb.append(labelEnd + ":\n");
        sb.append(OpCode.SCOPEPOP + "\n"); // Restore scope

        return sb.toString();
    }

    /**
     * Evaluate switch expression
     */
    String toAsmSwitchExpression(String varSwitchExpr) {
        if (switchExpr == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(switchExpr.toAsm());
        sb.append(OpCode.VARPOP + " " + varSwitchExpr + "\n");
        return sb.toString();
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();

        sb.append(sep + "switch( ");
        if (switchExpr != null) sb.append(switchExpr.prettyPrint(""));
        sb.append(" ) {\n");

        if (caseStatements != null) {
            for (Case c : caseStatements)
                sb.append(c.prettyPrint(sep + SEP));
        }

        if (defaultStatement != null) {
            sb.append(defaultStatement.prettyPrint(sep + SEP));
        }

        sb.append(sep + "}");

        return sb.toString();
    }

    @Override
    public void typeCheck(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (switchExpr != null) switchExpr.returnType(symtab, compilerMessages);
    }

}
