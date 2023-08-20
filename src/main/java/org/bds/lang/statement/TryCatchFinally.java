package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.lang.type.TypeClass;
import org.bds.vm.OpCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Try / Catch / Finally Statements
 *
 * @author pcingola
 */
public class TryCatchFinally extends StatementWithScope {

    private static final long serialVersionUID = 1874966304662651073L;

    Catch[] catchStatements;
    Finally finallyStatement;
    Try tryStatement;

    public TryCatchFinally(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    /**
     * Is this class derived from Exception class?
     */
    boolean isExceptionType(TypeClass typeClass) {
        return false;
    }

    @Override
    protected void parse(ParseTree tree) {
        tryStatement = new Try(this, tree); // Parse 'try'
        catchStatements = parseCatch(tree); // Parse 'catch' statements
        finallyStatement = new Finally(this, tree); // Parse 'finally' statement
    }

    /**
     * Parse catch statements
     */
    Catch[] parseCatch(ParseTree tree) {
        int idx = 0;
        // Parse 'catch' statements
        List<Catch> catchStatementsList = new ArrayList<>();

        // Find first 'catch' node
        while (!isTerminal(tree, idx, "catch")) {
            idx++;
            if (idx >= tree.getChildCount()) return new Catch[0];
        }

        // Parse all catch nodes and add to the list
        while (isTerminal(tree, idx, "catch")) {
            Catch catchStatement = new Catch(this, tree);
            idx = catchStatement.parse(tree, idx);
            catchStatementsList.add(catchStatement);
            if (idx >= tree.getChildCount()) break;
        }

        return catchStatementsList.toArray(new Catch[0]);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());

        String finallyLabel = baseLabelName() + "finally"; // Create a unique label for the "finally" block

        if (isNeedsScope()) sb.append(OpCode.SCOPEPUSH + "\n");

        // EHSTART (Exception Handler Start): Create a new exception handler
        // Note: EHSTART will create a frame that is discarded by the EHEND
        sb.append(OpCode.EHSTART + " '" + finallyLabel + "'\n");

        // Register all 'catch' blocks in Exception handler
        for (Catch catchStatement : catchStatements)
            sb.append(catchStatement.toAsmAddToExceptionHandler());

        // Add 'try' statement
        sb.append(tryStatement.toAsm(finallyLabel));

        // Add 'catch' blocks
        for (Catch catchStatement : catchStatements) {
            sb.append(catchStatement.toAsm(finallyLabel));
        }

        // Add 'finally' block
        sb.append(finallyLabel + ":\n");
        if (finallyStatement != null) sb.append(finallyStatement.toAsm());

        // EHEND (Exception Handler End): Cleanup and Re-throw pending exceptions
        sb.append(OpCode.EHEND + "\n");

        if (isNeedsScope()) sb.append(OpCode.SCOPEPOP + "\n");

        return sb.toString();
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();

        if (tryStatement != null) {
            sb.append(sep + tryStatement.prettyPrint(sep));
        }

        if (catchStatements != null) {
            for (Catch c : catchStatements) {
                sb.append(" ");
                sb.append(c.prettyPrint(sep + SEP));
            }
        }

        if (finallyStatement != null) {
            sb.append(" ");
            sb.append(finallyStatement.prettyPrint(sep + SEP));
        }
        sb.append("\n");

        return sb.toString();
    }

}
