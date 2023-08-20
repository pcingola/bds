package org.bds.test.unit;

import org.junit.Assert;
import org.bds.test.TestCasesBase;
import org.junit.Test;

/**
 * Test cases for reporting
 *
 * @author pcingola
 */
public class TestCasesReport extends TestCasesBase {

    public TestCasesReport() {
        dir = "test/unit/report/";
    }

    @Test
    public void test01_report() {
        String report = runAndGetReport(dir + "report_01.bds", true);
        Assert.assertTrue("Yaml report doesn't have the expected 'tasksExecuted' entry", report.indexOf("tasksExecuted: 1") > 0);
        Assert.assertTrue("Yaml report doesn't have the expected 'tasksFailed' entry", report.indexOf("tasksFailed: 0") > 0);
    }

}
