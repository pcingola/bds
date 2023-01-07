package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.util.Gpr;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("try {\n");
        if (statement != null) {
            sb.append(Gpr.prependEachLine("\t", statement.toString()));
        }
        sb.append("} ");
        return sb.toString();
    }

}
