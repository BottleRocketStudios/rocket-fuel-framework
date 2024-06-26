package com.bottlerocket.reporters;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * Created by ford.arnett on 12/12/16.
 */
public interface AutomationReporter {
    void initializeReporter();
    void initializeReporter(boolean forceNewReporter);

    void write();

    void addToTestCoverageList(String category, String testDescription);
    void writeTestCoverageList(File fileName);
    HashMap<String, ArrayList<String>> getTestCoverageList();

    ExtentTest startTest(String testName, String testDesc);
    ExtentTest startTest(String testName);
    void logTest(Status logStatus, String stepDetails);
    void logTest(Status logStatus, String stepDetails, String screenShotFileName) throws IOException;

    void logTest(Status logStatus, String stepDetails, boolean printToConsole);
    void logTest(Status logStatus, Throwable throwable);
    void logTest(Status logStatus, Throwable throwable, String screenShotFileName) throws IOException;

    void endTest();
    void close();
    ExtentTest appendChild(String testName, String description);

    void addSystemInfo(HashMap<String, String> map);
    void addSystemInfo(String param, String value);

    void addScreenshot(String fileName, String description) throws IOException;
    void addElementFound(By locator);
    void addCheckForExistence(By locator);
    void addCheckForDisplay(By locator);
    void addInfoToReport(String message);


    ExtentTest getTest();

    boolean shouldLog(EnumSet<LoggingLevel> acceptableLogLevels);
    boolean shouldLog(LoggingLevel logLevelForInstance, EnumSet<LoggingLevel> acceptableLogLevels);
    void setLoggingLevel(LoggingLevel loggingLevel);

    @Deprecated
    boolean logging();

    public enum LoggingLevel {
        //My current thinking. Verbose is for anything and everything,
        //Debug hides things that you wouldn't want to see unless you are down a deep rabbit hole of troubleshooting, this is where most development will be done
        //Error only displays stack traces and serious notices from the system
        VERBOSE,
        DEBUG,
        ERROR;

        public static EnumSet<LoggingLevel> all = EnumSet.allOf(LoggingLevel.class);
    }
}
