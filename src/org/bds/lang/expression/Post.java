package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.vm.OpCode;

/**
 * Post increment / decrement operator
 * <p>
 * E.g. :  i++ or i--
 *
 * @author pcingola
 */
public class Post extends Pre {

    private static final long serialVersionUID = -7171687135383949830L;

    public Post(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected void parse(ParseTree tree) {
        BdsNode node = factory(tree, 0);
        if (node instanceof Reference) expr = (Expression) node;

        operation = PrePostOperation.parse(tree.getChild(1).getText());
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        Reference ref = (Reference) expr;
        sb.append(ref.toAsm());
        sb.append(OpCode.DUP+"\n");

        switch (operation) {
            case INCREMENT:
                sb.append(OpCode.INC + "\n");
                break;

            case DECREMENT:
                sb.append(OpCode.DEC + "\n");
                break;

            default:
                compileError("Unknown operator " + operation);
        }

        sb.append(ref.toAsmSet());
        sb.append(OpCode.POP+"\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return "" + expr + operation;
    }

}
