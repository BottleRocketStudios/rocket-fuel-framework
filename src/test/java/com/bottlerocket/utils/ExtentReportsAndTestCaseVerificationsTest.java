package com.bottlerocket.utils;

import freemarker.template.TemplateException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.reporters.Files;
import com.bottlerocket.webdriverwrapper.uiElementLocator.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

//TODO: Per conversations about the dual purpose nature of this test class, it would be better to break test methods
// dealing with extent report and verifications out separately, and add each to their own test class.
public class ExtentReportsAndTestCaseVerificationsTest {
    /**
     * This test uses the OutputUtils class createRGraphJson method to compare test time duration in two Extent Reports
     * To simulate this, two sample Extent Reports are included as resources. The second report (002) includes two slow test cases
     * The output is an HTML file created in the temp directory which includes an rGraph chart of test case durations
     * This can be used for the following cases:
     *  - Comparing extent reports from two different environments
     *  - Comparing extent reports to find a performance trend
     *  - Comparing extent reports to find a specific slow test
     * To determine which specific test is slow, use the next Test Case - reportLogDurationOutputTest
     * @throws Exception
     */
//    @Test
//    public void reportCompareTest() throws Exception {
//        ExtentReportsAndTestCaseVerificationsBuilder util = new ExtentReportsAndTestCaseVerificationsBuilder();
//        String sampleSet1 = "OCT-001";
//        String sampleSet2 = "OCT-002";
//        List<String> expectedList = List.of(sampleSet1, sampleSet2);
//
//        File file1 = util.getFileFromResource("test/extent-report-001.html");
//        File file2 = util.getFileFromResource("test/extent-report-002.html");
//
//        String outputLocation = util.createRGraphJson(file1, file2, sampleSet1, sampleSet2, "if this is going to be used again then this needs updating");
//        File file = new File(outputLocation);
//        String contents = Files.readFile(file);
//
//        Assert.assertTrue(
//                expectedList.stream().allMatch(x -> contents.contains(x)),
//                "Verify the expected sample set names appear in the generated file."
//        );
//    }

    /**
     * This test uses the OutputUtils class getTestsFilteredByDuration method to find a list of tests that exceed a duration parameter
     * To simulate this, the 002 Extent Report includes two slow test cases: seeFreeDispensedOrder and seeFreeKioskFlow
     * The output is logged text that can be reviewed in the console window
     * Spreadsheet: Excel > (select column) > Data > Text to Columns > Delimited > Delimiter Other: Pipe > Finish
     * @throws Exception
     */
//    @Test
//    public void reportLogDurationOutputTest() throws Exception {
//        ExtentReportsAndTestCaseVerificationsBuilder util = new ExtentReportsAndTestCaseVerificationsBuilder();
//        List<String> expectedList = List.of("seeFreeDispensedOrder", "seeFreeKioskFlow");
//
//        File file = util.getFileFromResource("test/extent-report-002.html");
//        String contents = util.getTestsFilteredByDuration(file, 2000d);
//
//        Assert.assertTrue(
//                expectedList.stream().allMatch(x -> contents.contains(x)),
//                "Verify the slows tests appear in the logged output."
//        );
//    }

    /**
     * This test uses the OutputUtils class printAllVerifications method to print all the verification in a project to an HTML report
     * For this test the Test Cases in the framework are used, but normally this would be the Test Project's tests
     * The output is an HTML file created in the temp directory
     * This document can be shared with the Manual QA team or the Client
     * @throws IOException
     * @throws TemplateException
     */
//    @Test
//    public void printAllVerifications() throws IOException, TemplateException {
//        ExtentReportsAndTestCaseVerificationsBuilder util = new ExtentReportsAndTestCaseVerificationsBuilder();
//        List<String> expectedList = List.of("LoggingTest", "ExtentReportsAndTestCaseVerificationsTest");
//        String outputLocation = util.printAllVerifications("If this is used again this will need to be updated");
//
//        File file = new File(outputLocation);
//        String contents = Files.readFile(file);
//
//        Assert.assertTrue(
//                expectedList.stream().allMatch(x -> contents.contains(x)),
//                "Verify the report contains the expected verifications."
//        );
//    }

    /**
     * This test uses the OutputUtils class parseFileForTest method to find a list of all Tests Cases in a project
     * Tests that disabled are marked with ***disabled***
     * The output is logged text that can be reviewed in the console window
     * @throws IOException
     */
    @Test
    public void printAllTests() throws IOException {
        ExtentReportsAndTestCaseVerificationsBuilder util = new ExtentReportsAndTestCaseVerificationsBuilder();
        List<String> expectedList = List.of("LoggingTest", "ExtentReportsAndTestCaseVerificationsTest");

        List<String> list = util.readFiles(fileLines -> util.parseFileForTest(fileLines));

        Assert.assertTrue(
                expectedList.stream().allMatch(list::contains),
                "Verify all test classes were printed."
        );
    }

    @Test
    public void resourceLocatorTest() {
        UIElementLocator someLocator = new UIElementLocator();
    }
}
