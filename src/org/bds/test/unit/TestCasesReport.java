package org.bds.test.unit;

import junit.framework.Assert;
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

    @Test
    public void test02_report_should_escape_html_chars_in_html_report() {
        String report = runAndGetReport(dir + "report_02.bds", false);
        Assert.assertTrue("HTML report doesn't escape html chars", report.indexOf("echo '<html>'") < 0);
        Assert.assertTrue("HTML report doesn't escape html chars", report.indexOf("echo '&lt;html&gt;'") > 0);
    }

    @Test
    public void test02_report_should_not_escape_html_chars_in_yaml_report() {
        String report = runAndGetReport(dir + "report_02.bds", true);
        Assert.assertTrue("YAML report does escape html chars", report.indexOf("echo '<html>'") > 0);
    }

}
