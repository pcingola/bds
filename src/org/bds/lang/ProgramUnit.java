package org.bds.lang;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.bds.compile.BdsNodeWalker;
import org.bds.compile.CompilerMessages;
import org.bds.lang.statement.Module;
import org.bds.lang.statement.*;
import org.bds.lang.type.Type;
import org.bds.lang.type.Types;
import org.bds.run.BdsThread;
import org.bds.symbol.SymbolTable;
import org.bds.vm.OpCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A program unit incluses the program's code, libraries, etc.
 * I.e. All elelments that conform the final program
 *
 * @author pcingola
 */
public class ProgramUnit extends BlockWithFile {

    private static final long serialVersionUID = 3819936306695046515L;
    protected Module[] modules;
    protected BdsThread bdsThread;

    public ProgramUnit(BdsNode parent, ParseTree tree) {
        super(parent, null); // little hack begin: parse is done later
        modules = new Module[0];
        if (tree != null) setFile(discoverFileFromTree(tree));
        doParse(tree); // little hack end
    }

    private static File discoverFileFromTree(ParseTree tree) { // should probably go somewhere else?
        try {
            CharStream cs = ((ParserRuleContext) tree).getStart().getInputStream();
            String fileName = cs.getSourceName();
            if (fileName.equals("<unknown>")) return null;
            return new File(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new File("?");
        }
    }

    /**
     * Add a module
     */
    public void addModule(Module module) {
        modules = Arrays.copyOf(modules, modules.length + 1);
        modules[modules.length - 1] = module;
    }

    /**
     * Add local symbols to SymbolTable
     * The idea is that you should be able to refer to functions
     * and classes defined within the same scope, which may be defined
     * after the current statement, e.g.:
     * i := f(42)    // Function 'f' is not defined yet
     * int f(int x) { return 2*x }
     */
    public void addSymbols(SymbolTable symtab) {
        // Add all functions
        List<BdsNode> fdecls = BdsNodeWalker.findNodes(this, StatementFunctionDeclaration.class, false, true);
        for (BdsNode n : fdecls)
            symtab.addFunction((FunctionDeclaration) n);

        // Add classes
        List<BdsNode> cdecls = BdsNodeWalker.findNodes(this, ClassDeclaration.class, false, true);
        for (BdsNode n : cdecls) {
            ClassDeclaration cdecl = (ClassDeclaration) n;
            Types.add(cdecl.getType()); // This creates the type and adds it to Types
        }
    }

    /**
     * Return all functions whose name starts with 'test'
     */
    public List<FunctionDeclaration> findTestsFunctions() {
        List<FunctionDeclaration> testFuncs = new ArrayList<>();
        List<BdsNode> allFuncs = BdsNodeWalker.findNodes(this, StatementFunctionDeclaration.class, true, false);
        for (BdsNode func : allFuncs) {
            // Create scope symbol
            FunctionDeclaration fd = (FunctionDeclaration) func;

            String fname = fd.getFunctionName();
            if (fname.length() > 4 //
                    && fname.substring(0, 4).equalsIgnoreCase("test") // Starts with 'test'
                    && fd.getParameters().getVarDecl() != null //
                    && fd.getParameters().getVarDecl().length == 0 // There are no arguments to this function (e.g. 'test01()')
            ) testFuncs.add(fd);
        }

        return testFuncs;
    }

    @Override
    public BdsThread getBdsThread() {
        return bdsThread;
    }

    public void setBdsThread(BdsThread bdsThread) {
        this.bdsThread = bdsThread;
    }

    public Module[] getModules() {
        return modules;
    }

    public void setModules(Module[] modules) {
        this.modules = modules;
    }

    @Override
    protected void lineAndPos(Token token) {
        // Program Unit refers to the whole program, thus it does not have a "line number"
        // If we set this, coverage will be calculated incorrectly
    }

    @Override
    protected void parse(ParseTree tree) {
        super.parse(tree);
    }

    @Override
    public Type returnType(SymbolTable symtab, CompilerMessages compilerMessages) {
        return Types.INT;
    }

    @Override
    public String toAsm() {
        StringBuilder sb = new StringBuilder();

        // Modules first
        if (modules != null) {
            for (Module module : modules)
                sb.append(module.toAsm() + "\n");
        }

        // Main program
        sb.append(toAsmNode());
        sb.append("main:\n");

        if (isNeedsScope()) sb.append(OpCode.SCOPEPUSH + "\n");

        for (Statement s : statements)
            sb.append(s.toAsm());

        // Note: We don't pop the scope.
        //       We leave the last scope when because it is useful for
        //       checking variable values in test cases. Since the program
        //       finished, it makes no difference (we are cleaning up later).
        sb.append(OpCode.HALT + "\n");

        return sb.toString();
    }

}
