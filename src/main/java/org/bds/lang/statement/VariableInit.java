package org.bds.lang.statement;

import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.expression.Expression;
import org.bds.lang.type.Type;
import org.bds.lang.value.LiteralListEmpty;
import org.bds.lang.value.LiteralMapEmpty;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

/**
 * Variable initialization
 * E.g.:
 * int i = 3
 *
 * @author pcingola
 */
public class VariableInit extends BdsNode {

    private static final long serialVersionUID = 2385160471242254601L;
    protected boolean fieldInit; // Is this a 'field' initialization in a class declaration?
    protected Expression expression;
    protected String help;
    protected VarDeclaration varDeclaration;
    protected String varName;

    public VariableInit(BdsNode parent, ParseTree tree) {
        super(parent, tree);
        fieldInit = false;
    }

    public static VariableInit get(BdsNode parent, String name, Expression expression) {
        VariableInit vi = new VariableInit(null, null);
        vi.parent = parent;
        vi.varName = name;
        vi.expression = expression;
        return vi;
    }

    public static VariableInit get(String name) {
        VariableInit vi = new VariableInit(null, null);
        vi.varName = name;
        return vi;
    }

    public static VariableInit get(String name, Expression expression) {
        VariableInit vi = new VariableInit(null, null);
        vi.varName = name;
        vi.expression = expression;
        return vi;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getHelp() {
        return help;
    }

    public String getName() {
        return varName;
    }

    public String getVarName() {
        return varName;
    }

    public boolean isFieldInit() {
        return fieldInit;
    }

    public void setFieldInit(boolean fieldInit) {
        this.fieldInit = fieldInit;
    }

    @Override
    protected void parse(ParseTree tree) {
        int idx = 0;
        varName = tree.getChild(idx++).getText();

        // Initialization expression
        if (isTerminal(tree, idx, "=")) {
            idx++;
            expression = (Expression) factory(tree, idx++);
        }

        // Help string
        ParseTree node = tree.getChild(idx++);
        if (node != null && node.getText().startsWith("help")) {
            help = node.getText().substring("help ".length()).trim();
        }
    }

    public void setType(Type type) {
        returnType = type;
    }

    public void setVarDeclaration(VarDeclaration varDeclaration) {
        this.varDeclaration = varDeclaration;
    }

    @Override
    public String toAsm() {
        return fieldInit ? toAsmFieldInit() : toAsmVarInit();
    }

    /**
     * Cast value
     */
    String toAsmCast() {
        if (varDeclaration == null || expression == null) return "";

        Type et = expression.getReturnType();
        Type vdt = varDeclaration.getType();
        if (vdt.equals(et)) return "";

        // Different types, we need to cast
        if (vdt.isBool()) return OpCode.CAST_TOB + "\n";
        else if (vdt.isInt()) return OpCode.CAST_TOI + "\n";
        else if (vdt.isReal()) return OpCode.CAST_TOR + "\n";
        else if (vdt.isString()) return OpCode.CAST_TOS + "\n";
        else if (vdt.isClass() && et.isClass()) return ""; // Class to class, no need to cast
        else if (vdt.isList() && et.isList() && (expression instanceof LiteralListEmpty)) return ""; // Empty, no need to cast
        else if (vdt.isMap() && et.isMap() && (expression instanceof LiteralMapEmpty)) return ""; // Empty, no need to cast
        compileError("Cannot cast from '" + et + "' to '" + vdt + "'");
        return "";
    }

    /**
     * Default value initialization
     */
    public String toAsmDefaultValue() {
        if (returnType != null) return returnType.toAsmDefaultValue();
        compileError("Unknown default value for type '" + returnType + "'");
        return "";
    }

    String toAsmFieldInit() {
        if (expression == null) return "";
        return super.toAsm() //
                + expression.toAsm() //
                + (help != null ? "# help: " + help + "\n" : "") //
                + toAsmCast() //
                + OpCode.LOAD + " " + ClassDeclaration.VAR_THIS + "\n" //
                + OpCode.SETFIELDPOP + " " + varName + "\n"//
                ;
    }

    String toAsmVarInit() {
        return super.toAsm() //
                + (expression != null ? expression.toAsm() : toAsmDefaultValue()) //
                + (help != null ? "# help: " + help + "\n" : "") //
                + toAsmCast() //
                + OpCode.VARPOP + " " + varName + "\n" //
                ;
    }

    public String prettyPrint(String sep) {
        return sep + varName //
                + (expression != null ? " = " + expression.prettyPrint("") : "") //
                + (help != null ? " help " + help : "") //
                ;
    }

    @Override
    public void typeCheck(SymbolTable symtab, CompilerMessages compilerMessages) {
        // Variable type
        Type varType = symtab.getVariableTypeLocal(varName);

        // Calculate expression type
        if (expression != null) {
            Type exprRetType = expression.returnType(symtab, compilerMessages);

            // Compare types
            if ((varType == null) || (exprRetType == null)) {
                // Variable not found, nothing else to do
            } else if (varType.isList() && exprRetType.isList() && (expression instanceof LiteralListEmpty)) {
                // OK, Empty list literal can be assigned to any list
            } else if (varType.isMap() && exprRetType.isMap() && (expression instanceof LiteralMapEmpty)) {
                // OK, Empty map literal can be assigned to any map
            } else if (!exprRetType.canCastTo(varType)) {
                // We cannot cast expression's type to variable's type: Error
                compilerMessages.add(this, "Cannot cast " + exprRetType + " to " + varType, MessageType.ERROR);
            }
        }
    }
}
