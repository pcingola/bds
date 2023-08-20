package org.bds.run;

import org.bds.BdsLog;
import org.bds.Config;
import org.bds.compile.BdsNodeWalker;
import org.bds.data.Data;
import org.bds.executioner.Executioner;
import org.bds.executioner.Executioners;
import org.bds.lang.BdsNode;
import org.bds.lang.BdsNodeFactory;
import org.bds.lang.ProgramUnit;
import org.bds.lang.statement.BlockWithFile;
import org.bds.lang.statement.Statement;
import org.bds.lang.statement.StatementInclude;
import org.bds.lang.type.TypeList;
import org.bds.lang.type.Types;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueList;
import org.bds.lang.value.ValueString;
import org.bds.osCmd.Exec;
import org.bds.report.Report;
import org.bds.scope.Scope;
import org.bds.task.Task;
import org.bds.task.TaskDependecies;
import org.bds.task.TaskVmOpcode;
import org.bds.util.Gpr;
import org.bds.util.Timer;
import org.bds.vm.BdsVm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * A threads used in a bigDataScript program
 * <p>
 * It has all information to run a program (scope, pc, run state, etc)
 *
 * @author pcingola
 */
public class BdsThread extends Thread implements Serializable, BdsLog {

    // Exit codes (see bds.go)
    public static final int EXITCODE_OK = 0;
    public static final int EXITCODE_ERROR = 1;
    public static final int EXITCODE_TIMEOUT = 2;
    public static final int EXITCODE_KILLED = 3;
    public static final int EXITCODE_FATAL_ERROR = 10;
    public static final int EXITCODE_ASSERTION_FAILED = 5;
    public static final int FROZEN_SLEEP_TIME = 25; // Sleep time when frozen (milliseconds)
    public static final int MAX_TASK_FAILED_NAMES = 10; // Maximum number of failed tasks to show in summary
    private static final long serialVersionUID = 1206304272840188781L;
    private static int bdsThreadNumber = 1;
    private static int currentId = 1;

    Map<String, BdsThread> bdsChildThreadsById; // Child threads
    String bdsThreadId; // BdsThread ID
    int bdsThreadNum; // Thread number
    Config config; // Configuration
    String currentDir; // Program's 'current directory'
    String errorMessage; // Error message
    int exitValue; // Exit value
    boolean freeze; // Freeze execution in next execution step
    BdsThread parent; // Parent thread
    Random random; // Random number generator
    List<Data> removeOnExit; // Files to be removed on exit
    String reportFile; // Latest report file
    RunState runState; // Latest RunState
    Statement statement; // Main statement executed by this thread
    TaskDependecies taskDependecies;
    Timer timer; // Program timer
    BdsVm vm; // Virtual machine

    private BdsThread(BdsThread parent, Statement statement, Config config, BdsVm vm) {
        super();
        this.parent = parent;
        bdsThreadNum = bdsThreadId();
        setRunState(RunState.OK);
        this.config = config;
        random = new Random();
        removeOnExit = new LinkedList<>();
        taskDependecies = new TaskDependecies();
        bdsChildThreadsById = new HashMap<>();
        currentDir = System.getProperty(Exec.USER_DIR); // By default use Java program's current dir

        if (statement != null) setStatement(statement);

        taskDependecies.setVerbose(isVerbose());
        taskDependecies.setDebug(isDebug());

        if (vm != null) {
            this.vm = vm;
            vm.setBdsThread(this);
        }

        if (parent != null) parent.add(this);
    }

    public BdsThread(Statement statement, Config config, BdsVm vm) {
        this(null, statement, config, vm);
    }

    /**
     * Get an ID for a node
     */
    protected synchronized static int bdsThreadId() {
        return bdsThreadNumber++;
    }

    public static synchronized int nextId() {
        return currentId++;
    }

    /**
     * Add a child task
     */
    public synchronized void add(BdsThread bdsThread) {
        bdsChildThreadsById.put(bdsThread.getBdsThreadId(), bdsThread);
    }

    /**
     * Add a task
     */
    public synchronized void add(Task task) {
        taskDependecies.add(task);
    }

    /**
     * Has this or any child thread created tasks?
     */
    public boolean anyTask() {
        if (!getTasks().isEmpty()) return true;

        for (BdsThread bdsThread : bdsChildThreadsById.values())
            if (bdsThread.anyTask()) return true;

        return false;
    }

    /**
     * Assertion failed (in bds test case)
     */
    public void assertionFailed(BdsNode bdsnode, String message) {
        runState = RunState.FATAL_ERROR;

        // Try to get file name and line number
        String filePos = getFileLinePos(bdsnode);

        // Native functions don't have `filelinePos`, and 'assert' is a native function.
        if (filePos.isEmpty() && vm != null) {
            // Let's see if the bdsnode from the vm has 'fileLinePos' info
            bdsnode = vm.getBdsNode();
            if (bdsnode != null) filePos = getFileLinePos(bdsnode);
        }

        // Show error message
        System.err.println("Assertion failed" //
                + (filePos.isEmpty() ? "" : ". ") + filePos //
                + ": " //
                + (message != null ? message : "") //
        );

        // Set exit value and stop the vm
        setExitValue(EXITCODE_ASSERTION_FAILED);
        setRunState(RunState.FATAL_ERROR);
    }

    /**
     * Create a checkpoint file
     */
    public String checkpoint(BdsNode node) {
        // Skip checkpoint file?
        if (Config.get().isNoCheckpoint()) return "";

        // Create checkpoint
        String programFile = statement.getFileNameCanonical();
        String nodeFile = node.getFileNameCanonical();
        String checkpointFileName = Gpr.baseName(programFile);
        if (!programFile.equals(nodeFile)) checkpointFileName += "." + Gpr.baseName(node.getFileName(), ".bds");
        if (node.getLineNum() > 0) checkpointFileName += ".line_" + node.getLineNum();
        checkpointFileName += ".chp";

        return checkpoint(checkpointFileName);
    }

    /**
     * Create a checkpoint
     */
    public String checkpoint(String checkpointFileName) {
        // Default file name
        if (checkpointFileName == null) {
            checkpointFileName = statement.getFileNameCanonical() + ".chp";
        }

        // Get absolute path
        Data d = Data.factory(checkpointFileName);
        String filePath = d.isRemote() ? d.toString() : d.getAbsolutePath();

        // Save
        if (isVerbose()) System.err.println("Creating checkpoint file: '" + checkpointFileName + "'");

        try {
            // Freeze all threads (cannot serialize while running and changing state)
            Freeze.freeze();

            // Serialize root BdsThred to file
            String localPath = d.isRemote() ? d.getLocalPath() : checkpointFileName;

            // Make directory
            File parent = (new File(localPath)).getParentFile();
            if (parent != null) parent.mkdirs();

            // Save file
            ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(localPath)));
            out.writeObject(getRoot());
            out.close();

            // Upload remote file
            if (d.isRemote()) d.upload();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while serializing to file '" + checkpointFileName + "'", e);
        } finally {
            // Un-freeze all threads
            Freeze.unfreeze();
        }

        return filePath;
    }

    /**
     * Create checkpoint file. This method is called from VM opcode
     *
     * @return Checkpoint file name
     */
    public String checkpoint(String checkpointFileName, BdsNode node) {
        // Default file name
        if (checkpointFileName.isEmpty()) {
            checkpointFileName = generateId(node, "checkpoint", null, false, true) + ".chp";
        }
        checkpointFileName = checkpoint(checkpointFileName);
        return checkpointFileName;
    }

    /**
     * Create checkpoint file only fir the current VM.
     * In this checkpoint, nothing other than the current VM is serialized: no other
     * parallel running VMs, tasks, 'rmOnExit', etc.
     *
     * @return Checkpoint's file name
     */
    public synchronized String checkpointVm(String checkpointFileName, BdsNode node) {
        // Default file name
        if (checkpointFileName.isEmpty()) {
            checkpointFileName = generateId(node, "checkpoint", null, false, true) + ".chp";
        }

        // Store states
        List<Data> removeOnExitOri = removeOnExit;
        BdsThread parentOri = parent;
        Map<String, BdsThread> bdsChildThreadsByIdOri = bdsChildThreadsById;
        TaskDependecies taskDependeciesOri = taskDependecies;

        // Detach everything
        removeOnExit = new LinkedList<>();
        parent = null;
        taskDependecies = new TaskDependecies();
        bdsChildThreadsById = new HashMap<>();

        // Create checkpoint
        checkpointFileName = checkpoint(checkpointFileName);

        // Restore original state
        removeOnExit = removeOnExitOri;
        parent = parentOri;
        taskDependecies = taskDependeciesOri;
        bdsChildThreadsById = bdsChildThreadsByIdOri;

        // Return absolute path to checkpoint file
        File chpFile = new File(checkpointFileName);
        return chpFile.getAbsolutePath();
    }

    void cleanupBeforeReport() {
        // We are completely done
        setRunState(RunState.FINISHED);

        // Finish up
        removeStaleData();
        timer.end();
    }

    void clearupAfterReport() {
        if (!isRoot()) parent.remove(this); // Remove this bdsThread from parent's threads

        // OK, we are done
        if (isDebug()) {
            // Root thread? Report all tasks
            TaskDependecies td = isRoot() ? TaskDependecies.get() : taskDependecies;

            debug((isRoot() ? "Program" : "Parallel") + " " //
                    + "'" + getBdsThreadId() + "'" //
                    + " finished" //
                    + (isDebug() ? ", run state: '" + getRunState() + "'" : "") //
                    + ", exit value: " + getExitValue() //
                    + ", tasks executed: " + td.getTasks().size() //
                    + ", tasks failed: " + td.countTaskFailed() //
                    + ", tasks failed names: " + td.taskFailedNames(MAX_TASK_FAILED_NAMES, " , ") //
                    + "." //
            );
        }

        // Remove thread from "running threads"
        BdsThreads.getInstance().remove();
    }

    void createBdsThreadId() {
        if (bdsThreadId != null) return; // Nothing to do

        // Create ID
        String name = Gpr.baseName(statement.getFileName());
        if (isRoot())
            bdsThreadId = String.format("%s.%2$tY%2$tm%2$td_%2$tH%2$tM%2$tS_%2$tL", name, Calendar.getInstance());
        else bdsThreadId = parent.bdsThreadId + "/parallel_" + getId();
    }

    /**
     * Create a dir for all log files
     */
    public void createLogDir() {
        String dirname = getLogBaseName();
        File logdir = new File(dirname);
        if (!logdir.exists()) logdir.mkdirs();

        // No logging? Delete on exit
        if ((config != null) && !config.isLog()) logdir.deleteOnExit();
    }

    /**
     * Create a new (and canonical) file relative to 'currentDir'
     */
    public Data data(String fileName) {
        return Data.factory(fileName, this);
    }

    /**
     * There is an implicit 'wait' statment at the end of the program
     */
    boolean waitImplicit() {
        // We are done running
        debug("BdsThread finished: " + getBdsThreadId());
        if (getRunState().isFatalError()) {
            // Error condition
            debug((isRoot() ? "Program" : "Parallel") + " '" + getBdsThreadId() + "' fatal error");
            return false;
        } else {
            // OK, we finished running
            debug((isRoot() ? "Program" : "Parallel") + " '" + getBdsThreadId() + "' execution finished");

            // Implicit 'wait' statement at the end of the program (only if the program finished 'naturally')
            debug((isRoot() ? "Program" : "Parallel") + " '" + getBdsThreadId() + "' waitAll: Waiting for all threads and tasks to finish");

            var ok = waitAll();
            if (!ok) errorMessage = "Error waiting pending tasks";

            debug((isRoot() ? "Program" : "Parallel") + " '" + getBdsThreadId() + "' waitAll: All threads and tasks finished");
            return ok;
        }
    }

    /**
     * Calculate exitCode after bdsThred runs
     *
     * @param allTaskFinishedOk: Have all tasks finished OK?
     */
    void exitCode(boolean allTaskFinishedOk) {
        // Exit code already set? Nothing to do
        if (vm.getExitCode() != null) {
            setExitValue(vm.getExitCode()); // Make sure local exitValue is populated with the value from VM
            return;
        }

        // Set exitCode based on whether tasks fninished OK
        if (!allTaskFinishedOk) {
            // Errors? Then set exit status appropriately
            setExitValue(EXITCODE_ERROR);
        } else {
            // All tasks in wait finished OK?
            // Set exitCode based on VM's run state
            switch (getRunState()) {
                case FATAL_ERROR:
                    setExitValue(EXITCODE_FATAL_ERROR);
                    break;

                case THREAD_KILLED:
                    setExitValue(EXITCODE_KILLED);
                    break;

                default:
                    // All is OK, exitCode is zero
                    setExitValue(0);
                    break;
            }
        }
    }

    /**
     * Fatal error:
     * 1) Show a fatal error message
     * 2) Show stack trace
     * 3) Save checkpoint
     * 4) Exit VM
     */
    public void fatalError(BdsNode bdsnode, String message) {
        setRunState(RunState.FATAL_ERROR);
        errorMessage = message;
        String filePos = getFileLinePos(bdsnode);
        System.err.println("Fatal error: " //
                + filePos + (filePos.isEmpty() ? "" : ". ") //
                + message);

        // Show BDS stack trace
        try {
            String stackTrace = stackTrace();
            if (!stackTrace.isEmpty()) System.err.println("Stack trace:\n" + stackTrace);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Create checkpoint
        try {
            String checkpointFileName = checkpoint(bdsnode);
            if (checkpointFileName.isEmpty())
                log("Creating checkpoint file: Config or command line option disabled checkpoint file creation, nothing done.");
            else log("Created checkpoint file: '" + checkpointFileName + "'");
        } catch (Throwable t) {
            // Ignore serialization error at this stage (we are within a fatal error)
        }

        // Set exit value
        setExitValue(EXITCODE_FATAL_ERROR);
    }

    public void fatalError(String message) {
        fatalError(getBdsNodeCurrent(), message);
    }

    /**
     * Freeze thread (e.g. to serialize states)
     */
    protected void freeze(boolean freeze) {
        List<BdsThread> bdsThreads = getBdsThreadsAll();
        for (BdsThread th : bdsThreads)
            th.setFreeze(freeze);
    }

    /**
     * Create a generic ID (task ID, sys ID, etc)
     */
    public String generateId(BdsNode node, String tag, String name, boolean usePid, boolean useRand) {
        long pid = usePid ? ProcessHandle.current().pid() : -1;
        long rand = useRand ? Math.abs((new Random()).nextLong()) : -1;

        // Use module name
        int ln = -1;
        String module = null;
        if (node != null) {
            module = node.getFileName();
            ln = node.getLineNum();
        }
        if (module != null) module = Gpr.removeExt(Gpr.baseName(module));

        return getBdsThreadId() //
                + "/" + tag //
                + (module == null ? "" : "." + module) //
                + (name == null ? "" : "." + name) //
                + (ln > 0 ? ".line_" + ln : "") //
                + ".id_" + nextId() //
                + (pid < 0 ? "" : ".pid_" + pid) //
                + (rand < 0 ? "" : String.format(".%08x", rand)) //
                ;
    }

    /**
     * Get node currently executed by the VM
     */
    public BdsNode getBdsNodeCurrent() {
        int nodeId = vm.getNodeId();
        return BdsNodeFactory.get().getNode(nodeId);
    }

    public String getBdsThreadId() {
        return bdsThreadId;
    }

    public List<BdsThread> getBdsThreads() {
        List<BdsThread> list = new ArrayList<>();
        list.addAll(bdsChildThreadsById.values());
        return list;
    }

    /**
     * Get all child threads
     */
    public List<BdsThread> getBdsThreadsAll() {
        List<BdsThread> list = new ArrayList<>();
        list.add(this);

        for (BdsThread bth : bdsChildThreadsById.values())
            list.addAll(bth.getBdsThreadsAll());

        return list;
    }

    /**
     * Get variable's map as a bool
     */
    public boolean getBool(String varName) {
        return getScope().getValue(varName).asBool();
    }

    public Config getConfig() {
        return config;
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(long exitValue) {
        this.exitValue = (int) exitValue;
        if (vm != null) vm.setExitCode((int) exitValue);
    }

    /**
     * Try to get file / line / pos information
     * Recurse to parent node if not found
     */
    public String getFileLinePos(BdsNode bdsNode) {
        StringBuilder sb = new StringBuilder();

        // If the node has file, add it
        if (bdsNode.getFileNameCanonical() != null) {
            sb.append(bdsNode.getFileName());
            // If the node has line number, add it
            if (bdsNode.getLineNum() > 0) {
                sb.append(", line " + bdsNode.getLineNum());
                // If the node has line possition number, add it
                if (bdsNode.getCharPosInLine() >= 0) {
                    sb.append(", pos " + (bdsNode.getCharPosInLine() + 1));
                }
            }
        }

        // Nothing found? Return empty
        return sb.toString();
    }

    /**
     * Get variable's map as an int
     */
    public long getInt(String varName) {
        return getScope().getValue(varName).asInt();
    }

    public String getLogBaseName() {
        return bdsThreadId;
    }

    public BdsThread getParent() {
        return parent;
    }

    public ProgramUnit getProgramUnit() {
        return (ProgramUnit) statement;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * Get variable's map as a real
     */
    public double getReal(String varName) {
        return getScope().getValue(varName).asReal();
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    /**
     * Get 'root' thread
     */
    public BdsThread getRoot() {
        if (parent == null) return this;
        return parent.getRoot();
    }

    public RunState getRunState() {
        return runState;
    }

    public void setRunState(RunState runState) {
        this.runState = runState;

        if (vm == null) return;

        switch (runState) {
            case OK:
                vm.setRun(true);
                break;

            case FATAL_ERROR:
            case FINISHED:
            case THREAD_KILLED:
                vm.setRun(false);
                break;

            default:
                break;
        }
    }

    public Scope getScope() {
        return vm.getScope();
    }

    public Statement getStatement() {
        return statement;
    }

    /**
     * Set program unit and update bigDataScriptThreadId"
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
        createBdsThreadId(); // Create thread ID based on program's name
    }

    /**
     * Get variable's map as a string
     */
    public String getString(String varName) {
        return getScope().getValue(varName).toString();
    }

    /**
     * Get a task (this thread or any child thread)
     */
    public Task getTask(String taskId) {
        Task task = taskDependecies.getTask(taskId);
        if (task != null) return task;
        for (BdsThread bdsThread : bdsChildThreadsById.values()) {
            task = bdsThread.getTask(taskId);
            if (task != null) return task;
        }
        return null;
    }

    /**
     * Get all tasks
     */
    public Collection<Task> getTasks() {
        return taskDependecies.getTasks();
    }

    /**
     * Get a thread
     */
    public synchronized BdsThread getThread(String threadId) {
        // Do we have this thread?
        BdsThread bdsth = bdsChildThreadsById.get(threadId);
        if (bdsth != null) return bdsth;

        // Search in all child threads
        for (BdsThread child : bdsChildThreadsById.values()) {
            bdsth = child.getThread(threadId);
            if (bdsth != null) return bdsth;
        }

        // Not found
        return null;
    }

    public Timer getTimer() {
        return timer;
    }

    /**
     * Get variable's value (as a Value object)
     */
    public Value getValue(String varName) {
        return getScope().getValue(varName);
    }

    public BdsVm getVm() {
        return vm;
    }

    /**
     * Execute dependency tasks to achieve goal 'out'
     */
    public synchronized ValueList goal(String out) {
        ValueList taskIds = new ValueList(TypeList.get(Types.STRING));

        // Find dependencies
        Set<Task> tasks = taskDependecies.goal(this, out);

        // No update needed?
        if (tasks == null) {
            if (isDebug()) Gpr.debug("Goal '" + out + "' has no dependent tasks. Nothing to do.");
            return taskIds;
        }

        // Convert to task IDs
        for (Task t : tasks)
            taskIds.add(new ValueString(t.getId()));

        return taskIds;
    }

    /**
     * Does 'varName' exists?
     */
    public boolean hasVariable(String varName) {
        return getScope().getValue(varName) != null;
    }

    /**
     * Initialize and start threads before running ProgramUnit
     */
    void initThreads() {
        // Start child threads (e.g. when recovering)
        for (BdsThread bth : bdsChildThreadsById.values()) {
            if (!bth.isAlive() && !bth.getRunState().isFinished()) bth.start();
        }

        // Add this thread to collections
        BdsThreads.getInstance().add(this);
    }

    @Override
    public boolean isDebug() {
        return config != null && config.isDebug();
    }

    /**
     * Is this the 'root' (main) thread?
     */
    public boolean isRoot() {
        return parent == null;
    }

    public boolean isThreadsDone() {
        return bdsChildThreadsById.isEmpty();
    }

    @Override
    public boolean isVerbose() {
        return config != null && config.isVerbose();
    }

    /**
     * Kill: Stop execution of current thread
     */
    public void kill() {
        setRunState(RunState.THREAD_KILLED); // Set state to 'kill'
    }

    /**
     * Kill one task/thread
     */
    public void kill(Value vtaskId) {
        String taskId = vtaskId.asString();
        if (taskId == null) return;

        // Is 'tid' a bdsThread ID?
        if (bdsChildThreadsById.containsKey(taskId)) {
            // Kill thread
            bdsChildThreadsById.get(taskId).kill();
        } else {
            // Kill task: Just send a kill to all Executioners
            for (Executioner executioner : Executioners.getInstance().getAll())
                executioner.kill(taskId);
        }
    }

    /**
     * Kill all tasks/threads in the list
     */
    public void kill(ValueList taskIds) {
        if (taskIds == null) return;

        // We are done when ALL tasks are done
        for (Value tid : taskIds)
            kill(tid);
    }

    /**
     * Parallel: Create and start a new bds thread
     */
    public BdsThread parallel(BdsVm vmpar) {
        BdsThread newBdsThread = new BdsThread(this, statement, config, vmpar);

        push(new ValueString(newBdsThread.getBdsThreadId())); // Parent process: return child's thread ID
        newBdsThread.push(new ValueString("")); // Fork returns empty string on child process

        newBdsThread.start();
        return newBdsThread;
    }

    public Value pop() {
        return vm.pop();
    }

    /**
     * Print thread information
     */
    public void print() {
        // Create a list with program file and all included files
        List<BdsNode> nodeWithFiles = BdsNodeWalker.findNodes(statement, StatementInclude.class, true, false);
        nodeWithFiles.add(0, statement);

        // Show code
        for (BdsNode bwf : nodeWithFiles) {
            System.out.println("Program file: '" + bwf.getFileName() + "'");
            printCode(((BlockWithFile) bwf).getFileText());
        }

        // Show stack trace
        System.out.println("Stack trace:");
        System.out.println(stackTrace());

        // Show scopes
        for (Scope scope = getScope(); scope != null; scope = scope.getParent())
            if (!scope.isEmpty()) {
                System.out.println(scope.toString(false, false));
            }
    }

    void printCode(String code) {
        // Show file contents
        int lineNum = 1;
        for (String line : code.split("\n"))
            System.out.println(String.format("%6d |%s", lineNum++, line));
        System.out.println();
    }

    public void push(Value val) {
        vm.push(val);
    }

    /**
     * Remove a child thread
     */
    public synchronized void remove(BdsThread bdsThread) {
        if (BdsThreads.doNotRemoveThreads) return;

        // Add all files to be removed to the parent
        removeOnExit.addAll(bdsThread.removeOnExit);

        // Remove child thread
        bdsChildThreadsById.remove(bdsThread.getBdsThreadId());
    }

    /**
     * Remove stale files (controlled by '-noRmOnExit' command line option)
     */
    void removeStaleData() {
        if (!isRoot()) return;

        // Any files to delete?
        if (!removeOnExit.isEmpty()) {
            if (config != null && config.isNoRmOnExit()) {
                log("\tDeleting stale files: Cancelled ('noRmOnExit' is active).");
            } else {
                log("Deleting stale files/dirs");
                for (Data dfile : removeOnExit) {
                    log("Deleting file/dir '" + dfile + "'");
                    dfile.delete();
                }
            }
        }
    }

    void reportAfterRun() {
        // Create reports? Only root thread creates reports
        if (config != null && isRoot()) {
            var timeVsSizeReport = true;
            // Create HTML report?
            if (config.isReportHtml() || config.isLog()) {
                Report report = new Report(this, false, timeVsSizeReport);
                timeVsSizeReport = false;
                report.createReports();
            }

            // Create YAML report?
            if (config.isReportYaml() || config.isLog()) {
                Report report = new Report(this, true, timeVsSizeReport);
                report.createReports();
            }
        }
    }

    /**
     * Remove the file on exit
     */
    public synchronized boolean rmOnExit(Data data) {
        // Add file for removal
        debug("Delete file on exit (rmOnExit): Adding file/dir '" + data + "'");
        return removeOnExit.add(data);
    }

    /**
     * Remove the file on exit
     */
    public boolean rmOnExit(Value vfile) {
        String file = vfile.asString();
        return rmOnExit(data(file));
    }

    /**
     * Remove the file on exit
     */
    public boolean rmOnExitCancel(Value vfile) {
        String file = vfile.asString();
        var vurl = data(file).url();

        // Find matching url
        Data toDelete = null;
        for (Data d : removeOnExit) {
            if (d.url().equals(vurl)) {
                toDelete = d;
                break;
            }
        }

        // Remove entry
        if (toDelete != null) {
            debug("Delete file on exit cancel (rmOnExitCancel): File/Dir '" + toDelete + "' will not be deleted on exit");
            return removeOnExit.remove(toDelete);
        }
        return false;
    }

    /**
     * Remove the files on exit
     */
    public synchronized void rmOnExit(ValueList files) {
        for (Value v : files)
            rmOnExit(v);
    }

    /**
     * Cancel a "Remove the files on exit"
     */
    public synchronized void rmOnExitCancel(ValueList files) {
        for (Value v : files)
            rmOnExitCancel(v);
    }

    @Override
    public void run() {
        timer = new Timer();
        createLogDir(); // Create log dir
        initThreads(); // Initialize and start threads
        runStatement(); // Run statement (i.e. run program)
        var allTaskFinishedOk = waitImplicit(); // Implicit wait at the end of the program
        exitCode(allTaskFinishedOk); // Calculate exit code
        cleanupBeforeReport(); // Clean up before final report
        reportAfterRun(); // Create reports
        clearupAfterReport(); // Clean up after final report
    }

    /**
     * Run statements (i.e. run program)
     */
    protected void runStatement() {
        try {
            vm.run();
        } catch (Throwable t) {
            setRunState(RunState.FATAL_ERROR);
            if (isVerbose()) throw new RuntimeException("Fatal error", t);
            else error("Fatal error: Program execution finished");
        }
    }

    /**
     * Check that stack has size zero (perform this check after execution finishes)
     */
    public void sanityCheckStack() {
        vm.sanityCheckStack();
    }

    /**
     * Freeze execution before next node run
     */
    public void setFreeze(boolean freeze) {
        this.freeze = freeze;
    }

    public void setRandomSeed(long seed) {
        random = new Random(seed);
    }

    /**
     * Show BDS calling stack
     */
    public String stackTrace() {
        return vm.stackTrace();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BdsThread: " + bdsThreadNum + "\n");
        sb.append("\tRun state : " + getRunState() + "\n");
        sb.append("\tProgram   :\n" + statement.toStringTree("\t\t", "program") + "\n");
        return sb.toString();
    }

    public String toStringClassFileLinePos() {
        return getBdsNodeCurrent().toStringClassFileLinePos();
    }

    /**
     * Send task from un-serialization to execution list
     */
    public void unserializedTasksRestore() {
        for (Task task : taskDependecies.getTasks()) {
            if (!task.isStateFinished() // Not finished?
                    && !task.isDependency() // Don't execute dependencies, unless needed
            ) {
                TaskVmOpcode.execute(this, task);
            }
        }
    }

    /**
     * Wait for one task/thread to finish
     */
    public boolean wait(String id) {
        if (id == null) return true;

        // Note: We could be waiting for another thread's taskID.
        //       So we need to wait on the global TaskDependencies
        if (TaskDependecies.get().hasTask(id)) return TaskDependecies.get().waitTask(id);

        // Note: We could be waiting for a non-child thread to finish
        //       So we have to wait on the 'root' BdsThread'
        BdsThread bdsThRoot = getRoot();
        BdsThread bdsTh = bdsThRoot.getThread(id);
        if (bdsTh != null) return waitThread(bdsTh);
        return true; // Nothing to do (already finished)
    }

    /**
     * Wait for all tasks/threads in the list to finish
     *
     * @return true if all tasks/threads finished OK (or failed but were allowed to fail, i.e. canFail = true)
     */
    public boolean wait(ValueList ids) {
        if (ids == null) return true;

        boolean ok = true;

        // We are done when ALL tasks/threads are done
        for (Value id : ids)
            ok &= wait(id.toString());

        return ok;
    }

    public boolean waitAll() {
        boolean ok = taskDependecies.waitTasksAll();
        ok &= waitThreadAll();
        return ok;
    }

    /**
     * Wait for one thread to finish
     *
     * @return true if thread finished OK
     */
    public boolean waitThread(BdsThread bdsThread) {
        try {
            if (bdsThread != null) {
                debug("Waiting for parallel '" + bdsThread.getBdsThreadId() + "' to finish. RunState: " + bdsThread.getRunState());
                if (bdsThread.getRunState().isFinished()) return true;
                bdsThread.join();
                return bdsThread.getExitValue() == 0; // Finished OK?
            }
        } catch (InterruptedException e) {
            if (isVerbose()) e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean waitThreadAll() {
        // Wait for all tasks to finish
        boolean ok = true;

        if (!isThreadsDone()) debug("Waiting for all 'parrallel' to finish.");

        // Populate a list of threads to avoid concurrent modification
        List<BdsThread> bdsThreads = new LinkedList<>();
        synchronized (this) {
            bdsThreads.addAll(bdsChildThreadsById.values());
        }

        // Wait for all threads
        for (BdsThread bdsth : bdsThreads)
            ok &= waitThread(bdsth);

        return ok;
    }
}
