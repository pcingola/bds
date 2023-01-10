package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.vm.OpCode;

/**
 * try / catch / finally statements
 *
 * @author pcingola
 */
public class Finally extends StatementWithScope {

    private static final long serialVersionUID = 3950226124637269421L;

    Statement statement;

    public Finally(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected void parse(ParseTree tree) {
        int idx = 0;
        while (!isTerminal(tree, idx, "finally")) {
            idx++;
            if (idx >= tree.getChildCount()) return;
        }

        if (isTerminal(tree, idx, "finally")) idx++;
        statement = (Statement) factory(tree, idx++);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());
        // Note: If another exception is thrown within the 'finally' block, this
        // exception handler should not handle it (it should be handled by a surrounding try/catch)
        sb.append(OpCode.EHFSTART + "\n");
        if (statement != null) {
            if (isNeedsScope()) sb.append(OpCode.SCOPEPUSH + "\n");
            sb.append(statement.toAsm());
            if (isNeedsScope()) sb.append(OpCode.SCOPEPOP + "\n");
        }
        return sb.toString();
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append(sep + "finally {\n"); // Note: We don't start with 'sep' since this come from a 'TryCatchFinally' block
        if (statement != null) sb.append(statement.prettyPrint(sep + SEP));
        sb.append(sep + "}"); // Note: We don't end with a trailing '\n', it is handled in the 'TryCatchFinally' block
        return sb.toString();
    }

}
