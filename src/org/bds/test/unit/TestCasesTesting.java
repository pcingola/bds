package org.bds.test.unit;

import org.bds.Bds;
import org.bds.run.Coverage;
import org.bds.test.TestCasesBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for "bds --test"
 * <p>
 * Note: These test cases execute all the "test*()" methods in the scripts
 *
 * @author pcingola
 */
public class TestCasesTesting extends TestCasesBase {

    @Test
    public void testTestCases01() {
        runTestCasesPass("test/test_case_run_01.bds");
    }

    @Test
    public void testTestCases02() {
        runTestCasesPass("test/test_case_run_02.bds");
    }

    @Test
    public void testTestCases03() {
        runTestCasesPass("test/test_case_run_03.bds");
    }

    @Test
    public void testTestCases04() {
        runTestCasesPass("test/test_case_run_04.bds");
    }

    @Test
    public void testTestCases05() {
        runTestCasesPass("test/test_case_run_05.bds");
    }

    @Test
    public void testTestCasesCoverage06() {
        // Check that coverage is correctly computed: 100% coverage
        Bds bds = runTestCasesPassCoverage("test/test_case_run_06.bds", 0.95);
        checkCoverageRatio(bds, 1.0);
    }

    @Test
    public void testTestCasesCoverage07() {
        // Check that coverage is correctly computed
        Bds bds = runTestCasesPassCoverage("test/test_case_run_07.bds", 0.77);
        checkCoverageRatio(bds, 7.0 / 9.0);
    }

    @Test
    public void testTestCasesCoverage07_fail() {
        // Check that coverage is correctly computed
        runTestCasesFailCoverage("test/test_case_run_07.bds", 0.8);
    }

    @Test
    public void testTestCasesCoverage08() {
        // Check that coverage is correctly computed: Last line of the file not covered
        Bds bds = runTestCasesPassCoverage("test/test_case_run_08.bds", 0.8);
        checkCoverageRatio(bds, 0.8);
    }

    @Test
    public void testTestCasesCoverage09() {
        // Check that coverage is correctly computed: 'if' statement in one line
        Bds bds = runTestCasesPassCoverage("test/test_case_run_09.bds", 0.7);
        checkCoverageRatio(bds, 0.75);
    }

    @Test
    public void testTestCasesCoverage10() {
        // Check that coverage is correctly computed: 'while' statements
        Bds bds = runTestCasesPassCoverage("test/test_case_run_10.bds", 0.8);
        checkCoverageRatio(bds, 9.0 / 11.0);
    }

    @Test
    public void testTestCasesCoverage11() {
        // Check that coverage is correctly computed: for loop
        Bds bds = runTestCasesPassCoverage("test/test_case_run_11.bds", 0.8);
        checkCoverageRatio(bds, 8.0 / 9.0);
    }

    @Test
    public void testTestCasesCoverage12() {
        // Check that coverage is correctly computed: Private function
        Bds bds = runTestCasesPassCoverage("test/test_case_run_12.bds", 0.99);
        checkCoverageRatio(bds, 1.0);
    }

    @Test
    public void testTestCasesCoverage13() {
        // Check that coverage is correctly computed: Private function
        Bds bds = runTestCasesPassCoverage("test/test_case_run_13.bds", 0.88);
        checkCoverageRatio(bds, 8.0 / 9.0);
    }

    @Test
    public void testTestCasesCoverage14() {
        // Check that coverage is correctly computed: Switch / Case
        Bds bds = runTestCasesPassCoverage("test/test_case_run_14.bds", 0.64);
        checkCoverageRatio(bds, 9.0 / 14.0);
    }

    @Test
    public void testTestCasesCoverage15() {
        // Check that coverage is correctly computed: Ternary operator
        Bds bds = runTestCasesPassCoverage("test/test_case_run_15.bds", 0.75);
        checkCoverageRatio(bds, 0.75);
    }

    @Test
    public void testTestCasesCoverage16() {
        // Check that coverage is correctly computed: Ternary operator
        Bds bds = runTestCasesPassCoverage("test/test_case_run_16.bds", 0.99);
        checkCoverageRatio(bds, 1.0);
    }

    @Test
    public void testTestCasesCoverage17() {
        // Check that coverage is correctly computed: include
        Bds bds = runTestCasesPassCoverage("test/test_case_run_17.bds", 0.8);
        checkCoverageRatio(bds, 70.0 / 84.0);
    }

    @Test
    public void testTestCasesCoverage18() {
        // Check that coverage is correctly computed: include
        var coverageFileName = "test/test_case_run_18.coverage";
        Bds bds = runTestCasesSaveCoverage("test/test_case_run_18_1.bds", coverageFileName, true, 0.0);
        bds = runTestCasesSaveCoverage("test/test_case_run_18_2.bds", coverageFileName, false, 0.99);

        Assert.assertEquals(9, bds.getBdsRun().getCoverageCounter().getCountLines());
        Assert.assertEquals(9, bds.getBdsRun().getCoverageCounter().getCountCovered());
    }

    @Test
    public void testTestCasesCoverage19() {
        // Check that coverage is correctly computed: include
        var coverageFileName = "test/test_case_run_19.coverage";
        Bds bds = runTestCasesSaveCoverage("test/test_case_run_19_1.bds", coverageFileName, true, 0.0);
        bds = runTestCasesSaveCoverage("test/test_case_run_19_2.bds", coverageFileName, false, 0.99);

        Assert.assertEquals(11, bds.getBdsRun().getCoverageCounter().getCountLines());
        Assert.assertEquals(11, bds.getBdsRun().getCoverageCounter().getCountCovered());
    }

    @Test
    public void testTestCasesCoverage20() {
        // Test inline functions
        verbose = true;
        Bds bds = runTestCasesPassCoverage("test/test_case_run_20.bds", 0.99);
    }

    @Test
    public void testTestCasesCoverage21() {
        // Check that coverage is correctly computed, excluding test case code from the stats
        Bds bds = runTestCasesPassCoverage("test/test_case_run_21.bds", 0.99);
        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(5, coverage.getCountLines());
        Assert.assertEquals(5, coverage.getCountCovered());
        Assert.assertEquals(17, coverage.getCountTestCodeLines());
        Assert.assertEquals(7, coverage.getCountTestCoveredLines());
    }

    public void testTestCasesCoverage22() {
        // TODO: Test with bad assert (should not count all lines in the test code)
        Assert.assertTrue("IMPLEMENT THIS CODE!!!", false);
    }

}
