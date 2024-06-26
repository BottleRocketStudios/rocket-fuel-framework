package com.bottlerocket.reporters;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.bottlerocket.utils.ErrorHandler;
import com.bottlerocket.utils.Logger;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * Created by ford.arnett on 12/12/16.
 */
public class ExtentReporter implements AutomationReporter {
    private static ExtentReports reporter = null;
    private String fileName;
    ExtentTest test;
    HashMap<String, ArrayList<String>> testCoverage = new HashMap<>();
    private LoggingLevel loggingLevel = LoggingLevel.DEBUG;

    public ExtentReporter(String fileName) {
        this.fileName = fileName + ".html";
    }

    @Override
    public void initializeReporter() {
        if(reporter == null) {
            reporter = new ExtentReports();
            reporter.attachReporter(new ExtentHtmlReporter(fileName));
        }
    }

    @Override
    public void initializeReporter(boolean forceNewReporter) {
        if(reporter == null || forceNewReporter) {
            reporter = new ExtentReports();
            reporter.attachReporter(new ExtentHtmlReporter(fileName));
        }
    }

    @Override
    public void write() {
        reporter.flush();
    }

    @Override
    public void writeTestCoverageList(File fileName) {
        if(testCoverage.isEmpty()) {
            Logger.log("The test coverage list is empty, not writing to file");
        } else {
            try (PrintWriter out = new PrintWriter(fileName)) {
                for (String key : testCoverage.keySet()) {
                    out.println(key);
                    ArrayList<String> testCaseDescriptions = testCoverage.get(key);
                    for (String str : testCaseDescriptions) {
                        out.println("\t" + str);
                    }

                }
            } catch (IOException ex) {
                ErrorHandler.printErr("Error writing out test coverage list", ex);
            }
        }
    }

    @Override
    public void addToTestCoverageList(String category, String testDescription) {
        testCoverage.putIfAbsent(category, new ArrayList<>());
        testCoverage.get(category).add(testDescription);
    }

    @Override
    public HashMap<String, ArrayList<String>> getTestCoverageList() {
        return testCoverage;
    }

    @Override
    public ExtentTest startTest(String testName, String testDesc) {
        if(reporter == null) {
            Logger.log("It seems as if the reporter was not initialized correctly. Reporting will not function correctly.");
            return null;
        }

        test = reporter.createTest(testName, testDesc);
        return test;
    }

    @Override
    public ExtentTest startTest(String testName) {
        if(reporter == null) {
            Logger.log("It seems as if the reporter was not initialized correctly. Reporting will not function correctly.");
            return null;
        }
        test = reporter.createTest(testName);
        return test;
    }

    @Override
    public void logTest(Status logStatus, String stepDetails) {
        if(reporter == null) {
            Logger.log("It seems as if the reporter was not initialized correctly. Reporting will not function correctly.");
            return;
        }
        if(test == null) {
            startTest("Test name was not recorded properly, unknown test");
        }
        test.log(logStatus, stepDetails);
    }

    /**
     * Adds a screenshot to the test we are logging
     *
     * @param logStatus
     * @param stepDetails
     * @param screenShotFileName Path to the screenshot
     * @throws IOException
     */
    @Override
    public void logTest(Status logStatus, String stepDetails, String screenShotFileName) throws IOException {
        test.log(logStatus, stepDetails, getMediaEntity(screenShotFileName));
    }

    @Override
    public void logTest(Status logStatus, String stepDetails, boolean printToConsole) {
        if(test == null) {
            startTest("Test name was not recorded properly, unknown test");
        }
        if(printToConsole) {
            Logger.log(stepDetails);
        }

        test.log(logStatus, stepDetails);
    }

    @Override
    public void logTest(Status logStatus, Throwable throwable) {
        test.log(logStatus, throwable);
    }

    /**
     * This is mainly intended for fails, but could be used in other cases where it would make sense to log a throwable.
     *
     * @param logStatus
     * @param throwable
     * @param screenShotFileName
     * @throws IOException
     */
    @Override
    public void logTest(Status logStatus, Throwable throwable, String screenShotFileName) throws IOException {
        test.log(logStatus, throwable, getMediaEntity(screenShotFileName));
    }

    @Override
    public void endTest() {
        throw  new NotImplementedException("This may not have an equivalent in extent reports 4.0");
        //reporter.end(test);
    }

    @Override
    public void close() {
        reporter.flush();
    }

    @Override
    public ExtentTest appendChild(String testName, String description) {
        throw new NotImplementedException("This needs some more work, but not sure we are using it anywhere/any project");
        //return test.appendChild(reporter.startTest(testName,description));
    }


    @Override
    public ExtentTest getTest() {
        return test;
    }

    @Deprecated
    @Override
    public boolean logging() {
        return test != null && loggingLevel.equals(LoggingLevel.DEBUG) || loggingLevel.equals(LoggingLevel.VERBOSE);
    }

    /**
     * Check against the current log level to see if we should log the message
     *
     * @param acceptableLogLevels The levels at which a message should be logged
     * @return iff the current logging level is one of the logging levels for this check
     */
    @Override
    public boolean shouldLog(EnumSet<LoggingLevel> acceptableLogLevels) {
        return shouldLog(loggingLevel, acceptableLogLevels);
    }

    @Override
    public boolean shouldLog(LoggingLevel logLevelForInstance, EnumSet<LoggingLevel> acceptableLogLevels) {
        return acceptableLogLevels.contains(logLevelForInstance);
    }

    @Override
    public void setLoggingLevel(LoggingLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    @Override
    public void addSystemInfo(HashMap<String, String> map) {
        for(String keys : map.keySet()) {
            reporter.setSystemInfo(keys, map.get(keys));
        }
    }

    @Override
    public void addSystemInfo(String param, String value) {
        if(value != null && !value.isEmpty()) {
            reporter.setSystemInfo(param, value);
        }
    }

    @Override
    public void addScreenshot(String fileName, String description) throws IOException {
        if(test != null && shouldLog(LoggingLevel.all)) {
            MediaEntityModelProvider mediaEntityModelProvider = MediaEntityBuilder.createScreenCaptureFromPath(fileName).build();
            test.info("Screenshot: "  + " " + description, mediaEntityModelProvider);
    }
    }

    private MediaEntityModelProvider getMediaEntity(String fileName) throws IOException {
        return MediaEntityBuilder.createScreenCaptureFromPath(fileName).build();

    }

    @Override
    public void addElementFound(By locator) {
        if(logging()) {
            logTest(Status.INFO, "Found element " + locator);
        }
    }

    @Override
    public void addCheckForExistence(By locator) {
        if(logging()) {
            logTest(Status.INFO, "Checking for existence of " + locator);
        }
    }

    @Override
    public void addCheckForDisplay(By locator) {
        if(logging()) {
            logTest(Status.INFO, "Checking if " + locator + " element is displayed");
        }
    }

    /**
     * Add a general message to the report.
     *
     * @param message
     */
    @Override
    public void addInfoToReport(String message) {
        if(reporter != null && logging()) {
            logTest(Status.INFO, message);
        }
    }
}
