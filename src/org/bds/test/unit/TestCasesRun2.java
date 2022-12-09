package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueList;
import org.bds.test.TestCasesBase;
import org.bds.util.Gpr;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Test cases that require BDS code execution and check results
 * <p>
 * Note: These test cases requires that the BDS code is correctly parsed, compiled and executes.
 *
 * @author pcingola
 */
public class TestCasesRun2 extends TestCasesBase {

    public TestCasesRun2() {
        dir = "test/unit/run/";
    }
}
