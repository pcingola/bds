package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.statement.Args;
import org.bds.lang.statement.ClassDeclaration;
import org.bds.lang.statement.FieldDeclaration;
import org.bds.lang.statement.MethodCall;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeClass;
import org.bds.lang.type.Types;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * Operator 'new' calls a constructor method
 *
 * @author pcingola
 */
public class ExpressionNew extends MethodCall {

    public static final String THIS_KEYWORD = "this";
    private static final long serialVersionUID = -4273100354848715681L;

    // Operator 'new' calls a constructor method
    public ExpressionNew(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    @Override
    protected void parse(ParseTree tree) {
        // Parsing code example: 'new ClassName( args... )'
        // Note that the constructor is invoked by the 'new' operator
        expresionThis = null; // Note that object 'this' does not exist yet

        // First child: 'new', we don't need to parse it
        // Second child: ClassName
        // By definition the constructor is a method that has the same name
        // as the class, so we invoke a method having the same name as the class
        functionName = tree.getChild(1).getText();

        // Third child is open parenthesis '(', we don't need to parse it
        // Parse arguments: All next childs until the last one
        // Last child is a closing parenthesis, no parsing
        args = new Args(this, null);
        args.parse(tree, 3, tree.getChildCount() - 1);

        // If there are no arguments (empty arguments) we must have
        // an empty args object
        if (args == null) args = new Args(this, null);
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        if (returnType != null) return returnType;

        // Calculate return types for expr and args
        // Note that expresionObj is null in ExpressionNew (which is a MethodCall)
        TypeClass thisType = (TypeClass) Types.get(functionName); // Constructors have same name as class
        if (thisType == null) return null;
        returnType = thisType;

        // Prepend 'this' argument to method signature
        expresionThis = new ReferenceThis(this, thisType);
        args = Args.getArgsThis(args, expresionThis);

        // Calculate return type for args
        args.returnType(symtab, compilerMessages);

        // Find method
        functionDeclaration = findMethod(symtab, thisType, args);

        return returnType;
    }

    @Override
    protected String signature() {
        StringBuilder sig = new StringBuilder();

        if (expresionThis == null) return "null";

        Type classType = expresionThis.getReturnType();
        sig.append(classType != null ? classType : "null");
        sig.append(".");
        sig.append(functionName);
        sig.append("(");
        for (int i = 1; i < args.size(); i++) {
            sig.append(args.getArguments()[i].getReturnType());
            if (i < (args.size() - 1)) sig.append(",");
        }
        sig.append(")");
        return sig.toString();
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(toAsmNode());

        // Use internal symbol to avoid collision
        // e.g.:
        //        class A {
        //            ...
        //            void f() {
        //                # Constructor for object 'B' uses 'this' is an argument.
        //                # Note that 'this' means object 'A', so the constructor
        //                # for method B cannot have also a variable called 'this'
        //                B b = new B(this)
        //
        // To solve the name collision, we just call the variable '$this' instead of 'this'
        String thisName = SymbolTable.INTERNAL_SYMBOL_START + ClassDeclaration.VAR_THIS;

        // This is like a function call that initializes fields, so
        // we need a scope and variable 'this' has to be set
        sb.append(OpCode.SCOPEPUSH + "\n");
        sb.append(OpCode.NEW + " " + expresionThis.getReturnType() + "\n");
        sb.append(OpCode.VARPOP + " " + THIS_KEYWORD + "\n"); // Field initialization may use 'this' reference to new object, so we create a variable 'this'
        sb.append(toAsmInitFields()); // Initialize all fields
        sb.append(OpCode.LOAD + " " + THIS_KEYWORD + "\n"); // Leave new object in the stack
        sb.append(OpCode.SCOPEPOP + "\n"); // Remove scope (wipes out variable 'this' as well)

        // Call constructor method
        sb.append(OpCode.SCOPEPUSH + "\n"); // Create new scope
        sb.append(OpCode.VAR + " " + thisName + "\n"); // Create new variable '$this' (to avoid name collisions)
        sb.append(args.toAsmNoThis());
        sb.append(toAsmCall());
        sb.append(OpCode.POP + "\n"); // Ignore return value (it's void)
        sb.append(OpCode.LOAD + " " + thisName + "\n"); // Leave the new (initialized) object in the stack
        sb.append(OpCode.SCOPEPOP + "\n");

        return sb.toString();
    }

    /**
     * Field initialization
     */
    String toAsmInitFields() {
        StringBuilder sb = new StringBuilder();
        TypeClass tthis = (TypeClass) expresionThis.getReturnType();

        for (ClassDeclaration cd = tthis.getClassDeclaration(); cd != null; cd = cd.getClassDeclarationParent()) {
            FieldDeclaration[] fieldDecls = cd.getFieldDecl();
            for (FieldDeclaration fieldDecl : fieldDecls) {
                sb.append(fieldDecl.toAsm());
            }
        }

        return sb.toString();
    }

    public String prettyPrint(String sep) {
        return sep + "new " + functionName + "( " + args + " )";
    }

    @Override
    protected void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        // Could not find the function?
        if (functionDeclaration == null) {
            if (expresionThis == null) compilerMessages.add(this, "Constructor cannot be resolved", MessageType.ERROR);
            else compilerMessages.add(this, "Constructor '" + signature() + "' cannot be resolved", MessageType.ERROR);
        }
    }

}
