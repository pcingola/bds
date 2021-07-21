package org.bds.test;

import org.bds.test.unit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Invoke all test cases
 *
 * @author pcingola
 */

@RunWith(Suite.class)
@SuiteClasses({ TestCasesTail.class, //
		TestCasesVm.class, // Virtual machine
		TestCasesLang.class, // Language (compiler)
		TestCasesInterpolate.class, // Variable interpolation
		TestCasesExecutioners.class, // Task executioners
		TestCasesFunctionDeclaration.class, // Function declaration
		TestCasesRun.class, // Running bds code
		TestCasesRun2.class, // Running bds code
		TestCasesRun3.class, // Running bds code (classes / object)
		TestCasesJson.class, // JSON parsing
		TestCasesTesting.class, // Check bds unit testing system
		TestCasesCommandLineOptions.class, // Check command line options
		TestCasesRemote.class, // Accessing remote data (cloud storage)
		TestCasesReport.class, // Report generation
})
public class TestSuiteUnit {

}
