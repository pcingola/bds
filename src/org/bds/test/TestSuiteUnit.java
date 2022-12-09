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
        TestCasesTail.class, // Tail, following files
        TestCasesVm.class, // Virtual machine
        TestCasesInterpolate.class, // Variable interpolation
        TestCasesExecutioners.class, // Task executioners
        TestCasesFunctionDeclaration.class, // Function declaration
        //
        TestCasesLang.class, // Language, compiler, built-in statements
        TestCasesOperatorsAndMath.class, // Operators and math
        TestCasesVariable.class, // Variables, initializations, casting, etc.
        TestCasesFunctions.class, // Function definitions
        TestCasesFile.class, // File (native) functions
        TestCasesFunctionsMethodsNative.class, // Native functions
        TestCasesJson.class, // JSON parsing
        TestCasesLoopsIf.class, // Loops ('for', 'while') and 'if'
        TestCasesList.class, // Lists
        TestCasesMap.class, // Maps
        TestCasesSwitch.class, // Swtch statements
        TestCasesClass.class, // Class definition, fields, methods, etc.
        TestCasesSys.class, // 'sys' expressions
        TestCasesTask.class, // 'task' expressions, task dependencies, etc.
        TestCasesTaskCustomResources.class, // Custom resource allocation in tasks (e.g. GPU, FFPGA, etc.)
        TestCasesTryCatchFinally.class, // Try, catch, finally statements
        //
        TestCasesTesting.class, // Check bds unit testing system
        TestCasesCommandLineOptions.class, // Check command line options
        TestCasesRemote.class, // Accessing remote data (cloud storage)
        TestCasesReport.class, // Report generation


})
public class TestSuiteUnit {

}
