package org.bds.test.integration;

import org.junit.Assert;
import org.bds.task.Task;
import org.bds.test.BdsTest;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

/**
 * Executoner "Cluster Generic".
 * This test the cluster generic using a simple "mock" cluster scripts
 *
 * @author pcingola
 */
public class TestCasesIntegrationClusterGeneric extends TestCasesBase {

    public TestCasesIntegrationClusterGeneric() {
        dir = "test/integration/cluter_generic/";
    }

    @Test
    public void test01_log_TestCasesClusterGeneric() {
        // Create command line
        String[] args = {"-c", dir + "clusterGeneric_localhost_01.config"};
        BdsTest bdsTest = new BdsTest(dir + "clusterGeneric_01.bds", args, verbose, debug);
        bdsTest.bds(false);

        // Run script
        bdsTest.run();
        bdsTest.checkRunOk(); // Finished OK?

        // Get tasks and check that PID matches 'CLUSTERGENERIC_LOCALHOST_'
        // (run.pl prepends that string to PID)
        for (Task t : bdsTest.bds.getBdsRun().getBdsThread().getTasks()) {
            if (debug) Gpr.debug("Task " + t.getId() + ", pid " + t.getPid());
            Assert.assertTrue("Task " + t.getId() + " was NOT executed by ClusterGeneric_localhos (pid " + t.getPid() + ")", t.getPid().startsWith("CLUSTERGENERIC_LOCALHOST_"));
        }
    }
}
