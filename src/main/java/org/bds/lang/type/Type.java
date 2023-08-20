package org.bds.lang.type;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.nativeMethods.MethodNative;
import org.bds.lang.statement.FunctionDeclaration;
import org.bds.lang.value.LiteralBool;
import org.bds.lang.value.Value;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Variable type
 */
public abstract class Type extends BdsNode implements Comparable<Type> {

    private static final long serialVersionUID = -2836048827087214442L;
    public static boolean debug = false;

    protected PrimitiveType primitiveType;
    protected SymbolTable symbolTable; // A type requires a SymbolTable to define all methods related to this type / class

    protected Type(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    public Type(PrimitiveType primitiveType) {
        super(null, null);
        this.primitiveType = primitiveType;
        initialize();
    }

    /**
     * Add all library methods here
     */
    protected void addNativeMethods() {
        List<MethodNative> methods = declateNativeMethods();
        for (MethodNative method : methods)
            getSymbolTable().addFunction(method);
    }

    /**
     * Can this type be cast to 'type'?
     */
    public boolean canCastTo(Type type) {
        return equals(type) || type.isAny();
    }

    public void checkCanCast(Type type, CompilerMessages compilerMessages) {
        if (returnType.isReturnTypesNotNull() && !returnType.canCastTo(type)) {
            compilerMessages.add(this, "Cannot cast " + type + " to " + returnType, MessageType.ERROR);
        }
    }

    /**
     * Compare types
     */
    @Override
    public int compareTo(Type type) {
        return primitiveType.compareTo(type.primitiveType);
    }

    /**
     * Declare native methods (default is 'object' level methods
     */
    protected List<MethodNative> declateNativeMethods() {
        return new ArrayList<>(); // Empty list
    }

    public boolean equals(Type type) {
        return primitiveType.equals(type.primitiveType) //
                || isAny() //
                || type.isAny() //
                ;
    }

    /**
     * Canonical name for this type
     */
    public String getCanonicalName() {
        // if( parent == null ) return primitiveType.toString();
        // return parent.getCanonicalName() + "." + primitiveType.toString();
        // return primitiveType.toString();
        return toString();
    }

    /**
     * Default value (native object). Most of the time this value is null
     *
     * @return
     */
    public Object getDefaultValueNative() {
        return null;
    }

    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    @Override
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    protected void initialize() {
        returnType = this;
        symbolTable = new SymbolTable(this);
    }

    /**
     * Is this type same as 'type'?
     */
    public boolean is(Type type) {
        return equals(type);
    }

    @Override
    public boolean isAny() {
        return false;
    }

    @Override
    public boolean isBool() {
        return false;
    }

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isInt() {
        return false;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public boolean isList(Type elementType) {
        return false;
    }

    @Override
    public boolean isMap() {
        return false;
    }

    @Override
    public boolean isMap(Type keyType, Type valueType) {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isReal() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    /**
     * Create a new (default) value for this 'type'
     */
    public abstract Value newDefaultValue();

    public Value newValue() {
        return newDefaultValue();
    }

    /**
     * Create a new map if this type and set it to 'v'
     */
    public abstract Value newValue(Object v);

    @Override
    protected void parse(ParseTree tree) {
        compileError("This method should never be called!");
    }

    /**
     * Resolve method call
     * Note: For non-class types, there is nothing to resolve
     */
    public FunctionDeclaration resolve(FunctionDeclaration fdecl) {
        return fdecl;
    }

    /**
     * Default value initialization
     */
    public String toAsmDefaultValue() {
        if (isBool()) return OpCode.PUSHB + " " + LiteralBool.BOOL_FALSE + "\n";
        else if (isInt()) return OpCode.PUSHI + " 0\n";
        else if (isReal()) return OpCode.PUSHR + " 0.0\n";
        else if (isString()) return OpCode.PUSHS + " ''\n";
        else if (isVoid()) return OpCode.PUSHI + " 0\n"; // Void won't be used anyways (so just use an int)
        else if (isList() || isMap()) return OpCode.NEW + " " + this + "\n";
        else if (isClass()) return OpCode.PUSHNULL + "\n";
        compileError("Unknown default value for type '" + this + "'");
        return null;
    }

    public String prettyPrint(String sep) {
        return primitiveType.toString();
    }

    public String toStringSerializer() {
        return toString();
    }

}
