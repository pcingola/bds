package org.bds.test;

import org.bds.test.integration.TestCasesIntegrationRunAws;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * IMPORTANT: These cases that use AWS instances!
 *
 * @author pcingola
 */

@RunWith(Suite.class)
@SuiteClasses({ //
		TestCasesIntegrationRunAws.class, // Executioner AWS, task dependencies and detached AWS tasks
})
public class TestSuiteIntegrationRunAws {

}
