package org.bds.test.integration;

import org.junit.Assert;
import org.bds.task.Task;
import org.bds.test.BdsTest;
import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Cluster "ssh" is a "cluster" formed by nodes to which we have password-less ssh access.
 * These are test cases for SSH cluster
 *
 * @author pcingola
 */
public class TestCasesIntegrationClusterSsh extends TestCasesBase {

    public TestCasesIntegrationClusterSsh() {
        dir = "test/integration/cluter_ssh/";
    }

    /**
     * Test a simple command running via 'ssh cluster'
     */
    @Test
    public void test01() {
        // Create command line
        verbose = true;
        // debug = true;
        String[] args = {"-c", dir + "clusterSsh_localhost_01.config"};
        BdsTest bdsTest = new BdsTest(dir + "clusterSsh_01.bds", args, verbose, debug);
        bdsTest.bds(false);

        // Run script
        bdsTest.run();
        bdsTest.checkRunOk(); // Finished OK?

        // Get tasks and check that PID matches 'CLUSTERSSH'
        for (Task t : bdsTest.bds.getBdsRun().getBdsThread().getTasks()) {
            debug("Task " + t.getId() + ", pid " + t.getPid());
            Assert.assertTrue("Task " + t.getId() + " was NOT executed by 'Cluster Ssh', task id " + t.getId() //
                    , t.getId().toUpperCase().startsWith("CLUSTERSSH") //
            );
        }
    }
}
