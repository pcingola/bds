package org.bds.lang.type;

import org.bds.BdsLogStatic;

/**
 * An enumeration of primitive types
 *
 * @author pcingola
 */
public enum PrimitiveType {
    ANY, BOOL, CLASS, FUNCTION, LIST, MAP, NULL, INT, REAL, STRING, VOID;

    /**
     * Get 'Type' object from this enum
     *
     * @return
     */
    public Type get() {
        switch (this) {
            case ANY:
                return Types.ANY;

            case BOOL:
                return Types.BOOL;

            case INT:
                return Types.INT;

            case NULL:
                return Types.NULL;

            case REAL:
                return Types.REAL;

            case STRING:
                return Types.STRING;

            case VOID:
                return Types.VOID;

            default:
                BdsLogStatic.fatalError("Type " + this + " not found. This should never happen!");
                return null;
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
