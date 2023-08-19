package org.bds.languageServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bds.compile.BdsNodeWalker;
import org.bds.compile.CompilerMessage;
import org.bds.compile.CompilerMessages;
import org.bds.lang.BdsNode;
import org.bds.lang.Parameters;
import org.bds.lang.ProgramUnit;
import org.bds.lang.expression.Reference;
import org.bds.lang.statement.ClassDeclaration;
import org.bds.lang.statement.FieldDeclaration;
import org.bds.lang.statement.FunctionCall;
import org.bds.lang.statement.FunctionDeclaration;
import org.bds.lang.statement.MethodDeclaration;
import org.bds.lang.statement.VarDeclaration;
import org.bds.lang.statement.VariableInit;
import org.bds.lang.type.Type;
import org.bds.lang.value.ValueFunction;
import org.bds.symbol.GlobalSymbolTable;
import org.bds.symbol.SymbolTable;

public class LspServices {
    
    public static final String REFERENCE_NAME_STRING = Reference.class.getCanonicalName();

    private ProgramUnit programUnit;
    private CompilerMessages compilerMessages;
    private Set<BdsNode> visited;
    private boolean skipNative = true;
    
    public LspServices(ProgramUnit programUnit, CompilerMessages compilerMessages) {
        this.programUnit = programUnit;
        this.compilerMessages = compilerMessages;
        visited = new HashSet<>();
    }

    private String fileLine2json(BdsNode bdsNode, String tabs) {
        visited.add(bdsNode);
        if( bdsNode.getFile() == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + "\"fileName\": \"" + bdsNode.getFileNameCanonical() + "\",\n");
        sb.append(tabs + "\"lineNum\": " + bdsNode.getLineNum() + ",\n");
        sb.append(tabs + "\"charPosInLine\": " + bdsNode.getCharPosInLine() + ",\n");
        return sb.toString();
    }

    /**
     * Create JSON string representation of the given class declaration.
     */
    private String class2json(ClassDeclaration classDecl, String tabs) {
        visited.add(classDecl);
        if( classDecl.getFile() == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + "\"" + classDecl.getCanonicalName() + "\": {\n");
        sb.append(tabs + "  \"className\": \"" + classDecl.getClassName() + "\",\n");
        sb.append(fileLine2json(classDecl, tabs + "  "));
        // Fields
        sb.append(tabs + "  \"fields\": {\n");
        for(FieldDeclaration fd: classDecl.getFieldDecl()) {
            sb.append(variableDeclaration2json(fd, tabs + "    "));
        }
        sb.append(tabs + "  },\n");
        // Methods
        sb.append(tabs + "  \"methods\": [\n");
        for(MethodDeclaration md: classDecl.getMethodDecl()) {
            sb.append(function2json(md, tabs + "    "));
        }
        sb.append(tabs + "  ],\n");
        sb.append(tabs + "},\n");
        return sb.toString();
    }

    String classes2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"classes\": {\n");
        for(BdsNode bdsNode: nodes) {
            if( visited.contains(bdsNode) ) continue;
            if( bdsNode instanceof ClassDeclaration ) sb.append(class2json((ClassDeclaration)bdsNode, "    "));
        }
        sb.append("  },\n");
        return sb.toString();
    }

    private String compilerMessages2json() {
        StringBuilder sb = new StringBuilder();
        sb.append("  \"ComplieMessages\": [\n");
        for(CompilerMessage cm: compilerMessages) {
            sb.append("    {\n");
            sb.append("      \"fileName\": \"" + cm.getFileName() + "\", \n");
            sb.append("      \"lineNumber\": " + cm.getLineNum() + ", \n");
            sb.append("      \"charPosInLine\": " + cm.getCharPosInLine() + ", \n");
            sb.append("      \"type\": \"" + cm.getType() + "\", \n");
            sb.append("      \"message\": \"" + cm.getMessage() + "\", \n");
            sb.append("    },\n");
        }
        sb.append("  ],\n");
        return sb.toString();
    }

    /**
     * Returns a JSON string representation of the given function declaration.
     */
    private String function2json(FunctionDeclaration funcDecl, String tabs) {
        visited.add(funcDecl);
        if( funcDecl.isNative()  && skipNative ) return ""; 
        if( funcDecl.getFile() == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + "\"" + funcDecl.getCanonicalName() + "\": {\n");
        var fname = funcDecl.isMethod() ? "methodName" : "functionName";
        sb.append(tabs + "  \"" + fname + "\": \"" + funcDecl.getFunctionName() + "\",\n");
        sb.append(tabs + "  \"signature\": \"" + funcDecl.signature() + "\",\n");
        sb.append(tabs + "  \"isNative\": \"" + funcDecl.isNative() + "\",\n");
        sb.append(fileLine2json(funcDecl, tabs + "  "));
        // Function parameters
        Parameters parameters = funcDecl.getParameters();
        visited.add(parameters);
        sb.append(tabs + "  \"parameters\": {\n");
        for(VarDeclaration varDecl: parameters.getVarDecl()) {
            sb.append(variableDeclaration2json(varDecl, tabs + "    "));
        }
        sb.append(tabs + "  },\n");
        sb.append(tabs + "},\n");
        return sb.toString();
    }

    private String functionCall2json(FunctionCall funcCall, String tabs) {
        visited.add(funcCall);
        if( funcCall.getFile() == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + "{\n");
        sb.append(tabs + "  \"functionName\": \"" + funcCall.getFunctionName() + "\",\n");
        var canonicalName = funcCall.getFunctionDeclaration() != null? funcCall.getFunctionDeclaration().getCanonicalName() : null;
        sb.append(tabs + "  \"canonicalName\": \"" +  canonicalName + "\",\n");
        sb.append(fileLine2json(funcCall, tabs + "  "));
        sb.append(tabs + "},\n");
        return sb.toString();
    }

    String functionCalls2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"functionCalls\": [\n");
        for(BdsNode bdsNode: nodes) {
            if( visited.contains(bdsNode) ) continue;
            if( bdsNode instanceof FunctionCall ) sb.append(functionCall2json((FunctionCall)bdsNode, "    "));
        }
        sb.append("  ],\n");
        return sb.toString();
    }

    String functions2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"functions\": {\n");
        for(BdsNode bdsNode: nodes) {
            if( visited.contains(bdsNode) ) continue;
            if( bdsNode instanceof FunctionDeclaration ) sb.append(function2json((FunctionDeclaration)bdsNode, "    "));
        }
        sb.append("  },\n");
        return sb.toString();
    }

    /**
     * JSON string representation of the given symbol table.
     */
    private String symbolTable2Json(SymbolTable symbolTable, String tabs) {
        StringBuilder sb = new StringBuilder();
        // Variables
        sb.append(tabs + "\"variables\": {\n");
        for(String varName: symbolTable) {
            Type varType = symbolTable.getVariableTypeLocal(varName);
            sb.append(tabs + "  \"" + varName + "\": \"" + varType + "\",\n");
        }
        sb.append(tabs + "}\n");
        return sb.toString();
    }

    public String toString() {
        visited.add(null); // Avoid visiting null nodes
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        // Compiler errors
        sb.append(compilerMessages2json());
        List<BdsNode> nodes = BdsNodeWalker.findNodes(programUnit, null, true, true);
        sb.append(classes2json(nodes));
        sb.append(functions2json(nodes));
        sb.append(variables2json(nodes));
        sb.append(variableReferences2json(nodes));
        sb.append(functionCalls2json(nodes));
        // Symbol table
        sb.append(",\n");
        sb.append(symbolTable2Json(GlobalSymbolTable.get(), "  "));
        sb.append("}\n");
        return sb.toString();
    }

    private String variableDeclaration2json(VarDeclaration vardecl, String tabs) {
        StringBuilder sb = new StringBuilder();
        visited.add(vardecl);
        if( vardecl.getFile() == null ) return "";
        for(VariableInit vi : vardecl.getVarInit()) {
            visited.add(vi);
            sb.append(tabs + "\"" + vi.getCanonicalName() + "\": {\n");
            sb.append(tabs + "  \"name\": \"" + vi.getVarName() + "\",\n");
            sb.append(tabs + "  \"type\": \"" + vi.getReturnType() + "\",\n");
            sb.append(fileLine2json(vi, tabs + "  "));
            sb.append(tabs + "},\n");
        }
        return sb.toString();
    }

    String variables2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"variables\": {\n");
        visited = new HashSet<BdsNode>();
        for(BdsNode bdsNode: nodes) {
            if( visited.contains(bdsNode) ) continue;
            if( bdsNode instanceof VarDeclaration) sb.append(variableDeclaration2json((VarDeclaration)bdsNode, "    "));
        }
        sb.append("  },\n");
        return sb.toString();
    }

    private String variableReference2json(Reference ref, String tabs) {
        StringBuilder sb = new StringBuilder();
        visited.add(ref);
        if( ref.getFile() == null ) return "";
        sb.append(tabs + "{\n");
        var refType = ref.getClass().getCanonicalName();
        if( refType.startsWith(REFERENCE_NAME_STRING)) refType = refType.substring(REFERENCE_NAME_STRING.length());
        sb.append(tabs + "  \"reference\": \"" + refType + "\",\n");
        sb.append(tabs + "  \"type\": \"" + ref.getReturnType() + "\",\n");
        sb.append(tabs + "  \"refName\": \"" + ref.getVariableName() + "\",\n");
        sb.append(tabs + "  \"refCanonicalName\": \"" + ref.getVariableCanonicalName() + "\",\n");
        sb.append(fileLine2json(ref, tabs + "  "));
        sb.append(tabs + "},\n");
        return sb.toString();
    }

    private String variableReferences2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"references\": [\n");
        for(BdsNode bdsNode: nodes) {
            if( visited.contains(bdsNode) ) continue;
            if(bdsNode instanceof Reference) sb.append(variableReference2json((Reference)bdsNode, "    "));
        }
        sb.append("  ],\n");
        return sb.toString();
    }
}
