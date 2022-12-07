package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;

/**
 * A module for functions, methods, etc.
 *
 * @author pcingola
 */
public class Module extends BlockWithFile {

    public Module(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }


}
