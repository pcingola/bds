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
@SuiteClasses({ //
        TestCasesTail.class, //
        TestCasesVm.class, // Virtual machine
        TestCasesLang.class, // Language (compiler)
        TestCasesInterpolate.class, // Variable interpolation
        TestCasesExecutioners.class, // Task executioners
        TestCasesFunctionDeclaration.class, // Function declaration
        //
        TestCasesClass.class, // Class definition, fields, methods, etc.
        TestCasesFile.class, // File (native) functions
        TestCasesFunctions.class, // Function definitions
        TestCasesFunctionsMethodsNative.class, // Native functions
        TestCasesList.class, // Lists
        TestCasesLoopsIf.class, // Loops ('for', 'while') and 'if'
        TestCasesMap.class, // Maps
        TestCasesOperatorsAndMath.class, // Operators and math
        TestCasesSwitch.class, // Swtch statements
        TestCasesSys.class, // 'sys' expressions
        TestCasesTask.class, // 'task' expressions, task dependencies, etc.
        TestCasesTryCatchFinally.class, // Try, catch, finally statements
        TestCasesVariable.class, // Variables, initializations, casting, etc.
        TestCasesCustomResources.class, // Custom resource allocation in tasks (e.g. GPU, FFPGA, etc.)
        TestCasesJson.class, // JSON parsing
        //
        TestCasesTesting.class, // Check bds unit testing system
        TestCasesCommandLineOptions.class, // Check command line options
        TestCasesRemote.class, // Accessing remote data (cloud storage)
        TestCasesReport.class, // Report generation



})
public class TestSuiteUnit {

}
