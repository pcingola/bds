package org.bds.lang.expression;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.nativeMethods.list.MethodNativeListAdd;
import org.bds.lang.nativeMethods.list.MethodNativeListAddList;
import org.bds.lang.type.Type;
import org.bds.lang.type.TypeList;
import org.bds.lang.type.Types;
import org.bds.lang.value.ValueFunction;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

import java.util.ArrayList;

/**
 * Dependency operator '<-'
 *
 * @author pcingola
 */
public class ExpressionDepOperator extends Expression {

    public static final String DEP_OPERATOR = "<-";
    public static final String DEP_SEPARATOR = ",";
    private static final long serialVersionUID = -8557838570116658027L;
    Expression[] left;
    Expression[] right;

    public ExpressionDepOperator(BdsNode parent, Expression[] left, Expression[] right) {
        super(parent, null);
        this.left = left;
        this.right = right;
    }

    public ExpressionDepOperator(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    public Expression[] getLeft() {
        return left;
    }

    public Expression[] getRight() {
        return right;
    }

    @Override
    public boolean isReturnTypesNotNull() {
        return true;
    }

    @Override
    protected void parse(ParseTree tree) {
        // Find 'dependency' operator (i.e. '<-')
        int depIdx = indexOf(tree, "<-");

        // Create lists of expressions
        ArrayList<Expression> listl = new ArrayList<>(); // Operator's left side
        ArrayList<Expression> listr = new ArrayList<>(); // Operator's right side
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (isTerminal(tree, i, DEP_OPERATOR) || isTerminal(tree, i, DEP_SEPARATOR)) {
                // Do not add this node
            } else if (i < depIdx) listl.add((Expression) factory(tree, i)); // Add to left
            else if (i > depIdx) listr.add((Expression) factory(tree, i)); // Add to right
        }

        // Create arrays
        left = listl.toArray(new Expression[0]);
        right = listr.toArray(new Expression[0]);
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        // Make sure we calculate return type fo all expressions
        for (Expression e : left)
            e.returnType(symtab, compilerMessages);

        for (Expression e : right)
            e.returnType(symtab, compilerMessages);

        returnType = Types.BOOL;
        return returnType;
    }

    @Override
    public String toAsm() {
        return toAsm(null, null, true);
    }

    public String toAsm(String varInputs, String varOutputs, boolean pushDeps) {
        StringBuilder sb = new StringBuilder();
        sb.append(toAsmArray(left, varOutputs, pushDeps));
        sb.append(toAsmArray(right, varInputs, pushDeps));
        sb.append(OpCode.DEP + "\n");

        return sb.toString();
    }

    /**
     * Evaluate all expressions in the array.
     * Append all results to 'varName' (if varName is not null)
     */
    public String toAsmArray(Expression[] exprs, String varName, boolean pushDeps) {
        StringBuilder sb = new StringBuilder();

        // Create list
        TypeList listString = TypeList.get(Types.STRING);
        sb.append(OpCode.NEW + " " + listString + "\n");

        // Find 'list.add()' methods
        SymbolTable symtab = listString.getSymbolTable();
        ValueFunction methodAdd = symtab.findFunction(MethodNativeListAdd.class);
        ValueFunction methodAddList = symtab.findFunction(MethodNativeListAddList.class);

        // Evaluate all expression and add results to list
        for (Expression e : exprs) {
            sb.append(e.toAsm()); // Evaluate expression

            // Add result(s) to list
            if (e.getReturnType().isList()) sb.append(OpCode.CALLNATIVE + " " + methodAddList + "\n");
            else sb.append(OpCode.CALLNATIVE + " " + methodAdd + "\n");
        }

        // Append all results to 'varName'?
        if (varName != null && pushDeps) {
            // Copy results to tmp variable
            //     tmp := [results]
            String tmp = baseVarName() + "tmp";
            sb.append(OpCode.VARPOP + " " + tmp + "\n");

            // Append 'tmp' to 'varName'
            sb.append(OpCode.LOAD + " " + varName + "\n");
            sb.append(OpCode.LOAD + " " + tmp + "\n");
            sb.append(OpCode.CALLNATIVE + " " + methodAddList + "\n");
            sb.append(OpCode.POP + "\n");

            // Leave original results in the stack
            sb.append(OpCode.LOAD + " " + tmp + "\n");
        }

        return sb.toString();
    }

    public String prettyPrint(String sep) {
        StringBuilder sb = new StringBuilder();
        sb.append(sep);

        if (left.length == 1) {
            sb.append(left[0].prettyPrint(""));
        } else {
            sb.append("[ ");
            for (int i = 0; i < left.length; i++) {
                sb.append(left[i].prettyPrint(""));
                if (i < left.length) sb.append(",");
            }
            sb.append(" ]");
        }

        sb.append(" <- ");

        if (right.length == 1) {
            sb.append(right[0].prettyPrint(""));
        } else {
            sb.append("[ ");
            for (int i = 0; i < right.length; i++) {
                sb.append(right[i].prettyPrint(""));
                if (i < right.length) sb.append(",");
            }
            sb.append(" ]");
        }

        return sb.toString();
    }

    @Override
    protected void typeCheckNotNull(SymbolTable symtab, CompilerMessages compilerMessages) {
        // Check that expression lists are either strings or lists of strings
        for (Expression e : left)
            if (e.isString()) ; // OK
            else if (e.isList(Types.STRING)) ; //
            else compilerMessages.add(e, "Expression should be string or string[], got '" + e.getReturnType() + "'", MessageType.ERROR);

        for (Expression e : right)
            if (e.isString()) ; // OK
            else if (e.isList(Types.STRING)) ; //
            else compilerMessages.add(e, "Expression should be string or string[]", MessageType.ERROR);
    }

}
