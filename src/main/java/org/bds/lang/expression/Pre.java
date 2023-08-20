package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * Pre increment / decrement operator
 * <p>
 * E.g. :  --i or ++i
 *
 * @author pcingola
 */
public class Pre extends ExpressionUnary {

    private static final long serialVersionUID = 8545479171921068320L;

    PrePostOperation operation;

    public Pre(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected void parse(ParseTree tree) {
        operation = PrePostOperation.parse(tree.getChild(0).getText());

        BdsNode node = factory(tree, 1);
        if ((node instanceof Reference)) expr = (Expression) node;
    }

    @Override
    public void sanityCheck(CompilerMessages compilerMessages) {
        if (!(expr instanceof Reference)) compilerMessages.add(this, "Only variable reference can be used with ++ or -- operators", MessageType.ERROR);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        Reference ref = (Reference) expr;
        sb.append(ref.toAsm());

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

        return sb.toString();
    }

    public String prettyPrint(String sep) {
        return sep + operation + (expr != null ? expr.prettyPrint("") : "");
    }

    @Override
    protected void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (!expr.isInt()) compilerMessages.add(this, "Only int variables can be used with ++ or -- operators", MessageType.ERROR);
    }

}
