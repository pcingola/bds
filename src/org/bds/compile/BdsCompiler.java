package org.bds.compile;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;
import org.bds.Bds;
import org.bds.BdsLog;
import org.bds.Config;
import org.bds.antlr.BigDataScriptLexer;
import org.bds.antlr.BigDataScriptParser;
import org.bds.antlr.BigDataScriptParser.IncludeFileContext;
import org.bds.compile.CompilerMessage.MessageType;
import org.bds.lang.BdsNodeFactory;
import org.bds.lang.ProgramUnit;
import org.bds.lang.statement.Module;
import org.bds.lang.statement.StatementInclude;
import org.bds.symbol.GlobalSymbolTable;
import org.bds.util.Gpr;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.bds.libraries.Library.LIBRARIES;
import static org.bds.libraries.Library.LIBRARIES_PATH;

/**
 * BdsCompiler a Bds program.
 * <p>
 * Runs lexer & parser, create AST, perform type-checking and create BdsNode tree
 *
 * @author pcingola
 */
public class BdsCompiler implements BdsLog {

    boolean debug; // debug mode
    boolean verbose; // Verbose mode
    String programFileName; // Program file name
    ProgramUnit programUnit; // Program (parsed nodes)

    public BdsCompiler(String fileName) {
        programFileName = fileName;
        debug = Config.get().isDebug();
        verbose = Config.get().isVerbose();
    }

    /**
     * Convert a tree to string
     */
    public static String toStringTree(ParseTree tree) {
        StringBuilder sb = new StringBuilder();
        for (int childNum = 0; childNum < tree.getChildCount(); childNum++) {
            Tree child = tree.getChild(childNum);
            sb.append("\t\tChild " + childNum + ":\t" + child + "\tTree:'" + child.toStringTree() + "'\n");
        }
        return sb.toString();
    }

    /**
     * Add symbols: Classes, functions, methods, etc.
     * This step is necessary for forward resolution (i.e. when the
     * definition of the class is after the first time it's used)
     *
     * @return true on error, false on success
     */
    boolean addSymbols(ProgramUnit programUnit) {
        debug("Add symbols.");
        GlobalSymbolTable globalSymbolTable = GlobalSymbolTable.get();
        programUnit.addSymbols(globalSymbolTable);
        debug("Global SymbolTable after 'addSymbols':\n" + globalSymbolTable);
        return false;
    }

    /**
     * BdsCompiler program
     */
    public ProgramUnit compile() {
        CompilerMessages.reset(); // Compilation messages when creating tree

        // Compile main program
        programUnit = compileProgramUnit(programFileName);
        if (programUnit == null) return null;

        // Add  libraries to program unit
        List<Module> modules = compileLibraries(); // Compile standard libraries
        for (Module m : modules) programUnit.addModule(m);

        CompilerMessages.reset(); // Reset messages, now we only want look into type-checking messages

        // Add local symbols
        if (addSymbols(programUnit)) return null;

        // Type-checking
        if (typeChecking(programUnit)) return null;

        // Cleanup: Free some memory by resetting structure we won't use any more
        TypeCheckedNodes.get().reset();

        return programUnit;
    }

    /**
     * Compile all libraries (modules)
     */
    protected List<Module> compileLibraries() {
        List<Module> modules = new ArrayList<>();

        // All libraries
        for (String libraryName : LIBRARIES) {
            Module module = compileLibrary(LIBRARIES_PATH + "/" + libraryName);
            modules.add(module);
        }

        return modules;
    }

    /**
     * Compile a single library file, return a Module (BdsNode)
     */
    protected Module compileLibrary(String libraryPath) {
        debug("Compiling library: '" + libraryPath + "'");

        // Find library respect to Bds class location, and create an input stream
        InputStream inStream = Bds.class.getResourceAsStream(libraryPath);
        if (inStream == null) throw new RuntimeException("Cannot open library '" + libraryPath + "'");
        String input = Gpr.read(inStream);
        debug("Library '" + libraryPath + "', input:\n" + input);

        // Parse, create AST and convert to ProgramUnit (BdsNode)
        ParseTree tree = createAst(null, input, debug, new HashSet<>());
        var programUnit = createModel(tree);

        // Move statements from ProgramUnit to Module (we want to return a Module)
        Module module = new Module(null, null);
        module.setStatements(programUnit.getStatements());

        return module;
    }

    /**
     * BdsCompiler program into a ProgramUnit
     */
    protected ProgramUnit compileProgramUnit(String fileName) {
        debug("Loading file: '" + fileName + "'");

        // Convert to AST
        ParseTree tree = parseProgram(fileName);
        if (tree == null) return null;

        // Convert AST to BdsNodes, ProgramUnit is the root of the tree
        return createModel(tree);
    }

    /**
     * Create an AST from a program file
     *
     * @return A parsed tree
     */
    ParseTree createAst(String fileName) {
        File file = new File(fileName);
        return createAst(file, debug, new HashSet<>());
    }

    /**
     * Create an AST from a program (using ANTLR lexer & parser)
     * Returns null if error
     * Use 'alreadyIncluded' to keep track of from 'include' statements
     */
    ParseTree createAst(String fileName, CharStream input, boolean debug, Set<String> alreadyIncluded) {
        BigDataScriptLexer lexer;
        BigDataScriptParser parser;

        try {
            // Lexer: Create a lexer that feeds off of input CharStream
            lexer = new BigDataScriptLexer(input) {
                @Override
                public void recover(LexerNoViableAltException e) {
                    throw new RuntimeException(e); // Bail out
                }
            };

            // Parser
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new BigDataScriptParser(tokens);

            // Parser error handling
            parser.setErrorHandler(new CompileErrorStrategy()); // Bail out with pendingException if errors in parser
            parser.addErrorListener(new CompilerErrorListener()); // Catch some other error messages that 'CompileErrorStrategy' fails to catch

            // Begin parsing at main rule
            ParseTree tree = parserNode(parser);

            // Error loading file?
            if (tree == null) {
                System.err.println("Can't parse file '" + fileName + "'");
                return null;
            }

            // Show main nodes
            if (debug) debug("AST:" + toStringTree(tree));

            // Included files
            boolean resolveIncludePending = true;
            while (resolveIncludePending)
                resolveIncludePending = resolveIncludes(tree, debug, alreadyIncluded);

            return tree;
        } catch (Exception e) {
            String msg = e.getMessage();
            CompilerMessages.get().addError("Could not compile " + programFileName //
                    + (msg != null ? " :" + e.getMessage() : "") //
            );
            return null;
        }
    }

    /**
     * Create an AST from a program (using ANTLR lexer & parser)
     * Returns null if error
     * Use 'alreadyIncluded' to keep track of from 'include' statements
     */
    ParseTree createAst(File file, boolean debug, Set<String> alreadyIncluded) {
        alreadyIncluded.add(Gpr.getCanonicalFileName(file));
        String fileName = file.toString();
        String filePath = fileName;

        try {
            filePath = file.getCanonicalPath();

            // Input stream
            if (!Gpr.canRead(filePath)) {
                CompilerMessages.get().addError("Can't read file '" + filePath + "'");
                return null;
            }

            // Create a CharStream that reads from standard input
            CharStream input = CharStreams.fromFileName(fileName);
            return createAst(filePath, input, debug, alreadyIncluded);
        } catch (Exception e) {
            String msg = e.getMessage();
            CompilerMessages.get().addError("Could not compile " + filePath //
                    + (msg != null ? " :" + e.getMessage() : "") //
            );
            return null;
        }
    }

    /**
     * Create an AST from a program (using ANTLR lexer & parser)
     * Returns null if error
     * Use 'alreadyIncluded' to keep track of from 'include' statements
     */
    ParseTree createAst(String fileName, String inputStr, boolean debug, Set<String> alreadyIncluded) {
        try {
            // Create a CharStream that reads from standard input
            CharStream input = CharStreams.fromString(inputStr);
            return createAst(fileName, input, debug, alreadyIncluded);
        } catch (Exception e) {
            String msg = e.getMessage();
            CompilerMessages.get().addError("Could not compile string: '" + inputStr + "'" //
                    + (msg != null ? " :" + e.getMessage() : "") //
            );
            return null;
        }
    }

    /**
     * Convert to BdsNodes, create Program Unit
     */
    ProgramUnit createModel(ParseTree tree) {
        debug("Creating bds tree.");
        CompilerMessages.reset();
        ProgramUnit pu = (ProgramUnit) BdsNodeFactory.get().factory(null, tree); // Transform AST to BdsNode tree
        debug("AST:\n" + pu.toString());
        // Any error messages?
        if (!CompilerMessages.get().isEmpty()) System.err.println("Compiler messages:\n" + CompilerMessages.get());
        if (CompilerMessages.get().hasErrors()) return null;
        return pu;
    }

    public ProgramUnit getProgramUnit() {
        return programUnit;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Lex, parse and create Abstract syntax tree (AST)
     */
    ParseTree parseProgram(String fileName) {
        debug("Creating AST.");
        ParseTree tree;

        try {
            tree = createAst(fileName);
        } catch (Exception e) {
            System.err.println("Fatal error cannot continue - " + e.getMessage());
            return null;
        }

        // No tree produced? Fatal error
        if (tree == null) {
            if (CompilerMessages.get().isEmpty()) {
                CompilerMessages.get().addError("Fatal error: Could not compile");
            }
            return null;
        }

        // Any error? Do not continue
        if (!CompilerMessages.get().isEmpty()) return null;
        return tree;
    }

    protected ParseTree parserNode(BigDataScriptParser parser) {
        return parser.programUnit();
    }

    /**
     * Resolve include statements
     * Return 'true' of any new file has been included i.e. the AST ('tree') changed
     */
    boolean resolveIncludes(ParseTree tree, boolean debug, Set<String> alreadyIncluded) {
        boolean changed = false;
        if (tree instanceof IncludeFileContext) {
            // Parent file: The one that is including the other file
            File parentFile = new File(((IncludeFileContext) tree).getStart().getInputStream().getSourceName());

            // Included file name
            String includedFilename = StatementInclude.includeFileName(tree.getChild(1).getText());

            // Find file (look into all include paths)
            File includedFile = StatementInclude.includeFile(includedFilename, parentFile);
            if (includedFile == null) {
                CompilerMessages.get().add(tree, parentFile, "\n\tIncluded file not found: '" + includedFilename + "'\n\tSearch path: " + Config.get().getIncludePath(), MessageType.ERROR);
                return false;
            }

            // Already included? don't bother
            String canonicalFileName = Gpr.getCanonicalFileName(includedFile);
            if (alreadyIncluded.contains(canonicalFileName)) {
                debug("File already included: '" + includedFilename + "'\tCanonical path: '" + canonicalFileName + "'");
                return false;
            }

            // Can we read the include file?
            if (!includedFile.canRead()) {
                CompilerMessages.get().add(tree, parentFile, "\n\tCannot read included file: '" + includedFilename + "'", MessageType.ERROR);
                return false;
            }

            // Parse included file, i.e. create an AST of the included file
            ParseTree treeinc = createAst(includedFile, debug, alreadyIncluded);
            if (treeinc == null) {
                CompilerMessages.get().add(tree, parentFile, "\n\tFatal error including file '" + includedFilename + "'", MessageType.ERROR);
                return false;
            }

            // Add the nodes of the included file tree ('treeinc') into the original 'tree'
            IncludeFileContext includeFileContext = ((IncludeFileContext) tree);
            for (int i = 0; i < treeinc.getChildCount(); i++) {
                Tree child = treeinc.getChild(i);
                // Add only 'RuleContext', do not add TerminalNode (EOF)
                if (child instanceof RuleContext) {
                    includeFileContext.addChild((RuleContext) treeinc.getChild(i));
                }
            }
        } else {
            for (int i = 0; i < tree.getChildCount(); i++)
                changed |= resolveIncludes(tree.getChild(i), debug, alreadyIncluded);
        }

        return changed;
    }

    /**
     * Type checking
     */
    boolean typeChecking(ProgramUnit programUnit) {
        debug("Type checking.");
        GlobalSymbolTable globalSymbolTable = GlobalSymbolTable.get();
        programUnit.typeChecking(globalSymbolTable, CompilerMessages.get());

        // Any error messages?
        if (!CompilerMessages.get().isEmpty()) System.err.println("Compiler messages:\n" + CompilerMessages.get());
        return CompilerMessages.get().hasErrors();
    }
}
