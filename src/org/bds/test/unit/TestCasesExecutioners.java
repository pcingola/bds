package org.bds.test.unit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bds.Config;
import org.bds.cluster.host.TaskResourcesCluster;
import org.bds.executioner.CheckTasksRunningCmd;
import org.bds.executioner.Executioner;
import org.bds.executioner.ExecutionerClusterSlurm;
import org.bds.executioner.Executioners;
import org.bds.executioner.Executioners.ExecutionerType;
import org.bds.lang.type.Types;
import org.bds.lang.value.ValueList;
import org.bds.lang.value.ValueMap;
import org.bds.lang.value.ValueString;
import org.bds.osCmd.Cmd;
import org.bds.task.Task;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Quick test cases when creating a new feature...
 *
 * @author pcingola
 *
 */
public class TestCasesExecutioners extends TestCasesBase {

	@Test
	public void test01_parsePidQstatRegex() {
		Gpr.debug("Test");

		// Create 'CheckTasksRunning'
		Config config = new Config("test/test_parsePidQstatRegex_qstat.config"); // We set here the 'PID_REGEX_CHECK_TASK_RUNNING' parameter
		config.setDebug(debug);
		config.setVerbose(verbose);
		config.load();

		Executioner ex = Executioners.getInstance(config).get(ExecutionerType.LOCAL);
		CheckTasksRunningCmd ctr = new CheckTasksRunningCmd(config, ex);

		// Parse 'qstat' lines
		String fileName = "test/test_parsePidQstatRegex_qstat.txt";
		if (verbose) System.out.println("Reading file '" + fileName + "'");
		String file = Gpr.readFile(fileName);
		String lines[] = file.split("\n");
		Set<String> pids = ctr.parseCommandOutput(lines);

		// Check that all IDs are there
		for (int i = 1; i < 10; i++) {
			String pid = "jobId_10" + i;
			if (verbose) System.out.println("PID: '" + pid + "'\t" + pids.contains(pid));
			Assert.assertTrue("PID not found: '" + pid + "'", pids.contains(pid));
		}

		// Finished
		if (verbose) System.out.println("Killing executioners");
		ex.kill();
		if (verbose) System.out.println("Done");
	}

	@Test
	public void test02_parsePidQstatColumn() {
		Gpr.debug("Test");

		// Create 'CheckTasksRunning'
		Config config = new Config();
		config.setDebug(debug);
		config.setVerbose(verbose);
		config.load();

		Executioner ex = Executioners.getInstance(config).get(ExecutionerType.LOCAL);
		CheckTasksRunningCmd ctr = new CheckTasksRunningCmd(config, ex);

		// Parse 'qstat' lines
		String fileName = "test/test_parsePidQstatColumn_qstat.txt";
		if (verbose) System.out.println("Reading file '" + fileName + "'");
		String file = Gpr.readFile(fileName);
		String lines[] = file.split("\n");
		Set<String> pids = ctr.parseCommandOutput(lines);

		// Check that all IDs are there
		for (int i = 1; i < 10; i++) {
			String pid = "" + i;
			if (verbose) System.out.println("PID: '" + pid + "'\t" + pids.contains(pid));
			Assert.assertTrue("PID not found: '" + pid + "'", pids.contains(pid));
		}

		// Finished
		if (verbose) System.out.println("Killing executioners");
		ex.kill();
		if (verbose) System.out.println("Done");
	}

	@Test
	public void test03_clusterRunCmdOptionsFromTaskResourcesString() throws IOException {
		Gpr.debug("Test");

		String baseName = "test03_clusterRunCmdOptionsFromTaskResourcesString";
		String scriptName = baseName + ".sh";
		String scriptNameCluster = baseName + ".cluster.sh";
		String stdout = baseName + ".stdout.cluster";
		String stderr = baseName + ".stderr.cluster";

		Config config = new Config();
		config.setDebug(debug);
		config.setVerbose(verbose);
		config.load();

		ValueString val = new ValueString("--zzz=123");

		// Create executioner, task, resources, cmd
		ExecutionerClusterSlurm ex = new ExecutionerClusterSlurm(config);
		Task task = new Task("task_123", null, scriptName, "# Test case: No program txt");
		TaskResourcesCluster tr = new TaskResourcesCluster();
		tr.setRunCmdOptions(val, debug);
		task.setResources(tr);
		Cmd cmd = ex.createRunCmd(task);

		// Expected command
		String expected = "CmdCluster: sbatch " //
				+ "--parsable " //
				+ "--no-requeue " //
				+ "--cpus-per-task 1 " //
				+ "--zzz=123 " //
				+ "--output " + stdout + " " //
				+ "--error " + stderr + " " //
				+ (new File(scriptNameCluster)).getCanonicalPath() //
		;

		assertEquals(expected, cmd.toString().trim());
	}

	@Test
	public void test04_clusterRunCmdOptionsFromTaskResourcesList() throws IOException {
		Gpr.debug("Test");

		String baseName = "test04_clusterRunCmdOptionsFromTaskResourcesString";
		String scriptName = baseName + ".sh";
		String scriptNameCluster = baseName + ".cluster.sh";
		String stdout = baseName + ".stdout.cluster";
		String stderr = baseName + ".stderr.cluster";

		Config config = new Config();
		config.setDebug(debug);
		config.setVerbose(verbose);
		config.load();

		// Create a list
		ValueString val1 = new ValueString("--zzz=123");
		ValueString val2 = new ValueString("--yyy=456");
		ValueList val = new ValueList(Types.STRING);
		val.add(val1);
		val.add(val2);

		// Create executioner, task, resources, cmd
		ExecutionerClusterSlurm ex = new ExecutionerClusterSlurm(config);
		Task task = new Task("task_123", null, scriptName, "# Test case: No program txt");
		TaskResourcesCluster tr = new TaskResourcesCluster();
		tr.setRunCmdOptions(val, debug);
		task.setResources(tr);
		Cmd cmd = ex.createRunCmd(task);

		// Expected command
		String expected = "CmdCluster: sbatch " //
				+ "--parsable " //
				+ "--no-requeue " //
				+ "--cpus-per-task 1 " //
				+ "--zzz=123 " //
				+ "--yyy=456 " //
				+ "--output " + stdout + " " //
				+ "--error " + stderr + " " //
				+ (new File(scriptNameCluster)).getCanonicalPath() //
		;

		assertEquals(expected, cmd.toString().trim());
	}

	@Test
	public void test05_clusterRunCmdOptionsFromTaskResourcesMap() throws IOException {
		Gpr.debug("Test");

		String baseName = "test05_clusterRunCmdOptionsFromTaskResourcesMap";
		String scriptName = baseName + ".sh";
		String scriptNameCluster = baseName + ".cluster.sh";
		String stdout = baseName + ".stdout.cluster";
		String stderr = baseName + ".stderr.cluster";

		Config config = new Config();
		config.setDebug(debug);
		config.setVerbose(verbose);
		config.load();

		// Create a map
		ValueString key1 = new ValueString("zzz");
		ValueString val1 = new ValueString("123");
		ValueString key2 = new ValueString("yyy");
		ValueString val2 = new ValueString("456");
		ValueMap val = new ValueMap(Types.STRING);
		val.put(key1, val1);
		val.put(key2, val2);

		// Create executioner, task, resources, cmd
		ExecutionerClusterSlurm ex = new ExecutionerClusterSlurm(config);
		Task task = new Task("task_123", null, scriptName, "# Test case: No program txt");
		TaskResourcesCluster tr = new TaskResourcesCluster();
		tr.setRunCmdOptions(val, debug);
		task.setResources(tr);
		Cmd cmd = ex.createRunCmd(task);

		// Expected command
		String expected = "CmdCluster: sbatch " //
				+ "--parsable " //
				+ "--no-requeue " //
				+ "--cpus-per-task 1 " //
				+ "--yyy=456 " //
				+ "--zzz=123 " //
				+ "--output " + stdout + " " //
				+ "--error " + stderr + " " //
				+ (new File(scriptNameCluster)).getCanonicalPath() //
		;

		assertEquals(expected, cmd.toString().trim());
	}

}
