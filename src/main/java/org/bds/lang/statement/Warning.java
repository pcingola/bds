package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.vm.OpCode;

/**
 * An "warning" statement (quit the program immediately)
 *
 * @author pcingola
 */
public class Warning extends Error {

    private static final long serialVersionUID = -2281956946821260039L;

    public Warning(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    public String toAsm() {
        return toAsmNode() //
                + expr.toAsm() //
                + OpCode.PRINTSTDERRLN + "\n";
    }

}
