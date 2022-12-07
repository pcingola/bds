package org.bds.test;

import org.bds.Bds;
import org.bds.Config;
import org.bds.run.BdsRun;
import org.bds.run.Coverage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Quick test cases when creating a new feature...
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCasesBaseAws {

    @Before
    public void beforeEachTest() {
        BdsRun.reset();
        Config.get().load();
    }

    @Test
    public void test201() {
        verbose = true;
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("z", "{ i: 0, r: 0.0, s:  }");
        expectedValues.put("z2", "{ i: 0, r: 0.0, s:  }");

        runAndCheck("test/run_201.bds", expectedValues);
    }


}
