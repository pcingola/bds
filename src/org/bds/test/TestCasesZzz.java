package org.bds.test;

import org.bds.Config;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueObject;
import org.bds.run.BdsRun;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.bds.libraries.LibraryException.THROWABLE_FIELD_VALUE;

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
    public void test277_throw_string() {
        verbose = true;
        BdsTest bdsTets = runAndCheckException("test/run_277.bds", "Exception");
        ValueObject exceptionObject = (ValueObject) bdsTets.getBds().getBdsRun().getVm().getException();
        Value exceptionValue = exceptionObject.getFieldValue(THROWABLE_FIELD_VALUE);
        Assert.assertEquals("You can also throw a string, but it's a bit weird...", exceptionValue.asString());
    }
}
