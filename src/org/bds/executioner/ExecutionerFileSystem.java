package org.bds.executioner;

import org.bds.Config;
import org.bds.cluster.ComputerSystem;
import org.bds.cluster.host.HostLocal;
import org.bds.task.Tail;
import org.bds.task.Task;

/**
 * An ExecutionerFileSystem is an abstract system that executes Tasks.
 *
 * In this type of executioner, there is a common/shared file system.
 * This means that processes can write the results, STDOUT,
 * STDERR and exit status to files and the executioner would be able to
 * access that information immediately by just looking in the same path.
 *
 * Examples of these systems are:
 * 	- A single somputer / server: Obviously in a single computer all the
 *    processes can read / write to the (shared) disk
 *
 *  - A cluster: In a typical cluster, all nodes write to a shared file
 *    system
 *
 *  - Computer in a campus, with a shared file system
 *
 *
 * @author pcingola
 */
public abstract class ExecutionerFileSystem extends Executioner {

	protected int hostIdx = 0;
	protected Tail tail; // Perform a "tail -f" of STDOUT/STDERR files from all tasks, i.e. read the latest lines and show them on the local console

	public ExecutionerFileSystem(Config config) {
		super(config);
		tail = getTail();

		// Create a cluster having only one host (this computer)
		system = new ComputerSystem();
		new HostLocal(system); // Host is added to the system in constructor
	}

	@Override
	protected void checkFinishedTasks() {
		if (monitorTask != null) monitorTask.check();
	}

	/**
	 * Start following a running task (e.g. tail STDOUT & STDERR)
	 */
	@Override
	protected synchronized void follow(Task task) {
		if (taskLogger != null) taskLogger.add(task, this); // Log PID (if any)

		tail.add(task.getStdoutFile(), false);
		tail.add(task.getStderrFile(), true);

		if (monitorTask != null) monitorTask.add(this, task); // Start monitoring exit file
	}

	/**
	 * Stop following a running task (e.g. tail STDOUT & STDERR)
	 */
	@Override
	protected synchronized void followStop(Task task) {
		tail.remove(task.getStdoutFile());
		tail.remove(task.getStderrFile());

		// Remove from loggers
		if (taskLogger != null) taskLogger.remove(task);
		if (monitorTask != null) monitorTask.remove(task);
	}

	public synchronized Tail getTail() {
		if (tail == null) {
			tail = new Tail();
			tail.setDebug(isDebug());
			tail.setVerbose(isVerbose());
			tail.setQuiet(config.isQuiet());
			tail.start(); // Create a 'tail' process (to show STDOUT & STDERR from all processes)
		}
		return tail;
	}

	@Override
	public synchronized void kill() {
		super.kill();
		if (tail != null) {
			tail.kill(); // Kill tail process
			tail = null;
		}
	}

	/**
	 * Try to find some 'post-mortem' info about this
	 * task, in order to asses systematic errors.
	 */
	@Override
	protected void postMortemInfo(Task task) {
		// Nothing to do
	}
}
