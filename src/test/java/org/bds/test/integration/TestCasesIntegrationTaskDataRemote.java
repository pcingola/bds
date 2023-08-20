package org.bds.test.integration;

import org.bds.Config;
import org.bds.data.DataRemote;
import org.bds.run.BdsRun;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for local tasks with remote files
 *
 * @author pcingola
 */
public class TestCasesIntegrationTaskDataRemote extends TestCasesBase {

    public TestCasesIntegrationTaskDataRemote() {
        dir = "test/integration/task_data_remote/";
    }

    @Before
    public void beforeEachTest() {
        BdsRun.reset();
        //		Config.reset();
        Config.get().load();

        // Delete 'tmp' download dir
        String tmpDownloadDir = Config.get().getTmpDir() + "/" + DataRemote.TMP_BDS_DATA;
        try {
            Files.walk(Paths.get(tmpDownloadDir)) //
                    .map(Path::toFile) //
                    .sorted(Comparator.reverseOrder()) //
                    .forEach(File::delete);
        } catch (NoSuchFileException e) {
            // OK
        } catch (IOException e) {
            throw new RuntimeException("Error deleting tmp directory '" + tmpDownloadDir + "'", e);
        }
    }

    /**
     * Read parameters from '$BDS_HOME/aws_test.config' file
     *
     * @return A map with key = value parsed from the file
     */
    Map<String, String> readAwsTestConfig() {
        Map<String, String> awsTest = new HashMap<>();
        Config config = new Config();
        String awsTestConfig = config.getBdsHome() + "/aws_test.config";
        for (String line : Gpr.readFile(awsTestConfig).split("\n")) {
            if (line.indexOf('\t') > 0) {
                String[] fields = line.split("\t", 2);
                awsTest.put(fields[0], fields[1]);
            }
        }
        return awsTest;
    }

    String getCurrPath() {
        try {
            return (new File(".")).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test executing a local task with a remote input dependency
     */
    @Test
    public void test03_TaskUrlIn() {
        runAndCheck(dir + "remote_03.bds", "first", "<!DOCTYPE html>");
    }

    /**
     * Test executing a local task with a remote dependency
     * Replacement of task's `sys` command within double quotes
     */
    @Test
    public void test04_TaskUrlInDoubleQuotes() {
        runAndCheck(dir + "remote_04.bds", "first", "<!DOCTYPE html>");
    }

    /**
     * Test executing a local task with a remote dependency
     * Replacement of task's `sys` command within single quotes
     */
    @Test
    public void test05_TaskUrlInSingleQuotes() {
        runAndCheck(dir + "remote_05.bds", "first", "<!DOCTYPE html>");
    }

    /**
     * Test executing a task with multiple input remote dependencies
     * in a list literal
     */
    @Test
    public void test06_TaskUrlRemoteListLiteral() {
        runAndCheck(dir + "remote_06.bds", "first", "<!DOCTYPE html>");
    }

    /**
     * Test executing a task with multiple remote dependencies in a list
     */
    @Test
    public void test07_TaskUrlRemoteList() {
        runAndCheck(dir + "remote_07.bds", "first", "<!DOCTYPE html>");
    }

    /**
     * Test executing a task with multiple remote dependencies in a list
     * Replacement of task's `sys` command with list variables
     */
    @Test
    public void test08_TaskUrlRemoteListReplace() {
        runAndCheck(dir + "remote_08.bds", "first", "<!DOCTYPE html>");
    }

    /**
     * Test executing a task with a remote dependency
     * Replacement of task's `sys` command with literal references
     */
    @Test
    public void test09_TaskUrlRemoteListReplaceLiterals() {
        runAndCheck(dir + "remote_09.bds", "first", "<!DOCTYPE html>");
    }

    /**
     * Task input in s3, output local file
     */
    @Test
    public void test34_TaskInS3OutLocal() {
        verbose = true;
        runAndCheck(dir + "remote_34.bds", "outStr", "OK");
    }

    /**
     * Task input local, output s3 file
     */
    @Test
    public void test35_TaskInsLocalOutS3() {
        runAndCheck(dir + "remote_35.bds", "outStr", "IN: 'remote_35'");
    }

    /**
     * Task input from S3, output to S3
     */
    @Test
    public void test36_TaskInS3OutS3() {
        runAndCheck(dir + "remote_36.bds", "outStr", "IN: 'remote_36'");
    }

    /**
     * Task input from S3, output to S3
     */
    @Test
    public void test37_TaskStrictRemoteDependencies() {
        String bucket = readAwsTestConfig().get("bucket");
        String inS3Path = "s3://" + bucket + "/tmp/bds/remote_37/in.txt";
        runAndCheckStdout(dir + "remote_37.bds", inS3Path);
    }

}
