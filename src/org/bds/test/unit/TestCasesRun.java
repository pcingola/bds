package org.bds.test.unit;

import junit.framework.Assert;
import org.bds.test.TestCasesBase;
import org.bds.util.Timer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test cases that require BDS code execution and check results
 * <p>
 * Note: These test cases requires that the BDS code is correctly parsed, compiled and executes.
 *
 * @author pcingola
 */
public class TestCasesRun extends TestCasesBase {

    public TestCasesRun() {
        dir = "test/unit/run/";
    }
}
