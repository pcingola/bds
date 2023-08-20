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
import org.bds.symbol.GlobalSymbolTable;
import org.bds.symbol.SymbolTable;

public class LspData {
    
    public static final String REFERENCE_NAME_STRING = Reference.class.getCanonicalName();

    private ProgramUnit programUnit;
    private CompilerMessages compilerMessages;
    private Set<BdsNode> visited;

    public LspData(ProgramUnit programUnit, CompilerMessages compilerMessages) {
        this.programUnit = programUnit;
        this.compilerMessages = compilerMessages;
        visited = new HashSet<>();
    }

    /**
     * Create JSON string representation of the given class declaration.
     */
    private String class2json(ClassDeclaration classDecl, String tabs) {
        visited.add(classDecl);
        if( classDecl.getFile() == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + "\"" + classDecl.getCanonicalName() + "\": {\n");
        sb.append(tabs + "  \"className\": \"" + classDecl.getClassName() + "\"\n");
        sb.append(fileLocation2json(classDecl, tabs + "  "));
        // Fields
        sb.append(tabs + "  ,\"fields\": {\n");
        var isFirst = true;
        for(FieldDeclaration fd: classDecl.getFieldDecl()) {
            sb.append(variableDeclaration2json(fd, tabs + "    ", isFirst));
            isFirst = false;
        }
        sb.append(tabs + "  }\n");
        // Methods}
        sb.append(tabs + "  ,\"methods\": {\n");
        isFirst = true;
        for(MethodDeclaration md: classDecl.getMethodDecl()) {
            if( shouldVisit(md, MethodDeclaration.class)) {
                sb.append(function2json(md, tabs + "    ", isFirst));
                isFirst = false;
            }
        }
        sb.append(tabs + "  }\n");
        sb.append(tabs + "}\n");
        return sb.toString();
    }

    private String classes2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"classes\": {\n");
        var isFirst = true;
        for(BdsNode bdsNode: nodes) {
            if( shouldVisit(bdsNode, ClassDeclaration.class) ) {
                sb.append((isFirst ? "" : ",") + class2json((ClassDeclaration)bdsNode, "    "));
                isFirst = false;
            }
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

    private String fileLocation2json(BdsNode bdsNode, String tabs) {
        visited.add(bdsNode);
        if( bdsNode.getFile() == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + ",\"location\": {\n");
        sb.append(tabs + "  \"fileName\": \"" + bdsNode.getFileNameCanonical() + "\"\n");
        sb.append(tabs + "  ,\"lineNum\": " + bdsNode.getLineNum() + "\n");
        sb.append(tabs + "  ,\"charPosInLine\": " + bdsNode.getCharPosInLine() + "\n");
        sb.append(tabs + "}\n");
        return sb.toString();
    }

    /**
     * Returns a JSON string representation of the given function declaration.
     */
    private String function2json(FunctionDeclaration funcDecl, String tabs, boolean isFirst) {
        visited.add(funcDecl);
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + (isFirst ? "" : ",") + "\"" + funcDecl.getCanonicalName() + "\": {\n");
        var fname = funcDecl.isMethod() ? "methodName" : "functionName";
        sb.append(tabs + "  \"" + fname + "\": \"" + funcDecl.getFunctionName() + "\"\n");
        sb.append(tabs + "  ,\"signature\": \"" + funcDecl.signature() + "\"\n");
        sb.append(tabs + "  ,\"isNative\": \"" + funcDecl.isNative() + "\"\n");
        sb.append(fileLocation2json(funcDecl, tabs + "  "));
        // Function parameters
        Parameters parameters = funcDecl.getParameters();
        visited.add(parameters);
        sb.append(tabs + "  ,\"parameters\": {\n");
        var isFirstVardecl = true;
        for(VarDeclaration varDecl: parameters.getVarDecl()) {
            sb.append(variableDeclaration2json(varDecl, tabs + "    ", isFirstVardecl));
            isFirstVardecl = false;
        }
        sb.append(tabs + "  }\n");
        sb.append(tabs + "}\n");
        return sb.toString();
    }

    private String functionCall2json(FunctionCall funcCall, String tabs, boolean isFirst) {
        visited.add(funcCall);
        if( funcCall.getFile() == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(tabs + (isFirst ? "" : ",") + "{\n");
        sb.append(tabs + "  \"functionName\": \"" + funcCall.getFunctionName() + "\",\n");
        var canonicalName = funcCall.getFunctionDeclaration() != null? funcCall.getFunctionDeclaration().getCanonicalName() : null;
        sb.append(tabs + "  \"canonicalName\": \"" +  canonicalName + "\"\n");
        sb.append(fileLocation2json(funcCall, tabs + "  "));
        sb.append(tabs + "}\n");
        return sb.toString();
    }

    private String functionCalls2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"functionCalls\": [\n");
        var isFirst = true;
        for(BdsNode bdsNode: nodes) {
            if( shouldVisit(bdsNode, FunctionCall.class) ) {
                sb.append(functionCall2json((FunctionCall)bdsNode, "    ", isFirst));
                isFirst = false;
            }
        }
        sb.append("  ],\n");
        return sb.toString();
    }

    private String functions2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"functions\": {\n");
        var isFirst = true;
        for(BdsNode bdsNode: nodes) {
            if( shouldVisitFunctionDeclaration(bdsNode)) {
                sb.append(function2json((FunctionDeclaration)bdsNode, "    ", isFirst));
                isFirst = false;
            }
        }
        sb.append("  },\n");
        return sb.toString();
    }

    private boolean shouldVisit(BdsNode bdsNode, Class clazz) {
        return (bdsNode != null)
                && !visited.contains(bdsNode)
                && clazz.isInstance(bdsNode)
                && (bdsNode.getFile() != null);
    }

    private boolean shouldVisitFunctionDeclaration(BdsNode bdsNode) {
        return shouldVisit(bdsNode, FunctionDeclaration.class) && !((FunctionDeclaration) bdsNode).isNative();
    }

    /**
     * JSON string representation of the given symbol table.
     */
    private String symbolTable2Json(SymbolTable symbolTable, String tabs, String name) {
        StringBuilder sb = new StringBuilder();
        // Variables
        var isFirst = true;
        sb.append(tabs + "\"" + name + "\": {\n");
        for(String varName: symbolTable) {
            Type varType = symbolTable.getVariableTypeLocal(varName);
            sb.append(tabs + "  " + (isFirst ? "" : ",") + "\"" + varName + "\": \"" + varType + "\"\n");
            isFirst = false;
        }
        sb.append(tabs + "}\n");
        return sb.toString();
    }

    public String toString() {
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
        sb.append(symbolTable2Json(GlobalSymbolTable.get(), "  ", "globalsymboltable"));
        sb.append("}\n");
        return sb.toString();
    }

    private String variableDeclaration2json(VarDeclaration vardecl, String tabs, boolean isFirst) {
        StringBuilder sb = new StringBuilder();
        for(VariableInit vi : vardecl.getVarInit()) {
            visited.add(vi);
            sb.append(tabs + (isFirst ? "" : ",") + "\"" + vi.getCanonicalName() + "\": {\n");
            sb.append(tabs + "  \"name\": \"" + vi.getVarName() + "\"\n");
            sb.append(tabs + "  ,\"type\": \"" + vi.getReturnType() + "\"\n");
            sb.append(fileLocation2json(vi, tabs + "  "));
            sb.append(tabs + "}\n");
        }
        return sb.toString();
    }

    private String variables2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"variables\": {\n");
        visited = new HashSet<BdsNode>();
        var isFirst = true;
        for(BdsNode bdsNode: nodes) {
            if( shouldVisit(bdsNode, VarDeclaration.class) ) {
                sb.append(variableDeclaration2json((VarDeclaration)bdsNode, "    ", isFirst));
                isFirst = false;
            }
        }
        sb.append("  },\n");
        return sb.toString();
    }

    private String variableReference2json(Reference ref, String tabs, boolean isFirst) {
        StringBuilder sb = new StringBuilder();
        visited.add(ref);
        if( ref.getFile() == null ) return "";
        sb.append(tabs + (isFirst ? "" : ",") + "{\n");
        var refType = ref.getClass().getCanonicalName();
        if( refType.startsWith(REFERENCE_NAME_STRING)) refType = refType.substring(REFERENCE_NAME_STRING.length());
        sb.append(tabs + "  \"reference\": \"" + refType + "\",\n");
        sb.append(tabs + "  \"type\": \"" + ref.getReturnType() + "\",\n");
        sb.append(tabs + "  \"refName\": \"" + ref.getVariableName() + "\",\n");
        sb.append(tabs + "  \"refCanonicalName\": \"" + ref.getVariableCanonicalName() + "\"\n");
        sb.append(fileLocation2json(ref, tabs + "  "));
        sb.append(tabs + "}\n");
        return sb.toString();
    }

    private String variableReferences2json(List<BdsNode> nodes) {
        var sb = new StringBuilder();
        sb.append("  \"references\": [\n");
        var isFirst = true;
        for(BdsNode bdsNode: nodes) {
            if( shouldVisit(bdsNode, Reference.class) ) {
                sb.append(variableReference2json((Reference)bdsNode, "    ", isFirst));
                isFirst = false;
            }
        }
        sb.append("  ],\n");
        return sb.toString();
    }
}
