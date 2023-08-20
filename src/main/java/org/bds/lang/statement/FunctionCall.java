package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.Expression;
import org.bds.lang.expression.ReferenceThis;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeClass;
import org.bds.lang.value.ValueFunction;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * Function call
 * <p>
 * Note: A method call is the same as a function call
 * using 'this' as first argument.
 *
 * @author pcingola
 */
public class FunctionCall extends Expression {

    private static final long serialVersionUID = 6677584957911766940L;

    protected String functionName;
    protected Args args;
    protected FunctionDeclaration functionDeclaration;

    public FunctionCall(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    /**
     * Find method (or function) matching the signature
     */
    protected FunctionDeclaration findMethod(SymbolTable symtab, Type type, Args args) {
        if (type == null) return null;

        // Find function in class or any super-class
        if (type.isClass()) {
            // A class' method
            for (TypeClass tc = (TypeClass) type; tc != null && tc.hasClassDeclaration(); tc = tc.getParentTypeClass()) {
                // Get symbol table
                SymbolTable classSymTab = tc.getSymbolTable();
                if (classSymTab == null) return null;

                // Find method in class symbol table
                ValueFunction vfunc = classSymTab.findFunction(functionName, args);
                if (vfunc != null) return vfunc.getFunctionDeclaration();

                // Try a 'regular' function, e.g. 'this.funcName()' => 'funcName(this)'
                vfunc = symtab.findFunction(functionName, args);
                if (vfunc != null) return vfunc.getFunctionDeclaration();
            }
        } else {
            // Another type of method call, e.g.: string.length()
            SymbolTable classSymTab = type.getSymbolTable();
            if (classSymTab == null) return null;

            // Find method in class symbol table
            ValueFunction vfunc = classSymTab.findFunction(functionName, args);
            if (vfunc != null) return vfunc.getFunctionDeclaration();
        }

        // Try a 'regular' function
        ValueFunction vfunc = symtab.findFunction(functionName, args);
        if (vfunc != null) return vfunc.getFunctionDeclaration();

        // Not found
        return null;
    }

    public Args getArgs() {
        return args;
    }

    public void setArgs(Args args) {
        this.args = args;
    }

    public FunctionDeclaration getFunctionDeclaration() {
        return functionDeclaration;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    protected boolean isMethodCall() {
        return false;
    }

    @Override
    public boolean isReturnTypesNotNull() {
        return true;
    }

    /**
     * Is this a 'super.f()' method call?
     */
    protected boolean isSuper() {
        return false;
    }

    @Override
    protected void parse(ParseTree tree) {
        // Parsing code example: 'functionName( arguments )'

        // First child is functionName
        functionName = tree.getChild(0).getText();

        // Child 1 is an open parenthesis: child[1] = '('
        // We don't need to parse it

        // Next we have function arguments, all childs except the last one
        args = new Args(this, null);
        args.parse(tree, 2, tree.getChildCount() - 1);

        // Last child is closing parenthesis: We don't need to parse
        //    child[tree.getChildCount()] = ')'

        if (args == null) args = new Args(this, null);
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (returnType != null) return returnType;

        args.returnType(symtab, compilerMessages);

        ValueFunction tfunc = symtab.findFunction(functionName, args);
        if (tfunc != null) {
            functionDeclaration = tfunc.getFunctionDeclaration();
            returnType = functionDeclaration.getReturnType();
        } else if (symtab.hasType(ClassDeclaration.VAR_THIS)) {
            // Is this function call within a class?
            // Try "this.functionName(...)", i.e. implicit 'this' object
            TypeClass typeThis = (TypeClass) symtab.resolve(ClassDeclaration.VAR_THIS);
            // Add first argument ('this')
            Expression expresionThis = new ReferenceThis(this, typeThis);
            Args argsThis = Args.getArgsThis(args, expresionThis);
            functionDeclaration = findMethod(symtab, typeThis, argsThis);

            if (functionDeclaration != null) { // Found method
                returnType = functionDeclaration.getReturnType();
                args = argsThis;
            }
        }

        return returnType;
    }

    protected String signature() {
        StringBuilder sig = new StringBuilder();
        sig.append(functionName);
        sig.append("(");
        for (int i = 0; i < args.size(); i++) {
            sig.append(args.getArguments()[i].getReturnType());
            if (i < (args.size() - 1)) sig.append(",");
        }
        sig.append(")");
        return sig.toString();
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());
        sb.append(args.toAsm());
        sb.append(toAsmCall());
        return sb.toString();
    }

    public String toAsmCall() {
        return toAsmCallType() + " " //
                + functionDeclaration.signature() //
                + "\n";
    }

    public OpCode toAsmCallType() {
        if (functionDeclaration.isNative()) return OpCode.CALLNATIVE;
        if (functionDeclaration.isMethod()) { //
            if (isMethodCall() && isSuper()) return OpCode.CALLSUPER;
            return OpCode.CALLMETHOD;
        }
        return OpCode.CALL;
    }

    public String prettyPrint(String sep) {
        return functionName + "( " + args.prettyPrint("") + " )";
    }

    @Override
    protected void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        // Could not find the function?
        if (functionDeclaration == null) {
            compilerMessages.add(this, "Function " + signature() + " cannot be resolved", MessageType.ERROR);
        }
    }
}
