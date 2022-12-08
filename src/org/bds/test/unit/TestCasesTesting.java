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

    public TestCasesTesting() {
        dir = "test/test_cases/";
    }

    @Test
    public void testTestCases01() {
        runTestCasesPass(dir + "test_case_run_01.bds");
    }

    @Test
    public void testTestCases02() {
        runTestCasesPass(dir + "test_case_run_02.bds");
    }

    @Test
    public void testTestCases03() {
        runTestCasesPass(dir + "test_case_run_03.bds");
    }

    @Test
    public void testTestCases04() {
        runTestCasesPass(dir + "test_case_run_04.bds");
    }

    @Test
    public void testTestCases05() {
        runTestCasesPass(dir + "test_case_run_05.bds");
    }

    @Test
    public void testTestCasesCoverage06() {
        // Check that coverage is correctly computed: 100% coverage
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_06.bds", 0.95);
        checkCoverageRatio(bds, 1.0);
    }

    @Test
    public void testTestCasesCoverage07() {
        // Check that coverage is correctly computed
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_07.bds", 0.66);
        checkCoverageRatio(bds, 4.0 / 6.0);
    }

    @Test
    public void testTestCasesCoverage07_fail() {
        // Check that coverage is correctly computed
        runTestCasesFailCoverage(dir + "test_case_run_07.bds", 0.8);
    }

    @Test
    public void testTestCasesCoverage08() {
        // Check that coverage is correctly computed: Last line of the file not covered
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_08.bds", 0.66);
        checkCoverageRatio(bds, 2.0 / 3.0);
    }

    @Test
    public void testTestCasesCoverage09() {
        // Check that coverage is correctly computed: 'if' statement in one line
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_09.bds", 0.5);
        checkCoverageRatio(bds, 0.5);
    }

    @Test
    public void testTestCasesCoverage10() {
        // Check that coverage is correctly computed: 'while' statements
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_10.bds", 0.77);
        checkCoverageRatio(bds, 7.0 / 9.0);
    }

    @Test
    public void testTestCasesCoverage11() {
        // Check that coverage is correctly computed: for loop
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_11.bds", 0.8);
        checkCoverageRatio(bds, 0.8571428571428571);
    }

    @Test
    public void testTestCasesCoverage12() {
        // Check that coverage is correctly computed: Private function
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_12.bds", 0.99);
        checkCoverageRatio(bds, 1.0);
    }

    @Test
    public void testTestCasesCoverage13() {
        // Check that coverage is correctly computed: Private function
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_13.bds", 0.85);
        checkCoverageRatio(bds, 0.8571428571428571);
    }

    @Test
    public void testTestCasesCoverage14() {
        // Check that coverage is correctly computed: Switch / Case
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_14.bds", 0.58);
        checkCoverageRatio(bds, 0.5833333333333334);
    }

    @Test
    public void testTestCasesCoverage15() {
        // Check that coverage is correctly computed: Ternary operator
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_15.bds", 0.50);
        checkCoverageRatio(bds, 0.5);
    }

    @Test
    public void testTestCasesCoverage16() {
        // Check that coverage is correctly computed: Ternary operator
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_16.bds", 0.99);
        checkCoverageRatio(bds, 1.0);
    }

    @Test
    public void testTestCasesCoverage17() {
        // Check that coverage is correctly computed: include
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_17.bds", 0.75);
        checkCoverageRatio(bds, 46.0 / 60.0);
    }

    @Test
    public void testTestCasesCoverage18() {
        // Check that coverage is correctly computed: include
        var coverageFileName = dir + "test_case_run_18.coverage";
        Bds bds = runTestCasesSaveCoverage(dir + "test_case_run_18_1.bds", coverageFileName, true, 0.0);
        bds = runTestCasesSaveCoverage(dir + "test_case_run_18_2.bds", coverageFileName, false, 0.99);

        Assert.assertEquals(3, bds.getBdsRun().getCoverageCounter().getCountLines());
        Assert.assertEquals(3, bds.getBdsRun().getCoverageCounter().getCountCovered());
    }

    @Test
    public void testTestCasesCoverage19() {
        // Check that coverage is correctly computed: include
        var coverageFileName = dir + "test_case_run_19.coverage";
        Bds bds = runTestCasesSaveCoverage(dir + "test_case_run_19_1.bds", coverageFileName, true, 0.0);
        bds = runTestCasesSaveCoverage(dir + "test_case_run_19_2.bds", coverageFileName, false, 0.99);

        Assert.assertEquals(5, bds.getBdsRun().getCoverageCounter().getCountLines());
        Assert.assertEquals(5, bds.getBdsRun().getCoverageCounter().getCountCovered());
    }

    @Test
    public void testTestCasesCoverage20() {
        // Check that coverage is correctly computed, excluding test case code from the stats.
        // Case: One-liners
        var coverageFileName = dir + "test_case_run_20.coverage";
        Bds bds = runTestCasesSaveCoverage(dir + "test_case_run_20_1.bds", coverageFileName, true, 0.0);
        bds = runTestCasesSaveCoverage(dir + "test_case_run_20_2.bds", coverageFileName, false, 0.99);

        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(2, coverage.getCountLines());
        Assert.assertEquals(2, coverage.getCountCovered());
        Assert.assertEquals(2, coverage.getCountTestCodeLines());
        Assert.assertEquals(2, coverage.getCountTestCoveredLines());
    }

    @Test
    public void testTestCasesCoverage21() {
        // Check that coverage is correctly computed, excluding test case code from the stats
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_21.bds", 0.99);
        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(5, coverage.getCountLines());
        Assert.assertEquals(5, coverage.getCountCovered());
        Assert.assertEquals(17, coverage.getCountTestCodeLines());
        Assert.assertEquals(7, coverage.getCountTestCoveredLines());
    }

    @Test
    public void testTestCasesCoverage22() {
        // Check that coverage is correctly computed, excluding test case code from the stats.
        // Testing class methods
        Bds bds = runTestCasesPassCoverage(dir + "test_case_run_22.bds", 0.99);

        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(6, coverage.getCountLines());
        Assert.assertEquals(6, coverage.getCountCovered());
        Assert.assertEquals(11, coverage.getCountTestCodeLines());
        Assert.assertEquals(10, coverage.getCountTestCoveredLines());
    }

    @Test
    public void testTestCasesCoverage23() {
        // Check that coverage is correctly computed, excluding test case code from the stats.
        // Testing coverage after failed assert
        Bds bds = runTestCasesFailCoverage(dir + "test_case_run_23.bds", 0.5);

        Coverage coverage = bds.getBdsRun().getCoverageCounter();
        Assert.assertEquals(5, coverage.getCountLines());
        Assert.assertEquals(5, coverage.getCountCovered());
        Assert.assertEquals(21, coverage.getCountTestCodeLines());
        Assert.assertEquals(11, coverage.getCountTestCoveredLines());
    }
}
