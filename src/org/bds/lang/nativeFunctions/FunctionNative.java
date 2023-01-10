package org.bds.lang.nativeFunctions;

import org.bds.compile.CompilerMessages;
import org.bds.lang.statement.FunctionDeclaration;
import org.bds.lang.value.Value;
import org.bds.run.BdsThread;
import org.bds.scope.GlobalScope;
import org.bds.symbol.GlobalSymbolTable;
import org.bds.symbol.SymbolTable;

/**
 * A native function declaration
 *
 * @author pcingola
 */
public abstract class FunctionNative extends FunctionDeclaration {

    private static final long serialVersionUID = 5510708631419216087L;

    public FunctionNative() {
        super(null, null);
        initFunction();
        parameterNames = parameterNames();
    }

    /**
     * Add method to global scope and global symbol table
     */
    protected void addNativeFunction() {
        GlobalScope.get().add(this);
        GlobalSymbolTable.get().addFunction(this);
    }

    /**
     * Initialize method parameters (if possible)
     */
    protected abstract void initFunction();

    @Override
    public boolean isNative() {
        return true;
    }

    /**
     * Run a native method, wrap result in a 'Value'
     */
    public Value runFunction(BdsThread bdsThread) {
        Object ret = runFunctionNative(bdsThread);
        return returnType.newValue(ret);
    }

    /**
     * Run a native method
     */
    protected abstract Object runFunctionNative(BdsThread bdsThread);

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append(sep + returnType.prettyPrint("") + " " + functionName + "( " + parameters.prettyPrint("") + " ) {\n");
        sb.append(sep + SEP + "// Native '" + this.getClass().getCanonicalName() + "'\n");
        sb.append(sep + "}\n");
        return sb.toString();
    }

    @Override
    public void typeChecking(SymbolTable symtab, CompilerMessages compilerMessages) {
        // Nothing to do
    }

}
