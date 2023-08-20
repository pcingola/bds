package org.bds.executioner;

import org.bds.Config;

/**
 * Fake Cluster: This is a small computer simulating to be a cluster. It is
 * used for debugging and development
 *
 * @author pcingola
 */
public class ExecutionerClusterFake extends ExecutionerCluster {

	public static final String FAKE_CLUSTER_DIR_NAME = "fakeCluster";

	String fakeClusterDir;

	protected ExecutionerClusterFake(Config config) {
		super(config);

		fakeClusterDir = config.getFakeClusterDir();

		// Define commands
		String[] runCommand = { fakeClusterDir + "/qsub" };
		String[] killCommand = { fakeClusterDir + "/qdel" };
		String[] statCommand = { fakeClusterDir + "/qstat" };
		String[] postMortemInfoCommand = { fakeClusterDir + "/qstat", "-f" };

		clusterRunCommand = runCommand;
		clusterKillCommand = killCommand;
		clusterStatCommand = statCommand;
		clusterPostMortemInfoCommand = postMortemInfoCommand;
	}

}
