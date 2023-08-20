package org.bds.test;

import org.bds.test.integration.TestCasesIntegrationCheckpoint;
import org.bds.test.integration.TestCasesIntegrationCheckpointAws;
import org.bds.test.integration.TestCasesIntegrationClusterGeneric;
import org.bds.test.integration.TestCasesIntegrationClusterSsh;
import org.bds.test.integration.TestCasesIntegrationDataRemote;
import org.bds.test.integration.TestCasesIntegrationDataRemoteS3;
import org.bds.test.integration.TestCasesIntegrationGraph;
import org.bds.test.integration.TestCasesIntegrationRun;
import org.bds.test.integration.TestCasesIntegrationTaskDataRemote;
import org.bds.test.integration.TestCasesIntegrationTaskDetached;
import org.bds.test.integration.TestCasesIntegrationTaskImproper;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Invoke all test cases
 *
 * WARNING: Some of these test cases read/write S3 files (but they do not create instances)
 *
 * @author pcingola
 */

@RunWith(Suite.class)
@SuiteClasses({ //
		TestCasesIntegrationRun.class, // Running bds code (local, long running tests)
		TestCasesIntegrationCheckpoint.class, // Running bds code: Checkpoint and recovery (local, long running tests)
		TestCasesIntegrationGraph.class, // Running bds code: Task graphs and dependencies (local, long running tests)
		TestCasesIntegrationClusterGeneric.class, // Executoner Cluster Generic
		TestCasesIntegrationClusterSsh.class, // Executioner Ssh
		TestCasesIntegrationTaskDetached.class, // Detached tasks on local computer
		TestCasesIntegrationTaskImproper.class, // Improper tasks (local, long running tests)
		// The following tests require internet connectivity AND an S3 bucket access
		TestCasesIntegrationDataRemote.class, // Remote files: HTTP, FTP
		TestCasesIntegrationDataRemoteS3.class, //  Remote files: S3
		TestCasesIntegrationTaskDataRemote.class, // Tasks with remote dependencies
		TestCasesIntegrationCheckpointAws.class, // Running bds code: Checkpoint and recovery from S3
})
public class TestSuiteIntegration {

}
