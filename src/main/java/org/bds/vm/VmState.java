package org.bds.vm;

import org.bds.scope.Scope;

/**
 * Save VM state during long running OpCode execution such as 'callnative', 'sys', 'wait', etc.
 * <p>
 * IMPORTANT: These objects are meant to be reused!
 *
 * @author pcingola
 */
public class VmState extends CallFrame {

    private static final long serialVersionUID = -2276170953266575987L;

    public boolean valid;
    public int fp, sp;

    public VmState() {
        set(-1, -1, -1, -1, null);
    }

    /**
     * Mark this state as invalid.
     */
    public void invalidate() {
        valid = false;
        // A negative 'pc' also marks the state as invalid.
        // This would make the bdsVm throw an error when trying to execute on an invalid state, so
        // it makes the problem more obvious and easier to debug VM errors
        set(-1, -1, -1, -1, null);
    }

    /**
     * Is this state valid?
     */
    public boolean isValid() {
        return valid;
    }

    public void set(int fp, int nodeId, int pc, int sp, Scope scope) {
        this.pc = pc;
        this.nodeId = nodeId;
        this.fp = fp;
        this.sp = sp;
        this.scope = scope;
        valid = pc >= 0; // A non-negative 'pc' is valid
    }

    @Override
    public String toString() {
        return "{pc: " + pc //
                + ", fp: " + fp //
                + ", sp: " + sp //
                + ", nodeId: " + nodeId//
                + ", scope.name: " + scope.getScopeName() //
                + "}" //
                ;
    }
}
