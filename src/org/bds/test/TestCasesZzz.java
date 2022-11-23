package org.bds.test;

import org.bds.Bds;
import org.bds.Config;
import org.bds.run.BdsRun;
import org.bds.run.Coverage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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


}
