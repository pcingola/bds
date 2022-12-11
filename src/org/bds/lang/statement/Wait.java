package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.Expression;
import org.bds.lang.expression.ExpressionNew;
import org.bds.lang.expression.ReferenceThis;
import org.bds.lang.nativeMethods.list.MethodNativeListAdd;
import org.bds.lang.type.TypeClass;
import org.bds.lang.type.TypeList;
import org.bds.lang.type.Types;
import org.bds.lang.value.LiteralString;
import org.bds.lang.value.ValueFunction;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

import static org.bds.libraries.LibraryException.CLASS_NAME_WAIT_EXCEPTION;

/**
 * A "wait" statement
 *
 * @author pcingola
 */
public class Wait extends Statement {

    private static final long serialVersionUID = -6803518993665623097L;

    String errMsg;
    Expression taskId;
    ExpressionNew newWaitException; // Declare a 'new WaitException(message)', to be used when the 'wait' statement fails

    public Wait(BdsNode parent, ParseTree tree) {
        super(parent, tree);
    }

    /**
     * Create a 'new WaitException(errorMessage)' expression
     */
    ExpressionNew createNewWaitException(String errMsg) {
        // Create a 'new WaitException' expression
        var exprNew = new ExpressionNew(this, null);

        // Set function name: A constructor is a method that has the same name as the class
        exprNew.setFunctionName(CLASS_NAME_WAIT_EXCEPTION);

        // Add constructor arguments
        Args args = new Args(exprNew, null);

        // Expression literal string 'errMsg'
        LiteralString errMsgLiteral = new LiteralString(args, null);
        errMsgLiteral.setValueInterpolate(errMsg);
        args.add(errMsgLiteral);
        exprNew.setArgs(args);

//        // Create reference to 'this' object (the WaitException object)
//        TypeClass thisType = (TypeClass) Types.get(CLASS_NAME_WAIT_EXCEPTION);
//        ReferenceThis expresionThis = new ReferenceThis(this, thisType);
//
//        // Update arguments to point to 'this'
//        Args argsThis = Args.getArgsThis(args, expresionThis);
//        exprNew.setArgs(argsThis);

        return exprNew;
    }

    @Override
    protected void parse(ParseTree tree) {
        // child[0] = 'wait'
        if (tree.getChildCount() > 1) taskId = (Expression) factory(tree, 1);

        // Create a 'new WaitExpression(...)' expression
        errMsg = "Error in wait statement, file " + getFileName() + ", line " + getLineNum();
        newWaitException = createNewWaitException(errMsg);
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toAsm());

        String labelBase = baseLabelName();
        String labelOk = labelBase + "ok";
        String labelFail = labelBase + "fail";

        // No arguments? Wait for all tasks
        if (taskId == null) {
            sb.append(OpCode.WAITALL + "\n");
        } else if (taskId.isList()) {
            // Wait for a list of taskIds
            sb.append(taskId.toAsm());
            sb.append(OpCode.WAIT + "\n");
        } else {
            // Wait for a single taskId: We need to pass a list of one element

            // Create an empty list, add single element to it
            TypeList listString = TypeList.get(Types.STRING);
            SymbolTable symtab = listString.getSymbolTable();
            ValueFunction methodAdd = symtab.findFunction(MethodNativeListAdd.class);

            sb.append(OpCode.NEW + " " + listString + "\n");
            sb.append(taskId.toAsm());
            sb.append(OpCode.CALLNATIVE + " " + methodAdd + "\n");

            // Now we have a list of elements to wait
            sb.append(OpCode.WAIT + "\n");
        }

        // Wait opcode returns 'true' on success, 'false' on failure
        sb.append(OpCode.JMPT + " " + labelOk + "\n");

        // Failed 'wait' statement: This code is executed when 'wait' fails
        sb.append(labelFail + ":\n");
        sb.append(toAsmFail());

        // Succeeded 'wait' statement: Jump here if 'wait' succeeds
        sb.append(labelOk + ":\n");

        return sb.toString();
    }

    // Failed 'wait' statement: This code is executed when 'wait' fails
    protected String toAsmFail() {
        StringBuilder sb = new StringBuilder();

        // Add a 'new WaitException( ... )' expression
        sb.append(newWaitException.toAsm());

        // Throw the newly create WaitException, which is in the stack
        sb.append(OpCode.THROW + "\n");

        return sb.toString();
    }

    // Failed 'wait' statement: This code is executed when 'wait' fails
    protected String toAsmFailOri() {
        StringBuilder sb = new StringBuilder();
        sb.append(OpCode.PUSHS + " '" + errMsg + "'\n");
        sb.append(OpCode.ERROR + "\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName().toLowerCase() + (taskId != null ? taskId : "") + "\n";
    }
}
