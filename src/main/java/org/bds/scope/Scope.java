package org.bds.scope;

import org.bds.lang.BdsNode;
import org.bds.lang.statement.FunctionDeclaration;
import org.bds.lang.type.Type;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueFunction;
import org.bds.util.Gpr;
import org.bds.util.GprString;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Scope: Variables, functions and classes
 *
 * @author pcingola
 */
public class Scope implements Iterable<String>, Serializable, ValuesGetSet {

    private static final long serialVersionUID = -4041502602912302092L;
    private static int scopeNum = 0;

    int id;
    Scope parent;
    Map<String, Value> values;
    BdsNode node;

    public Scope() {
        id = nextId();
        parent = GlobalScope.get();
        node = null;
        values = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param parent : If null => use global Scope
     */
    public Scope(Scope parent, BdsNode node) {
        id = nextId();
        this.parent = parent;
        this.node = node;
        values = new HashMap<>();
    }

    protected static int nextId() {
        return ++scopeNum;
    }

    public synchronized void add(FunctionDeclaration fdecl) {
        ValueFunction vf = new ValueFunction(fdecl);
        values.put(fdecl.signature(), vf);
    }

    /**
     * Add a value to scope using a primitive object
     */
    public synchronized void add(String name, Object val) {
        Value value = Value.factory(val);
        values.put(name, value);
    }

    /**
     * Add a new variable of type 'type'
     */
    public synchronized void add(String name, Type type) {
        values.put(name, type.newDefaultValue());
    }

    /**
     * Add a value to scope
     */
    public synchronized void add(String name, Value value) {
        values.put(name, value);
    }

    /**
     * Return all variable names in the scope and all parent scopes
     */
    public Collection<String> getNames() {
        Set<String> names = new HashSet<>();
        for (Scope scope = this; scope != null; scope = scope.parent) names.addAll(scope.getNamesLocal());
        return names;
    }

    /**
     * Return all (local) names in the scope (including function names)
     */
    public Collection<String> getNamesLocal() {
        return values.keySet();
    }

    public BdsNode getNode() {
        return node;
    }

    public Scope getParent() {
        return parent;
    }

    public String getScopeName() {
        if (node == null) return "" + id;
        return (node.getFileName() != null ? node.getFileName() + ":" + node.getLineNum() + ":" : "") + node.getClass().getSimpleName();
    }

    public String getScopeNameCode() {
        if (node == null) return "" + id;
        String[] lines = node.toString().split("\n");
        String line = lines[0];
        return (node.getFileName() != null ? node.getFileName() + ":" + node.getLineNum() + "\t" : "") + line;
    }

    /**
     * Get value in this scope or any parent scope
     */
    public Value getValue(String name) {
        // Find value on this or any parent scope
        for (Scope scope = this; scope != null; scope = scope.parent) {
            // Try to find a value
            Value v = scope.getValueLocal(name);
            if (v != null) return v;
        }

        // Nothing found
        return null;
    }

    /**
     * Get value in this scope
     * Note: 'Local' means that only searches this scope (not parent scopes)
     */
    public synchronized Value getValueLocal(String name) {
        return values.get(name);
    }

    /**
     * Return a collection of all values
     * Note: 'Local' means that only searches this scope (not parent scopes)
     */
    public Collection<Value> getValuesLocal() {
        return values.values();
    }

    /**
     * Return all variable names (i.e. exclude function names)
     * Note: 'Local' means that only searches this scope (not parent scopes)
     */
    public List<String> getVariableNamesLocal() {
        return values.keySet().stream().filter(this::isVariable).collect(Collectors.toList());
    }

    public boolean hasValue(String value) {
        return getValue(value) != null;
    }

    /**
     * Is value available on this scope or any parent scope?
     */
    public boolean hasValueLocal(String value) {
        return getValueLocal(value) != null;
    }

    /**
     * Is this scope empty?
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean isVariable(String name) {
        var val = getValueLocal(name);
        return val != null && !(val instanceof ValueFunction);
    }

    @Override
    public Iterator<String> iterator() {
        return values.keySet().iterator();
    }

    /**
     * Remove variable from scope
     */
    public synchronized void remove(String name) {
        values.remove(name);
    }

    /**
     * Add a value to scope
     */
    public synchronized void setValue(String name, Value value) {
        for (Scope s = this; s != null; s = s.parent) {
            if (s.values.containsKey(name)) {
                s.values.put(name, value);
                return;
            }
        }
        throw new RuntimeException("Could not find variable '" + name + "'");
    }

    @Override
    public String toString() {
        return toString(true, true);
    }

    public String toString(boolean showFunc, boolean recurse) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n# Scope " + getScopeNameCode() + "\n");
        sb.append(toStringLocal(showFunc));

        // Show parents
        if (recurse && parent != null) sb.append(parent.toString(showFunc, recurse));

        return sb.toString();
    }

    public String toStringLocal(boolean showFunc) {
        StringBuilder sb = new StringBuilder();

        // Show scope values
        List<String> names = new ArrayList<>();
        names.addAll(values.keySet());
        Collections.sort(names);
        int i = 0;
        for (String n : names) {
            Value v = getValueLocal(n);
            Type t = v.getType();

            if (t.isFunction() && showFunc) {
                String valStr = Gpr.prependEachLine("\t", v.toString());
                sb.append(i + " Function " + t.getPrimitiveType() + " " + valStr + "\n");
                i++;
            } else if (v.getType().isString()) {
                sb.append(i + " " + t + " : " + n + " = '" + GprString.escape(v.asString()) + "'\n");
                i++;
            } else {
                String valStr = Gpr.prependEachLine("\t", v.toString());
                sb.append(i + " " + t + " : " + n + " = " + valStr + "\n");
                i++;
            }
        }

        if (sb.length() <= 0) sb.append("Scope ID: " + id + ". Empty");
        else sb.insert(0, "Scope ID: " + id + ". Scope length: " + i + "\n");

        return sb.toString();
    }

    public String toStringScopeNames() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scopes:\n");

        int i = 0;
        for (Scope scope = this; scope != null; scope = scope.getParent())
            sb.append("\t" + (i++) + ": " + scope.getScopeName() + "\n");

        return sb.toString();
    }

}
