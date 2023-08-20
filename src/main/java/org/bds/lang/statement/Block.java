package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.vm.OpCode;

import java.util.ArrayList;
import java.util.List;

/**
 * A block of statements
 *
 * @author pcingola
 */
public class Block extends StatementWithScope {

    private static final long serialVersionUID = -8981215874906264612L;
    protected Statement[] statements;

    public Block(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    public Statement[] getStatements() {
        return statements;
    }

    public void setStatements(Statement[] statements) {
        this.statements = statements;
    }

    @Override
    protected void parse(ParseTree tree) {
        parse(tree, 0);
    }

    protected void parse(ParseTree tree, int startIdx) {
        List<Statement> stats = new ArrayList<>();
        for (int i = startIdx; i < tree.getChildCount(); i++) {
            BdsNode node = factory(tree, i);
            if (node != null) stats.add((Statement) node);
        }

        // Create an array
        statements = stats.toArray(new Statement[0]);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();

        if (isNeedsScope()) {
            sb.append(OpCode.NODE + " " + id + "\n");
            sb.append(OpCode.SCOPEPUSH + "\n");
        }

        for (Statement s : statements)
            sb.append(s.toAsm());

        if (isNeedsScope()) sb.append(OpCode.SCOPEPOP + "\n");

        return sb.toString();
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();
        if (statements != null) {
            for (int i = 0; i < statements.length; i++)
                sb.append(statements[i].prettyPrint(sep) + "\n");
        }
        return sb.toString();
    }

}
