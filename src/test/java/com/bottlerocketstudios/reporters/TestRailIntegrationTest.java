package com.bottlerocketstudios.reporters;

import com.bottlerocket.reporters.testrail.TestRailReporter;
import com.bottlerocket.reporters.testrail.model.TestCaseId;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * These "tests" are disabled because they hit actual TestRail APIs
 * Uncomment them  if you want to see the TestRailReporter Listener in action
 */

//@Listeners(TestRailReporter.class)
public class TestRailIntegrationTest {

    @Test(enabled = false)
    @TestCaseId(id = "2017089")
    public void testPassing() {
        assert(true);
    }

    @Test(enabled = false)
    @TestCaseId(id = "2017090")
    public void testFailure() {
        assert(false);
    }

}
