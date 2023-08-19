package org.bds.run;

import org.bds.*;
import org.bds.compile.BdsCompiler;
import org.bds.compile.BdsNodeWalker;
import org.bds.compile.CompilerMessage;
import org.bds.compile.CompilerMessages;
import org.bds.data.Data;
import org.bds.data.FtpConnectionFactory;
import org.bds.executioner.*;
import org.bds.executioner.Executioners.ExecutionerType;
import org.bds.lang.BdsNode;
import org.bds.lang.BdsNodeFactory;
import org.bds.lang.ProgramUnit;
import org.bds.lang.nativeFunctions.NativeLibraryFunctions;
import org.bds.lang.nativeMethods.string.NativeLibraryString;
import org.bds.lang.statement.FunctionDeclaration;
import org.bds.lang.statement.Module;
import org.bds.lang.statement.Statement;
import org.bds.lang.type.Types;
import org.bds.languageServer.LanguageServerBds;
import org.bds.languageServer.LspServices;
import org.bds.osCmd.CmdAws;
import org.bds.scope.GlobalScope;
import org.bds.scope.Scope;
import org.bds.symbol.GlobalSymbolTable;
import org.bds.task.TaskDependecies;
import org.bds.util.Timer;
import org.bds.vm.BdsVm;
import org.bds.vm.BdsVmAsm;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

/**
 * Run a bds program
 *
 * @author pcingola
 */
public class BdsRun implements BdsLog {

    BdsAction bdsAction;
    BdsThread bdsThread;
    String chekcpointRestoreFile; // Restore file
    Config config;
    boolean coverage; // Run coverage tests
    String coverageFile; // Save coverage statistics to file
    double coverageMin; // Minimum coverage required to pass a coverage test
    Coverage coverageCounter; // Keep track of coverage between test runs
    boolean debug; // debug mode
    int exitValue;
    boolean log; // Log everything (keep STDOUT, SDTERR and ExitCode files)
    List<String> programArgs; // Command line arguments for bds program
    String programFileName; // Program file name
    ProgramUnit programUnit; // Program (parsed nodes)
    boolean stackCheck; // Check stack size when thread finishes runnig (should be zero)
    boolean verbose; // Verbose mode
    BdsVm vm;

    public BdsRun() {
        bdsAction = BdsAction.RUN;
        programArgs = new ArrayList<>();
    }

    /**
     * Reset all singleton objects
     */
    public static void reset() {
        BdsLogger.debug("Full reset");
        Config.reset();
        Executioners.reset();
        BdsThreads.reset();
        Types.reset();
        BdsNodeFactory.reset();
        GlobalSymbolTable.reset();
        GlobalScope.reset();
        TaskDependecies.reset();
        FtpConnectionFactory.kill();
    }

    public void addArg(String arg) {
        programArgs.add(arg);
    }

    /**
     * Print assembly code to STDOUT
     */
    int assembly() {
        // Compile, abort on errors
        if (!compileBds()) return 1;
        try {
            System.out.println(programUnit.toAsm());
        } catch (Throwable t) {
            if (verbose) t.printStackTrace();
            return 1;
        }
        return 0;
    }

    /**
     * Check 'pidRegex'
     */
    public void checkPidRegex() {
        // PID regex matcher
        String pidPatternStr = config.getPidRegex();

        if (pidPatternStr.isEmpty()) {
            System.err.println("Cannot find 'pidRegex' entry in config file.");
            System.exit(1);
        }

        ExecutionerFileSystem executioner = (ExecutionerFileSystem) Executioners.getInstance().get(ExecutionerType.CLUSTER);

        // Show pattern
        System.out.println("Matching pidRegex '" + pidPatternStr + "'");

        // Read STDIN and check pattern
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = in.readLine()) != null) {
                String pid = executioner.parsePidLine(line);
                System.out.println("Input line:\t'" + line + "'\tMatched: '" + pid + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        executioner.kill(); // Kill executioner
    }

    /**
     * Compile program: bds -> ATS -> BdsNodes -> VM ASM -> VM OpCodes
     *
     * @returns: -1 on compile errors; 0 if run OK, 1 if run with errors
     */
    public CompileCode compile() {
        // Compile bds to BdsNodes tree
        if (!compileBds()) return CompileCode.ERROR;

        // Parse command line args & show automatic help
        // Note: Command line arguments set variables by changing VarInit
        //       nodes, that's why we do command line parsing before ASM
        //       compilation.
        if (parseCmdLineArgs()) return CompileCode.OK_HELP;

        // Compile to VM assembly
        vm = compileAsm(programUnit);
        return vm != null ? CompileCode.OK : CompileCode.ERROR;
    }

    /**
     * Compile: BdsNodes -> VM ASM -> VM OpCodes
     *
     * @return A BdsVm with all compiled assembly code
     */
    BdsVm compileAsm(ProgramUnit programUnit) {
        try {
            // Compile assembly
            BdsVmAsm vmasm = new BdsVmAsm(programUnit);
            vmasm.setDebug(debug);
            vmasm.setVerbose(verbose);
            vmasm.setCoverage(coverage);
            vmasm.setCode(programUnit.toAsm());

            // Compile assembly
            return vmasm.compile();
        } catch (Throwable t) {
            if (config.isVerbose()) t.printStackTrace();
            else Timer.showStdErr(t.getMessage());
            return null;
        }
    }

    /**
     * Compile program to BdsNode tree: bds -> BdsNodes
     *
     * @return True if compiled OK
     */
    boolean compileBds() {
        debug("Parsing");
        BdsCompiler compiler = new BdsCompiler(programFileName);
        programUnit = compiler.compile();

        // Show errors and warnings, if any
        if ((programUnit == null) && !CompilerMessages.get().isEmpty()) {
            System.err.println("Compiler messages:\n" + CompilerMessages.get());
        }

        return programUnit != null;
    }

    public BdsAction getBdsAction() {
        return bdsAction;
    }

    public void setBdsAction(BdsAction bdsAction) {
        this.bdsAction = bdsAction;
    }

    public BdsThread getBdsThread() {
        return bdsThread;
    }

    public Coverage getCoverageCounter() {
        return coverageCounter;
    }

    public double getCoverageMin() {
        return coverageMin;
    }

    public void setCoverageMin(double coverageMin) {
        this.coverageMin = coverageMin;
    }

    public List<String> getProgramArgs() {
        return programArgs;
    }

    public void setProgramArgs(ArrayList<String> programArgs) {
        this.programArgs = programArgs;
    }

    public ProgramUnit getProgramUnit() {
        return programUnit;
    }

    public void setProgramUnit(ProgramUnit programUnit) {
        this.programUnit = programUnit;
    }

    public Scope getScope() {
        return vm.getScope();
    }

    public BdsVm getVm() {
        return vm;
    }

    /**
     * Show information from a checkpoint file
     */
    int infoCheckpoint() {
        // Load checkpoint file
        BdsThread bdsThreadRoot = loadCheckpoint();

        for (BdsThread bdsThread : bdsThreadRoot.getBdsThreadsAll())
            bdsThread.print();

        return 0;
    }

    /**
     * Initialize before running or type-checking
     */
    void initialize() {
        Types.reset();

        // Reset node factory
        BdsNodeFactory.reset();

        // Startup message
        if (log || debug) Timer.showStdErr(Bds.VERSION);

        // Global scope
        GlobalSymbolTable.reset();
        GlobalScope.reset();
        GlobalScope.get().initilaize(config);

        // Initialize native classes
        initilaizeNativeClasses();

        // Libraries
        initilaizeNativeLibraries();
    }

    /**
     * Initialize coverage counter (for test cases)
     */
    void initializeCoverageCounter() {
        if (!coverage) return;

        // Should we load from a coverage file?
        if (coverageFile != null) {
            // Load form coverage file, if it exists
            var coveragef = new File(coverageFile);
            if (coveragef.exists()) {
                // If the file exists, try loading it
                log("Loading coverage from '" + coverageFile + "'");
                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(coverageFile));
                    coverageCounter = (Coverage) in.readObject();
                    in.close();
                    coverageCounter.resetNodes(); // Reset bdsNodes links from stats object after loading, these change every time we run the tests
                } catch (IOException e) {
                    throw new RuntimeException("Could not read coverage file '" + coverageFile + "'. Corrupted file? Try deleting it", e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Could not parse coverage file file '" + coverageFile + "'. Corrupted file? Try deleting it. ", e);
                }
            } else {
                log("Coverage file '" + coverageFile + "' not found");
            }
        }

        // If we did not load from file, we create a new one here
        if (coverageCounter == null) {
            log("Creating new coverage");
            coverageCounter = new Coverage();
        }
    }

    /**
     * Initialize all base classes provided by 'bds'
     */
    void initilaizeNativeClasses() {
        debug("Initialize standard classes.");
    }

    /**
     * Initialize standard libraries
     */
    void initilaizeNativeLibraries() {
        debug("Initialize standard libraries.");

        // Native functions
        NativeLibraryFunctions nativeLibraryFunctions = new NativeLibraryFunctions();
        debug("Native library 'functions', functions: " + nativeLibraryFunctions.size());

        // Native library: String
        NativeLibraryString nativeLibraryString = new NativeLibraryString();
        debug("Native library 'string', methods: " + nativeLibraryString.size());
    }

    public boolean isCoverage() {
        return coverage;
    }

    public void setCoverage(boolean coverage) {
        this.coverage = coverage;
    }

    public void setCoverageFile(String coverageFile) {
        this.coverageFile = coverageFile;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    @Override
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Restore from checkpoint and run
     */
    BdsThread loadCheckpoint() {
        // Load checkpoint file
        log("Loading checkpoint: " + chekcpointRestoreFile);
        BdsThread bdsThreadRoot;
        try {
            // If the checkpoint is remote, download it
            Data d = Data.factory(chekcpointRestoreFile);
            String localFile = d.isRemote() ? d.getLocalPath() : d.getAbsolutePath();
            if (d.isRemote()) d.download();
            // Load data from local file
            ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(localFile)));
            bdsThreadRoot = (BdsThread) in.readObject();
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Error while reading checkpoint file '" + chekcpointRestoreFile + "'", e);
        }

        // Set main thread's programUnit running scope (mostly for debugging and test cases)
        // ProgramUnit's scope it the one before 'global'
        // BdsThread mainThread = bdsThreads.get(0);
        programUnit = bdsThreadRoot.getProgramUnit();

        // Add all nodes
        for (BdsNode n : BdsNodeWalker.findNodes(programUnit, null, true, true)) {
            BdsNodeFactory.get().addNode(n);
        }

        return bdsThreadRoot;
    }

    /**
     * Parse command line arguments
     *
     * @return true if automatic help is shown and program should finish
     */
    boolean parseCmdLineArgs() {
        debug("Initializing");
        BdsParseArgs bdsParseArgs = new BdsParseArgs(programUnit, programArgs);
        bdsParseArgs.setDebug(debug);
        bdsParseArgs.parse();

        // Show script's automatic help message
        if (bdsParseArgs.isShowHelp()) {
            debug("Showing automaic 'help'");
            HelpCreator hc = new HelpCreator(programUnit);
            System.out.println(hc);
            return true;
        }

        return false;
    }

    /**
     * Run program
     */
    public int run() {
        // Initialize
        initialize();
        Executioners executioners = Executioners.getInstance(config);
        TaskDependecies.reset();

        // Run action
        switch (bdsAction) {
            case ASSEMBLY:
                exitValue = assembly();
                break;

            case CHECK_PID_REGEX:
                checkPidRegex();
                exitValue = 0;
                break;

            case COMPILE:
                exitValue = compileBds() ? 0 : 1;
                break;

            case INFO_CHECKPOINT:
                exitValue = infoCheckpoint();
                break;

            case RUN:
                exitValue = runCompile(); // Compile + Run
                break;

            case RUN_CHECKPOINT:
                exitValue = runCheckpoint();
                break;

            case RUN_TASK_IMPROPER:
                exitValue = runTaskImproper();
                break;

            case RUN_TEST:
                exitValue = runTests();
                break;

            case RUN_LANGUAGE_SERVER:
                runLanguageServer();
                break;

            case ZZZ:
                exitValue = zzz();
                break;

            default:
                throw new RuntimeException("Unimplemented action '" + bdsAction + "'");
        }

        debug("Finished. Exit code: " + exitValue);

        // Kill all executioners
        executioners.kill();

        // Kill other timer tasks
        FtpConnectionFactory.kill();

        // FIXME: Dead code? Remove
        // config.kill(); // Kill 'tail' and 'monitor' threads

        return exitValue;
    }

    /**
     * Create a BdsThread and run it
     */
    int runBdsThread() {
        // Create & run thread
        BdsThread bdsThread = new BdsThread(programUnit, config, vm);
        debug("Process ID '" + bdsThread.getBdsThreadId() + "': Running");

        // Run and get exit code
        int exitCode = runThread(bdsThread);

        // Check stack
        if (stackCheck) bdsThread.sanityCheckStack();

        return exitCode;
    }

    /**
     * Restore from checkpoint and run
     */
    int runCheckpoint() {
        // Load checkpoint file
        bdsThread = loadCheckpoint();
        vm = bdsThread.getVm();
        vm.setRecoveredCheckpoint(true);

        // Set main thread's programUnit running scope (mostly for debugging and test cases)
        // ProgramUnit's scope it the one before 'global'
        programUnit = bdsThread.getProgramUnit();

        // Set state and recover tasks
        List<BdsThread> bdsThreads = bdsThread.getBdsThreadsAll();
        bdsThreads.add(bdsThread);
        for (BdsThread bdsThread : bdsThreads) {
            // Re-execute or add tasks (if thread is not finished)
            if (!bdsThread.getRunState().isFinished()) {
                bdsThread.unserializedTasksRestore();
            }
        }

        // All set, run main thread
        debug("Running from checkpoint");
        return runThread(bdsThread);
    }

    /**
     * BdsCompiler and run
     */
    int runCompile() {
        // Compile, abort on errors
        debug("Compiling");
        CompileCode ccode = compile();
        switch (ccode) {
            case OK:
                break;

            case OK_HELP:
                return 0;

            case ERROR:
                return 1;

            default:
                throw new RuntimeException("Unknown compile result code: '" + ccode + "'");
        }

        // Run thread
        return runBdsThread();
    }

    void runLanguageServer() {
        // Use STDIN/STDOUT, we may choose another transport layer in the future
        InputStream in = System.in;
        OutputStream out = System.out;
        try {
            LanguageServerBds server = new LanguageServerBds();
            Launcher<LanguageClient> launcher = Launcher.createLauncher(server, LanguageClient.class, in, out);
            LanguageClient client = launcher.getRemoteProxy();
            server.connect(client);
            Future<?> startListening = launcher.startListening();
            startListening.get();
        } catch (InterruptedException ie) {
            throw new RuntimeException("Language Server interrupted.", ie);
        } catch (ExecutionException ee) {
            throw new RuntimeException("Language Server execution exception.", ee);
        }
    }

    /**
     * Restore from checkpoint and run
     */
    int runTaskImproper() {
        // Load checkpoint file
        bdsThread = loadCheckpoint();
        vm = bdsThread.getVm();
        vm.setRecoveredCheckpoint(true);

        // Set main thread's programUnit running scope (mostly for debugging and test cases)
        // ProgramUnit's scope it the one before 'global'
        programUnit = bdsThread.getProgramUnit();

        // All set, run main thread
        debug("Running task improper");
        return runThread(bdsThread);
    }

    /**
     * Compile and run tests, calculate coverage
     */
    int runTests() {
        // Compile, abort on errors
        CompileCode ccode = compile();
        switch (ccode) {
            case OK:
                break;

            case OK_HELP:
                return 0;

            case ERROR:
                return 1;

            default:
                throw new RuntimeException("Unknown compile result code: '" + ccode + "'");
        }

        // Run tests
        debug("Running tests");
        initializeCoverageCounter(); // Initialize coverage statistics, or load them from file

        int retCode = runTestsFunctions(); // Run each test function

        // Show coverage statistics and save them
        if (coverage) {
            // Show stats
            System.out.println(coverageCounter);
            if (debug) debug("Detailed coverage counts:\n" + coverageCounter.toStringCounts());

            // Set exit code if the min coverage is not met
            if (coverageMin > 0.0 && coverageCounter.coverageRatio() < coverageMin) retCode = 1;

            // Save coverage stats to file
            saveCoverageStatistics();
        }

        return retCode;
    }

    /**
     * Run a single test function, return exit code
     */
    int runTestFunction(FunctionDeclaration testFunc) {
        // Add all 'declaration' statements
        BdsNodeWalker bwalker = new BdsNodeWalker(programUnit);
        List<Statement> statements = bwalker.findDeclarations();

        // Note: We execute the function's body (not the function declaration) as if it was the body of the program
        statements.add(testFunc.getStatement());

        // Create a program unit having all variable/function/class declarations and the test function's statements
        ProgramUnit puTest = new ProgramUnit(programUnit, null);
        puTest.setFile(programUnit.getFile());
        puTest.setModules(programUnit.getModules());
        puTest.setStatements(statements.toArray(new Statement[0]));

        // Compile and create vm
        BdsVm vmtest = compileAsm(puTest);
        vmtest.setDebug(debug);

        BdsThread bdsThreadTest = new BdsThread(puTest, config, vmtest);

        // Run thread and check exit code
        int exitValTest = runThread(bdsThreadTest);

        // Show coverage results
        if (coverage) {
            coverageCounter.add(vmtest); // Add all statistics from vm execution
            coverageCounter.markNativeCode(vmtest, puTest.getModules()); // Then set 'Modules' nodes as 'native code' so they don't count in coverage calculations
            coverageCounter.markTestCode(vmtest, testFunc); // Mark all nodes that are in the test*() function. We don't want to count test code in the coverage statistics
        }

        return exitValTest;
    }

    /**
     * For each "test*()" function in ProgramUnit, create a thread that executes the function's body
     */
    int runTestsFunctions() {
        List<FunctionDeclaration> testFuncs = programUnit.findTestsFunctions();

        // Run each test function
        int exitCode = 0;
        int testOk = 0, testError = 0;
        for (FunctionDeclaration testFunc : testFuncs) {
            // Run each function
            debug("Running test function: '" + testFunc.getFunctionName() + "', file '" + testFunc.getFileName() + "', line " + testFunc.getLineNum());
            int exitValTest = runTestFunction(testFunc);

            // Show test result
            if (exitValTest == 0) {
                Timer.show("Test '" + testFunc.getFunctionName() + "': OK");
                testOk++;
            } else {
                Timer.show("Test '" + testFunc.getFunctionName() + "': FAIL");
                exitCode = 1;
                testError++;
            }
        }

        // Show results
        Timer.show("Totals"//
                + "\n                  OK    : " + testOk //
                + "\n                  ERROR : " + testError //
        );

        return exitCode;
    }

    /**
     * Run a thread
     */
    int runThread(BdsThread bdsThread) {
        this.bdsThread = bdsThread;
        if (bdsThread.getRunState().isFinished()) return 0;

        bdsThread.start();

        try {
            bdsThread.join();
        } catch (InterruptedException e) {
            // Nothing to do?
            // May be checkpoint?
            return 1;
        }

        // Check stack
        if (stackCheck) bdsThread.sanityCheckStack();

        // OK, we are done
        return bdsThread.getExitValue();
    }

    /**
     * Save coverage statistics to file
     */
    void saveCoverageStatistics() {
        if (coverageFile == null || coverageFile.isBlank()) return;

        try {
            log("Saving coverage to file '" + coverageFile + "'");
            coverageCounter.resetNodes(); // Reset bdsNode links before saving, since these will change each time we run tests
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(coverageFile));
            out.writeObject(coverageCounter);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not save coverage to file '" + coverageFile + "'", e);
        }
    }


    public void setChekcpointRestoreFile(String chekcpointRestoreFile) {
        this.chekcpointRestoreFile = chekcpointRestoreFile;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setProgramFileName(String programFileName) {
        this.programFileName = programFileName;
    }

    public void setStackCheck(boolean stackCheck) {
        this.stackCheck = stackCheck;
    }

    /**
     * Run some experimental code
     * This is only used for developments (undocumented)
     */
    private int zzz() {
        System.out.println("ZZZ!");
        debug("LSP service");
        BdsCompiler compiler = new BdsCompiler(programFileName);
        programUnit = compiler.compile();

        LspServices lspServices = new LspServices(programUnit, CompilerMessages.get());
        System.out.println(lspServices.getCompilerMessagesAsJsonString());
        
        // System.out.println("{");

        // // Show errors and warnings
        // System.out.println("  \"ComplieMessages\": [");
        // for(CompilerMessage cm: CompilerMessages.get()) {
        //     System.out.println("    {");
        //     System.out.println("      \"file\": \"" + cm.getFileName() + "\", ");
        //     System.out.println("      \"lineNumber\": " + cm.getLineNum() + ", ");
        //     System.out.println("      \"charPosInLine\": " + cm.getCharPosInLine() + ", ");
        //     System.out.println("      \"type\": \"" + cm.getType() + "\", ");
        //     System.out.println("      \"message\": \"" + cm.getMessage() + "\", ");
        //     System.out.println("    },");
        // }
        // System.out.println("  ],");

        // // Show symbols
        // System.out.println("  \"Symbols\": [");
        // for(CompilerMessage cm: CompilerMessages.get()) {
        //     System.out.println("    {");
        //     System.out.println("      \"file\": \"" + cm.getFileName() + "\", ");
        //     System.out.println("      \"lineNumber\": " + cm.getLineNum() + ", ");
        //     System.out.println("      \"charPosInLine\": " + cm.getCharPosInLine() + ", ");
        //     System.out.println("      \"type\": \"" + cm.getType() + "\", ");
        //     System.out.println("      \"message\": \"" + cm.getMessage() + "\", ");
        //     System.out.println("    },");
        // }
        // System.out.println("  ],");

        // System.out.println("}");
        return programUnit != null ? 0 : 1;
    }

    public enum BdsAction {
        ASSEMBLY // Only create assembly code and show it to STDOUT
        , CHECK_PID_REGEX // Check that PID regex works
        , COMPILE // Compile only. This is used to check if a program compiles (it does not run the program)
        , INFO_CHECKPOINT // Show information in a checkpoint file
        , RUN // Run a program
        , RUN_CHECKPOINT // Run from a checkpoint
        , RUN_TASK_IMPROPER // Run an improper task from a checkpoint
        , RUN_TEST // Run test cases in bds (i.e. compile and run all functions named `test*()`
        , RUN_LANGUAGE_SERVER // Run language server (LSP)
        , ZZZ // Run the 'zzz()' method. This is only used for developing experimental code (undocumented option
    }

    public enum CompileCode {
        OK, // The code compiled OK
        OK_HELP, // The code compiled OK, '-h' option used so help was shown
        ERROR // There were compilation errors
    }
}
