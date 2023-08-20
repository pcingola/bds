package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.vm.OpCode;

/**
 * An "breakpoint" statement
 *
 * @author pcingola
 */
public class Breakpoint extends Print {

    private static final long serialVersionUID = 8067280413717818916L;

    public Breakpoint(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(toAsmNode());

        // Label
        if (expr != null) sb.append(expr.toAsm());
        else sb.append(OpCode.PUSHS + " ''\n");

        sb.append(OpCode.BREAKPOINT + "\n");
        return sb.toString();
    }

}
