package org.bds.lang.value;

import org.bds.lang.type.Type;
import org.bds.lang.type.TypeClass;
import org.bds.scope.ValuesGetSet;
import org.bds.symbol.SymbolTable;

import java.util.*;

/**
 * Define a value of an object (i.e. a class)
 *
 * @author pcingola
 */
public class ValueObject extends ValueComposite implements ValuesGetSet {

    private static final long serialVersionUID = -1443386366370835828L;
    Map<String, Value> fields;

    public ValueObject(Type type) {
        super(type);
    }

    /**
     * Is this a 'hidden' field?
     * Hidden field names start with '$'
     */
    public static boolean isHiddenField(String fieldName) {
        return fieldName.charAt(0) == SymbolTable.INTERNAL_SYMBOL_START_CHAR;
    }

    @Override
    public Value clone() {
        ValueObject vclone = new ValueObject(type);
        vclone.fields.putAll(fields);
        return vclone;
    }

    public Set<String> getFieldNames() {
        if (isNull()) return new HashSet<>();
        return fields.keySet();
    }

    public Type getFieldType(String name) {
        TypeClass tc = (TypeClass) type;
        return tc.getFieldType(name);
    }

    /**
     * Get field's value, can be null
     */
    public Value getFieldValue(String name) {
        return fields.get(name);
    }

    @Override
    public Collection<String> getNames() {
        return getFieldNames();
    }

    @Override
    public Value getValue(String name) {
        return fields.get(name);
    }


    /**
     * Does the class have a field 'name'?
     */
    public boolean hasField(String name) {
        TypeClass tc = (TypeClass) type;
        return tc.hasField(name);
    }

    @Override
    public int hashCode() {
        // Note: We use identity hash to avoid infinite recursion
        //       E.g. if an object has a field pointing to itself. This
        //       could also happen indirectly:  A -> B -> A
        return System.identityHashCode(this);
        //		return fields != null ? fields.hashCode() : 0;
    }

    /**
     * Initialize fields (by default the fields are null)
     */
    public void initializeFields() {
        fields = new HashMap<>();
        TypeClass tc = (TypeClass) type;

        // Fields for this class and all parent classes
        for (Map.Entry<String, Type> e : tc.getFieldTypes().entrySet()) {
            var fieldName = e.getKey();
            var fieldType = e.getValue();
            fields.put(fieldName, fieldType.newDefaultValue());
        }
    }

    public boolean isNull() {
        return fields == null;
    }

    @Override
    public void parse(String str) {
        runtimeError("String parsing unimplemented for type '" + this + "'");
    }

    public void setValue(String name, Value v) {
        if (isNull()) runtimeError("Null pointer: Cannot set field '" + getType() + "." + name + "'");
        fields.put(name, v);
    }

    @Override
    public void setValue(Value v) {
        fields = ((ValueObject) v).fields;
    }

    @Override
    protected void toString(StringBuilder sb, Set<Value> done) {
        if (isNull()) {
            sb.append("null");
            return;
        }
        if (done.contains(this)) {
            sb.append(toStringIdentity());
            return;
        }
        done.add(this);

        sb.append("{");
        if (fields != null) {
            List<String> fnames = new ArrayList<>(fields.size());
            fnames.addAll(fields.keySet());
            Collections.sort(fnames);
            for (int i = 0; i < fnames.size(); i++) {
                String fn = fnames.get(i);
                if (isHiddenField(fn)) continue; // Don't show hidden fields
                Value val = fields.get(fn);
                sb.append((i > 0 ? ", " : " ") + fn + ": ");
                if (val != null) val.toString(sb, done);
                else sb.append("null");
            }
        }
        sb.append(" }");
    }

}
