package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.vm.OpCode;

/**
 * try / catch / finally statements
 *
 * @author pcingola
 */
public class Try extends StatementWithScope {

    private static final long serialVersionUID = -2501792284878137707L;

    Statement statement;

    public Try(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected void parse(ParseTree tree) {
        int idx = 0;
        while (!isTerminal(tree, idx, "try"))
            idx++;
        statement = (Statement) factory(tree, ++idx);
    }

    public String toAsm(String finallyLabel) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());
        if (statement != null) {
            if (isNeedsScope()) sb.append(OpCode.SCOPEPUSH + "\n");
            sb.append(statement.toAsm());
            if (isNeedsScope()) sb.append(OpCode.SCOPEPOP + "\n");
        }
        sb.append(OpCode.JMP + " '" + finallyLabel + "'\n");
        return sb.toString();
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append("try {\n"); // Note: We don't start with 'sep' since this come from a 'TryCatchFinally' block

        if (statement != null) {
            sb.append(statement.prettyPrint(sep + SEP));
        }
        sb.append(sep + "}"); // Note: We don't end with a trailing '\n', it is handled in the 'TryCatchFinally' block
        return sb.toString();
    }

}
