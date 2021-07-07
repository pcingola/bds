package org.bds.lang.expression;

import org.bds.BdsLog;
import org.bds.BdsLogStatic;

/**
 * Pre/Post operation type.
 * E.g.:
 * ```
 * i++
 * --j
 * ```
 */
public enum PrePostOperation implements BdsLog {
    INCREMENT, DECREMENT;

    /**
     * Parse a string
     */
    public static PrePostOperation parse(String opStr) {
        if (opStr.equals("++")) return PrePostOperation.INCREMENT;
        else if (opStr.equals("--")) return PrePostOperation.DECREMENT;
        BdsLogStatic.fatalError("Cannot parse string'" + opStr + "'");
        return null;
    }

    @Override
    public String toString() {
        switch (this) {
            case INCREMENT:
                return "++";

            case DECREMENT:
                return "--";

            default:
                fatalError("Unhandled operation " + this);
                return null;
        }

    }
}
