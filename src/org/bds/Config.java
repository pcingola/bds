package org.bds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.bds.executioner.ExecutionerClusterFake;
import org.bds.executioner.Executioners.ExecutionerType;
import org.bds.executioner.TaskLogger;
import org.bds.scope.GlobalScope;
import org.bds.task.TailFile;
import org.bds.task.Task;
import org.bds.util.Gpr;
import org.bds.util.Timer;

/**
 * Config file
 *
 * @author pcingola
 */
public class Config implements Serializable, BdsLog {

	private static final long serialVersionUID = 6558109289073244716L;
	// Singleton instance
	private static Config configInstance = null; // Config is some kind of singleton because we want to make it accessible from everywhere
	// Default values
	private static final String DEFAULT_BDS_HOME = Gpr.HOME + "/.bds"; // Bds home directory
	private static final String BDS_INCLUDE_PATH = "BDS_PATH"; // BDS include path (colon separated list of directories to look for include files)
	private static final String DEFAULT_CONFIG_BASENAME = "bds.config"; // We want to put bds.config together with bds executable
	private static final String DEFAULT_CONFIG_DIR = DEFAULT_BDS_HOME; // by default BDS_HOME == HOME
	public static final String DEFAULT_CONFIG_FILE = DEFAULT_CONFIG_DIR + "/" + DEFAULT_CONFIG_BASENAME;
	private static final String DEFAULT_INCLUDE_DIR = DEFAULT_CONFIG_DIR + "/include";
	private static final int DEFAULT_MAX_NUMBER_OF_RUNNING_THREADS = 512;
	private static final String DEFAULT_SYS_SHELL = "/bin/bash -euo pipefail -c"; // Note: This executes a script, so it requires the "-c" right before script name
	public static final String DEFAULT_TASK_SHELL = "/bin/bash -eu\nset -o pipefail"; // Use '-euo pipefail' so that shell script stops after first error
	private static final long DEFAULT_TIMEOUT = 1L * 24 * 60 * 60;
	private static final String DEFAULT_TMP_DIR = "/tmp";
	private static final int DEFAULT_WAIT_AFTER_TASK_RUN = 0;
	private static final int DEFAULT_WAIT_FILE_CHECK = -1;
	private static final int DEFAULT_WAIT_TEXT_FILE_BUSY = 10;
	public static final int MAX_NUMBER_OF_RUNNING_THREADS_MIN_VALUE = 50; // If maxThreads in configuration file is too small, we'll consider it an error and use this number
	// Config entry names
	public static final String CLUSTER_GENERIC_KILL = "clusterGenericKill"; // Cluster: Generic cluster
	public static final String CLUSTER_GENERIC_POSTMORTEMINFO = "clusterGenericPostMortemInfo";
	public static final String CLUSTER_GENERIC_RUN = "clusterGenericRun";
	public static final String CLUSTER_GENERIC_STAT = "clusterGenericStat";
	public static final String CLUSTER_KILL_ADDITIONAL_ARGUMENTS = "clusterKillAdditionalArgs"; // Cluster additional command line arguments (when killing tasks)
	public static final String CLUSTER_POSTMORTEMINFO_ADDITIONAL_ARGUMENTS = "clusterPostMortemInfoAdditionalArgs"; // Cluster additional command line arguments (when requesting information about a failed task)
	public static final String CLUSTER_POSTMORTEMINFO_DISABLED = "clusterPostMortemDisabled"; // Some clusters do not provide information after the process dies
	public static final String CLUSTER_RUN_ADDITIONAL_ARGUMENTS = "clusterRunAdditionalArgs"; // Cluster additional command line arguments (when running tasks)
	public static final String CLUSTER_SGE_MEM = "sge.mem";
	public static final String CLUSTER_SGE_PE = "sge.pe";
	public static final String CLUSTER_SGE_TIME_IN_SECS = "sge.timeInSecs";
	public static final String CLUSTER_SGE_TIMEOUT_HARD = "sge.timeout";
	public static final String CLUSTER_SGE_TIMEOUT_SOFT = "sge.timeoutSoft";
	public static final String CLUSTER_SSH_NODES = "ssh.nodes"; // Cluster ssh
	public static final String CLUSTER_STAT_ADDITIONAL_ARGUMENTS = "clusterStatAdditionalArgs"; // Cluster additional command line arguments (when requesting information about all tasks)
	public static final String DISABLE_CHECKPOINT_CREATE = "disableCheckpoint"; // Disable checkpoint creation
	public static final String DISABLE_RM_ON_EXIT = "disableRmOnExit";
	public static final String CLUSTER_FAKE_DIR = "cluster.fake.dir";
	public static final String FILTER_OUT_TASK_HINT = "filterOutTaskHint"; // Lines to filter out from task hint
	public static final String MAX_NUMBER_OF_RUNNING_THREADS = "maxThreads";
	public static final String PID_CHECK_TASK_RUNNING_COLUMN = "pidColumnCheckTaskRunning"; // Regex used for checking PID
	public static final String PID_CHECK_TASK_RUNNING_REGEX = "pidRegexCheckTaskRunning"; // Regex used for checking PID
	public static final String PID_REGEX = "pidRegex"; // Regex used for PID
	public static final String QUEUE = "queue";
	public static final String REPORT_HTML = "reportHtml"; // Create an HTML report
	public static final String REPORT_YAML = "reportYaml"; // Create a YAML report
	public static final String SHOW_TASK_CODE = "showTaskCode"; // Always show task's code (sys commands)
	public static final String SYS_SHELL = "sysShell"; // Sys's shell
	public static final String TAIL_LINES = "tailLines"; // Number of lie to use in 'tail'
	public static final String TASK_MAX_HINT_LEN = "taskMaxHintLen";
	public static final String TASK_PRELUDE = "taskPrelude"; // Task prelude
	public static final String TASK_SHELL = "taskShell"; // Task's shell
	public static final String TMP_DIR = "tmpDir";
	public static final String WAIT_AFTER_TASK_RUN = "waitAfterTaskRun";
	public static final String WAIT_FILE_CHECK = "waitFileCheck";
	public static final String WAIT_TEXT_FILE_BUSY = "waitTextFileBusy";
	// Other
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	String bdsHome = DEFAULT_BDS_HOME;
	boolean clusterPostMortemInfoDisabled = false;
	String clusterSgeMem = "";
	String clusterSgePe = "";
	boolean clusterSgeTimeInSecs = false;
	String clusterSgeTimeoutHard = "";
	String clusterSgeTimeoutSoft = "";
	String clusterSshNodes = "";
	String configDirName = DEFAULT_CONFIG_DIR;
	String configFileName;
	boolean coverage; // Perform coverage analysis (only when test cases are run)
	int cpus = 1;
	int cpusLocal = Gpr.NUM_CORES;
	boolean debug = false; // Debug mode?
	boolean dryRun = false; // Is this a dry run? (i.e. don't run commands, just show what they do).
	boolean extractSource = false; // Extract source code from checkpoint file
	String fakeClusterDir;
	ArrayList<String> filterOutTaskHint;
	ArrayList<String> includePath;
	boolean log = false; // Log all commands?
	int maxThreads = DEFAULT_MAX_NUMBER_OF_RUNNING_THREADS; // Maximum number of simultaneous threads (e.g. when running 'qsub' commands)
	long mem = -1; // Default amount of memory: -1 (unrestricted)
	// !!!!! FIXME: REMOVE?
	// MonitorTask monitorTask;
	boolean noCheckpoint = false; // Do not create checkpoint files
	String node = "";
	boolean noRmOnExit = false; // Avoid removing files on exit
	int pidCheckTaskRunningColumn = 0; // Which column to use when parsing PID
	String pidFile = "pidFile" + (new Date()).getTime() + ".txt"; // Default PID file
	String pidRegex = ""; // Regex used to extract PID from cluster command (e.g. qsub)
	String pidRegexCheckTaskRunning = ""; // Regex to match PID when bds checks that tasks are running in the cluster
	Properties properties;
	String queue = ""; // Queue name
	boolean quiet = false; // Quiet mode?
	String reportFileName; // Preferred file name to use for progress and final report
	boolean reportHtml = false; // Use HTML report format
	boolean reportYaml = false; // Use YAML report format
	boolean showTaskCode = false; // Always show task's code (sys statements)
	String sysShell = DEFAULT_SYS_SHELL; // System shell
	String system = ExecutionerType.LOCAL.toString().toLowerCase(); // System type
	// !!!!! FIXME: REMOVE?
	// Tail tail;
	int tailLines = TailFile.DEFAULT_TAIL; // Number of lines to use in 'tail'
	int taskFailCount = 0; // Number of times a task is allowed to fail (i.e. number of re-tries)
	TaskLogger taskLogger;
	Integer taskMaxHintLen = Task.MAX_HINT_LEN; // Max number of characters to use in tasks's "hint"
	String taskPrelude = ""; // Task prelude
	String taskShell = DEFAULT_TASK_SHELL; // Task shell
	long timeout = DEFAULT_TIMEOUT;
	String tmpDir = DEFAULT_TMP_DIR; // Tmp directory
	boolean verbose = false; // Verbose mode?
	int waitAfterTaskRun = DEFAULT_WAIT_AFTER_TASK_RUN; // Wait some milisec after task run
	int waitFileCheck = DEFAULT_WAIT_FILE_CHECK; // Wait some milisecs after task finished before checking if output files exists
	int waitTextFileBusy = DEFAULT_WAIT_TEXT_FILE_BUSY; // Wait some milisecs after writing a shell file to disk (before execution)
	long wallTimeout = DEFAULT_TIMEOUT;

	/**
	 * Get singleton
	 */
	public static Config get() {
		if (configInstance == null) configInstance = new Config();
		return configInstance;
	}

	/**
	 * Reset singleton
	 */
	public static void reset() {
		configInstance = null;
	}

	public Config() {
		this(null);
	}

	/**
	 * Create a configuration from 'configFileName'
	 */
	public Config(String configFileName) {
		bdsHome = bdsHome();
		this.configFileName = configFileName != null ? configFileName : DEFAULT_CONFIG_FILE;
		configDirName = configFileName != null ? Gpr.dirName(configFileName) : DEFAULT_CONFIG_DIR;
		fakeClusterDir = configDirName + "/" + ExecutionerClusterFake.FAKE_CLUSTER_DIR_NAME;
		configInstance = this;
	}

	/**
	 * Find BdsHome value
	 */
	String bdsHome() {
		String envBdsHome = System.getenv("BDS_HOME");
		return envBdsHome != null ? envBdsHome : DEFAULT_BDS_HOME;
	}

	/**
	 * Find a bds.config file
	 */
	String findConfigFile(String configFileName) {
		// The user specified a configuration file (different than default)
		if (configFileName != null && !configFileName.equals(DEFAULT_CONFIG_FILE)) {
			if (Gpr.exists(configFileName)) return configFileName;

			// The user specified config file doesn't exist: Error
			throw new RuntimeException("Config file '" + configFileName + "' not found");
		}

		// Create a search path
		String bdsDir = new File(Gpr.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
		String[] searchPaths = { ".", bdsDir, Gpr.HOME, bdsHome };

		for (String d : searchPaths) {
			String cf = d + "/" + DEFAULT_CONFIG_BASENAME;
			debug("Trying config file '" + cf + "'");
			if (Gpr.exists(cf)) return cf;
		}

		// Nothing found, try default
		return DEFAULT_CONFIG_FILE;
	}

	public String getBdsHome() {
		return bdsHome;
	}

	/**
	 * Get a property as a boolean
	 */
	protected boolean getBool(String propertyName, boolean defaultValue) {
		String val = getString(propertyName);
		if (val == null) return defaultValue;
		return Gpr.parseBoolSafe(val.trim());
	}

	public String getClusterSgeMem() {
		return clusterSgeMem;
	}

	public String getClusterSgePe() {
		return clusterSgePe;
	}

	public String getClusterSgeTimeoutHard() {
		return clusterSgeTimeoutHard;
	}

	public String getClusterSgeTimeoutSoft() {
		return clusterSgeTimeoutSoft;
	}

	public String getClusterSshNodes() {
		return clusterSshNodes;
	}

	public String getConfigDirName() {
		return configDirName;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public int getCpus() {
		return cpus;
	}

	public int getCpusLocal() {
		return cpusLocal;
	}

	/**
	 * Get a property as a double
	 */
	protected double getDouble(String propertyName, double defaultValue) {
		String val = getString(propertyName);
		if (val == null) return defaultValue;
		return Gpr.parseDoubleSafe(val);
	}

	public String getFakeClusterDir() {
		return fakeClusterDir;
	}

	public ArrayList<String> getFilterOutTaskHint() {
		return filterOutTaskHint;
	}

	/**
	 * A collection of strings showing where to search for include files
	 */
	public Collection<String> getIncludePath() {
		// Create array if needed
		if (includePath == null) {
			includePath = new ArrayList<>();

			// Add by search order
			includePath.add("."); // Current dir (obviously)
			includePath.add(DEFAULT_INCLUDE_DIR); // Default include path ($HOME/.bds/include)

			// Add BDS_PATH environment variable
			String bdsPath = System.getenv(BDS_INCLUDE_PATH);
			if ((bdsPath != null) && !bdsPath.isEmpty()) {
				String incPaths[] = bdsPath.split(":");
				for (String incPath : incPaths) {
					if (!incPath.isEmpty()) includePath.add(incPath);
				}
			}
		}

		return includePath;
	}

	/**
	 * Get a property as a int
	 */
	protected int getInt(String propertyName, int defaultValue) {
		String val = getString(propertyName);
		if (val == null) return defaultValue;
		return Gpr.parseIntSafe(val.trim());
	}

	/**
	 * Get a property as a long
	 */
	protected long getLong(String propertyName, long defaultValue) {
		String val = getString(propertyName);
		if (val == null) return defaultValue;
		return Gpr.parseLongSafe(val.trim());
	}

	/**
	 * Max number of concurrent threads
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	public long getMem() {
		return mem;
	}

	public String getNode() {
		return node;
	}

	public int getPidCheckTaskRunningColumn() {
		return pidCheckTaskRunningColumn;
	}

	public String getPidFile() {
		return pidFile;
	}

	public String getPidRegex() {
		return getPidRegex("");
	}

	public String getPidRegex(String defaultPidRegex) {
		if (pidRegex == null || pidRegex.isEmpty()) return defaultPidRegex;
		return pidRegex;
	}

	public String getPidRegexCheckTasksRunning() {
		// TODO: Remove next line
		// if (pidRegexCheckTaskRunning == null || pidRegexCheckTaskRunning.isEmpty()) return defaultPidRegex;
		return pidRegexCheckTaskRunning;
	}

	public String getQueue() {
		return queue;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	/**
	 * Get a property as a string
	 */
	protected String getString(String propertyName) {
		return properties.getProperty(propertyName);
	}

	protected String getString(String propertyName, String defaultValue) {
		String val = getString(propertyName);
		if (val == null) return defaultValue;
		val = val.trim();
		if (val.startsWith("\"") && val.endsWith("\"")) val = val.substring(1, val.length() - 1);
		return val;
	}

	public String[] getStringArray(String propertyName) {
		return getStringArray(propertyName, false);
	}

	/**
	 * Get a configuration map and split is into an array (using regex '\\s+')
	 */
	public String[] getStringArray(String propertyName, boolean required) {
		String val = getString(propertyName);
		if (val == null) {
			if (required) throw new RuntimeException("Cannot find configuration parameter '" + propertyName + "'" + (configFileName != null ? " in config file '" + configFileName + "'" : ""));
			return EMPTY_STRING_ARRAY;
		}

		// Parse and add to list
		ArrayList<String> vals = new ArrayList<>();
		for (String v : val.split("\\s+")) {
			v = v.trim();
			if (!v.isEmpty()) vals.add(v.trim());
		}

		// Convert to string array
		String valsArray[] = vals.toArray(EMPTY_STRING_ARRAY);

		// Show
		if (isDebug()) {
			Timer.showStdErr("Config: Parsing '" + propertyName + "'. Number of additional arguments: " + valsArray.length);
			for (int i = 0; i < valsArray.length; i++) {
				System.err.println("\t\t\t" + i + "\t'" + valsArray[i] + "'");
			}
		}

		return valsArray;
	}

	public String getSysShell() {
		return sysShell;
	}

	public String getSystem() {
		return system;
	}

	public int getTailLines() {
		return tailLines;
	}

	public int getTaskFailCount() {
		return taskFailCount;
	}

	public TaskLogger getTaskLogger() {
		if (taskLogger == null) {
			taskLogger = new TaskLogger(getPidFile());
		}
		return taskLogger;
	}

	public int getTaskMaxHintLen() {
		return taskMaxHintLen;
	}

	public String getTaskPrelude() {
		return taskPrelude;
	}

	public String getTaskShell() {
		return taskShell;
	}

	public long getTimeout() {
		return timeout;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public int getWaitAfterTaskRun() {
		return waitAfterTaskRun;
	}

	public int getWaitFileCheck() {
		return waitFileCheck;
	}

	public int getWaitTextFileBusy() {
		return waitTextFileBusy;
	}

	public long getWallTimeout() {
		return wallTimeout;
	}

	public boolean isClusterPostMortemInfoDisabled() {
		return clusterPostMortemInfoDisabled;
	}

	//	public void kill() {
	//		if (tail != null) {
	//			tail.kill(); // Kill tail process
	//			tail = null;
	//		}
	//
	//		if (monitorTask != null) {
	//			// monitorTask.kill();
	//			monitorTask = null;
	//		}
	//	}

	public boolean isClusterSgeTimeInSecs() {
		return clusterSgeTimeInSecs;
	}

	public boolean isCoverage() {
		return coverage;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public boolean isExtractSource() {
		return extractSource;
	}

	@Override
	public boolean isLog() {
		return log;
	}

	public boolean isNoCheckpoint() {
		return noCheckpoint;
	}

	public boolean isNoRmOnExit() {
		return noRmOnExit;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public boolean isReportHtml() {
		return reportHtml;
	}

	public boolean isReportYaml() {
		return reportYaml;
	}

	public boolean isShowTaskCode() {
		return showTaskCode;
	}

	@Override
	public boolean isVerbose() {
		return verbose;
	}

	public void load() {
		read(configFileName); // Read config file
		parse(); // Parse values form properties
	}

	/**
	 * Parse some values
	 */
	void parse() {
		clusterPostMortemInfoDisabled = getBool(CLUSTER_POSTMORTEMINFO_DISABLED, clusterPostMortemInfoDisabled);
		clusterSgeMem = getString(CLUSTER_SGE_MEM, clusterSgeMem);
		clusterSgePe = getString(CLUSTER_SGE_PE);
		clusterSgeTimeInSecs = getBool(CLUSTER_SGE_TIME_IN_SECS, clusterSgeTimeInSecs);
		clusterSgeTimeoutHard = getString(CLUSTER_SGE_TIMEOUT_HARD, clusterSgeTimeoutHard);
		clusterSgeTimeoutSoft = getString(CLUSTER_SGE_TIMEOUT_SOFT, clusterSgeTimeoutSoft);
		clusterSshNodes = getString(Config.CLUSTER_SSH_NODES, clusterSshNodes);
		cpusLocal = getInt(GlobalScope.GLOBAL_VAR_LOCAL_CPUS, cpusLocal);
		cpus = getInt(GlobalScope.GLOBAL_VAR_TASK_OPTION_CPUS, cpus);
		fakeClusterDir = getString(CLUSTER_FAKE_DIR, fakeClusterDir);
		maxThreads = getInt(MAX_NUMBER_OF_RUNNING_THREADS, maxThreads);
		mem = getLong(GlobalScope.GLOBAL_VAR_TASK_OPTION_MEM, mem);
		noCheckpoint = getBool(DISABLE_CHECKPOINT_CREATE, noCheckpoint);
		node = getString(GlobalScope.GLOBAL_VAR_TASK_OPTION_NODE, "");
		noRmOnExit = getBool(DISABLE_RM_ON_EXIT, noRmOnExit);
		pidCheckTaskRunningColumn = getInt(PID_CHECK_TASK_RUNNING_COLUMN, pidCheckTaskRunningColumn);
		pidRegex = getString(PID_REGEX, pidRegex).trim();
		pidRegexCheckTaskRunning = getString(PID_CHECK_TASK_RUNNING_REGEX, pidRegexCheckTaskRunning).trim();
		queue = getString(QUEUE, queue);
		showTaskCode = getBool(SHOW_TASK_CODE, showTaskCode);
		sysShell = getString(Config.SYS_SHELL, sysShell);
		tailLines = getInt(TAIL_LINES, tailLines);
		reportHtml = getBool(REPORT_HTML, reportHtml);
		reportYaml = getBool(REPORT_YAML, reportYaml);
		system = getString(GlobalScope.GLOBAL_VAR_TASK_OPTION_SYSTEM, system);
		taskFailCount = getInt(GlobalScope.GLOBAL_VAR_TASK_OPTION_RETRY, taskFailCount);
		taskMaxHintLen = getInt(TASK_MAX_HINT_LEN, taskMaxHintLen);
		taskPrelude = getString(TASK_PRELUDE, taskPrelude);
		taskShell = getString(TASK_SHELL, taskShell);
		timeout = getLong(GlobalScope.GLOBAL_VAR_TASK_OPTION_TIMEOUT, timeout);
		tmpDir = getString(TMP_DIR, tmpDir);
		waitAfterTaskRun = (int) getLong(WAIT_AFTER_TASK_RUN, waitAfterTaskRun);
		waitFileCheck = (int) getLong(WAIT_FILE_CHECK, waitFileCheck);
		waitTextFileBusy = (int) getLong(WAIT_TEXT_FILE_BUSY, waitTextFileBusy);
		wallTimeout = getLong(GlobalScope.GLOBAL_VAR_TASK_OPTION_WALL_TIMEOUT, wallTimeout);

		// Sanity checks
		if (maxThreads < MAX_NUMBER_OF_RUNNING_THREADS_MIN_VALUE) {
			warning("Config: Attempt to set 'maxThreads' to " + maxThreads + ". Too small, using " + MAX_NUMBER_OF_RUNNING_THREADS_MIN_VALUE + " inseatd.");
			maxThreads = MAX_NUMBER_OF_RUNNING_THREADS_MIN_VALUE;
		}

		// Negative number means 'unlimited' (technically 2G)
		if (taskMaxHintLen < 0) taskMaxHintLen = Integer.MAX_VALUE;

		// Remove leading and trailing quotes from pidRegex
		if (pidRegex.startsWith("\"") && pidRegex.endsWith("\"") && pidRegex.length() > 2) {
			pidRegex = pidRegex.substring(1, pidRegex.length() - 1);
		}

		// Split and add all items
		filterOutTaskHint = new ArrayList<>();
		for (String foth : getString(FILTER_OUT_TASK_HINT, "").split(" ")) {
			foth = foth.trim();
			if (!foth.isEmpty()) filterOutTaskHint.add(foth);
		}
	}

	/**
	 * Read configuration file
	 */
	private void read(String confFile) {
		configFileName = findConfigFile(confFile);
		properties = new Properties();

		//---
		// Read properties file
		//---
		try {
			properties.load(new FileReader(new File(configFileName)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find config file '" + configFileName + "'");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Config directory
		configDirName = "";
		try {
			File configDir = new File(configFileName).getAbsoluteFile().getParentFile();
			configDirName = configDir.getCanonicalPath();
		} catch (IOException e1) {
			// OK: May be there is no config file.
		}
	}

	/**
	 * Resolve un-serialization
	 */
	private Object readResolve() throws ObjectStreamException {
		configInstance = this;
		return this;
	}

	public void set(String propertyName, String value) {
		properties.setProperty(propertyName, value);
	}

	public void setCoverage(boolean coverage) {
		this.coverage = coverage;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public void setExtractSource(boolean extractSource) {
		this.extractSource = extractSource;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public void setNoCheckpoint(boolean noCheckpoint) {
		this.noCheckpoint = noCheckpoint;
	}

	public void setNoRmOnExit(boolean noRmOnExit) {
		this.noRmOnExit = noRmOnExit;
	}

	public void setPidFile(String pidFile) {
		this.pidFile = pidFile;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public void setReportHtml(boolean reportHtml) {
		this.reportHtml = reportHtml;
	}

	public void setReportYaml(boolean yamlReport) {
		reportYaml = yamlReport;
	}

	public void setShowTaskCode(boolean showTaskCode) {
		this.showTaskCode = showTaskCode;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public void setTailLines(int tailLines) {
		this.tailLines = tailLines;
	}

	public void setTaskFailCount(int taskFailCount) {
		this.taskFailCount = taskFailCount;
	}

	public void setTaskPrelude(String taskPrelude) {
		this.taskPrelude = taskPrelude;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Config file '" + configFileName + "':\n");

		ArrayList<String> keys = new ArrayList<>();
		for (Object key : properties.keySet())
			keys.add(key.toString());

		Collections.sort(keys);

		for (String key : keys)
			sb.append("\t'" + key + "' : '" + properties.get(key) + "'\n");

		return sb.toString();
	}

}
