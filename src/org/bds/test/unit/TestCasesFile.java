package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueList;
import org.bds.test.TestCasesBase;
import org.junit.Test;
import java.io.File;

/**
 * Test cases for file methods
 *
 * @author pcingola
 */
public class TestCasesFile extends TestCasesBase {

    public TestCasesFile() {
        dir = "test/unit/file/";
    }

    @Test
    public void test21() {
        // File readLines
        runAndCheck(dir + "run_21.bds", "l1", "line 1\nline 2\nline 3\n");
    }

    @Test
    public void test21_2() {
        // File: readLines
        runAndCheck(dir + "run_21.bds", "l2", "line 2");
    }

    @Test
    public void test22() {
        // Files: Dir
        runAndCheck(dir + "run_22.bds", "l2", "file_3.txt");
    }

    @Test
    public void test69() {
        // File: removeExt
        runAndCheck(dir + "run_69.bds", "bgz", "path/to/file.txt");
    }

    @Test
    public void test69b() {
        // File: removeExt
        runAndCheck(dir + "run_69.bds", "btxt", "path/to/file");
    }

    @Test
    public void test131ChdirFileMethods() {
        // File functions: read(), readLines(), etc.
        String out = ""//
                + "chdir_test_file_01.txt\tread:FILE_01\n" //
                + "chdir_test_file_01.txt\treadLines:[FILE_01]\n" //
                + "chdir_test_file_01.txt\texists:true\n" //
                + "chdir_test_file_01.txt\tisDir:false\n" //
                + "chdir_test_file_01.txt\tisEmpty:false\n" //
                + "chdir_test_file_01.txt\tisFile:true\n" //
                + "chdir_test_file_01.txt\tcanRead:true\n" //
                + "chdir_test_file_01.txt\tcanWrite:true\n" //
                + "\n" //
                + "----------\n" //
                + "chdir_test_file_02.txt\tread:FILE_02\n" //
                + "chdir_test_file_02.txt\treadLines:[FILE_02]\n" //
                + "chdir_test_file_02.txt\texists:true\n" //
                + "chdir_test_file_02.txt\tisDir:false\n" //
                + "chdir_test_file_02.txt\tisEmpty:false\n" //
                + "chdir_test_file_02.txt\tisFile:true\n" //
                + "chdir_test_file_02.txt\tcanRead:true\n" //
                + "chdir_test_file_02.txt\tcanWrite:true\n" //
                ;

        String outreal = runAndReturnStdout(dir + "run_131.bds");
        Assert.assertEquals(out, outreal);
    }

    @Test
    public void test142DirPath() {
        // File: dir, dirPath
        ValueList dir2 = (ValueList) runAndGet(dir + "run_142.bds", "dir2");

        Assert.assertEquals(10, dir2.size());

        for (Value v : dir2) {
            String f = v.toString();
            debug(f);
            Assert.assertTrue("Path must be canonical", f.startsWith("/"));
            Assert.assertTrue("Path must be canonical", f.endsWith(".txt"));
        }
    }

    @Test
    public void test143PathAbsolute() {
        // File: baseName
        runAndCheck(dir + "run_143.bds", "fileBase", "tmp_run_143_link.txt");
    }

    @Test
    public void test269RmOnExit() {
        // File: rmOnExit()
        var tmpFile = new File("/tmp/run_269.tmp");
        var txtFile = new File("/tmp/run_269.txt");
        runOk(dir + "run_269.bds");
        org.junit.Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
        org.junit.Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test270RmOnExitError() {
        // File: rmOnExit() on error statement
        var tmpFile = new File("/tmp/run_270.tmp");
        var txtFile = new File("/tmp/run_270.txt");
        runAndCheckExit(dir + "run_270.bds", 1);
        org.junit.Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
        org.junit.Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test271RmOnExitDirWithFiles() {
        // File: rmOnExit() for directories
        runOk(dir + "run_271.bds");

        var tmpDir = new File("/tmp/run_271/rm_on_exit");
        var txtDir = new File("/tmp/run_271/no_rm_on_exit");

        org.junit.Assert.assertFalse("Tmp dir " + tmpDir + " should not exists", tmpDir.exists());

        org.junit.Assert.assertTrue("Tmp dir " + txtDir + " should exists", txtDir.exists());
        org.junit.Assert.assertTrue("Tmp dir " + txtDir + " should be a directory", txtDir.isDirectory());

        for (int i = 0; i < 10; i++) {
            var tmpFile = new File("/tmp/run_271/rm_on_exit/" + i + ".tmp");
            var txtFile = new File("/tmp/run_271/no_rm_on_exit/" + i + ".txt");
            org.junit.Assert.assertFalse("Tmp file " + tmpFile + " should have been deleted", tmpFile.exists());
            org.junit.Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
        }
    }

    @Test
    public void test272RmOnExitCancel() {
        // File: rmOnExitCancel()
        var tmpFile = new File("/tmp/run_272.tmp");
        var txtFile = new File("/tmp/run_272.txt");
        runOk(dir + "run_272.bds");
        org.junit.Assert.assertTrue("Tmp file " + tmpFile + " should exist", tmpFile.exists());
        org.junit.Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test273RmOnExitCancelError() {
        // File: rmOnExitCancel()
        var tmpFile = new File("/tmp/run_273.tmp");
        var txtFile = new File("/tmp/run_273.txt");
        runAndCheckExit(dir + "run_273.bds", 1);
        org.junit.Assert.assertTrue("Tmp file " + tmpFile + " should exist", tmpFile.exists());
        org.junit.Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test274RmOnExitCancelList() {
        // File: rmOnExitCancel() with a list of files
        var tmpFile1 = new File("/tmp/run_274.1.tmp");
        var tmpFile2 = new File("/tmp/run_274.2.tmp");
        var tmpFile3 = new File("/tmp/run_274.3.tmp");
        var txtFile = new File("/tmp/run_274.txt");
        runOk(dir + "run_274.bds");
        org.junit.Assert.assertTrue("Tmp file " + tmpFile1 + " should exist", tmpFile1.exists());
        org.junit.Assert.assertTrue("Tmp file " + tmpFile2 + " should exist", tmpFile2.exists());
        org.junit.Assert.assertTrue("Tmp file " + tmpFile3 + " should exist", tmpFile3.exists());
        org.junit.Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    @Test
    public void test275RmOnExitCancelListError() {
        // File: rmOnExitCancel() with a list of files, error statement forcing exit
        var tmpFile1 = new File("/tmp/run_275.1.tmp");
        var tmpFile2 = new File("/tmp/run_275.2.tmp");
        var tmpFile3 = new File("/tmp/run_275.3.tmp");
        var txtFile = new File("/tmp/run_275.txt");
        runAndCheckExit(dir + "run_275.bds", 1);
        org.junit.Assert.assertTrue("Tmp file " + tmpFile1 + " should exist", tmpFile1.exists());
        org.junit.Assert.assertTrue("Tmp file " + tmpFile2 + " should exist", tmpFile2.exists());
        org.junit.Assert.assertTrue("Tmp file " + tmpFile3 + " should exist", tmpFile3.exists());
        org.junit.Assert.assertTrue("Tmp file " + txtFile + " should exists", txtFile.exists());
    }

    /**
     * Test parsing basename of a remote AWS S3 file (DataS3) using "virtual hosting" style URL
     */
    @Test
    public void test_s3VirtualHostBasename() {
        runAndCheck(dir + "test_s3VirtualHostBasename.bds", "b", "dbNSFP4.4c.zip");
    }

}
