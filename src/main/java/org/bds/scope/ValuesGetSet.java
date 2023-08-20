package org.bds.scope;

import org.bds.lang.value.Value;

import java.util.Collection;

public interface ValuesGetSet {

    /**
     * Get all names
     * E.g. All names in this scope, all field names in this object
     *
     * @return A list of names
     */
    Collection<String> getNames();

    /**
     * Get Value for 'name'
     *
     * @param name: Variable name (for scope) or field name (for object)
     * @return Value associated with the name, or null if not found
     */
    Value getValue(String name);

    /**
     * Set value for 'name'
     * WARNING: No type checking is performed
     */
    void setValue(String name, Value value);
}
