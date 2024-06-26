package com.bottlerocket.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoggingTest {
    @Test
    public void loggingTest() {
        LogBuilder logBuilder = new LogBuilder();
        Logger.log("");

        String message1 = logBuilder
                .appendPrettyLineSeparator()
                .appendLine("LOGGING TEST 1")
                .appendPrettyLineSeparator()
                .appendLine("LOG NAME:    {}, {}", "LogName1", "LogName2")
                .appendLine("LOG EMAIL:   {}", "test1@test1.com")
                .appendLine("LOG NUMBER:  {}", "N1001")
                .appendLine("LOG ID:      {}", "ae1001")
                .appendPrettyLineSeparator()
                .appendLineBreak()
                .logAndGetMessage();

        Assert.assertEquals(
                message1,
                "-------------------------\n" +
                        "LOGGING TEST 1\n" +
                        "-------------------------\n" +
                        "LOG NAME:    LogName1, LogName2\n" +
                        "LOG EMAIL:   test1@test1.com\n" +
                        "LOG NUMBER:  N1001\n" +
                        "LOG ID:      ae1001\n" +
                        "-------------------------\n" +
                        "\n",
                "Verify the the logging output is correct when using implicit line separators."
                );

        String message2 = logBuilder
                .appendPrettyLineSeparator('=', 25)
                .appendLine("LOGGING TEST 2")
                .appendPrettyLineSeparator('=', 25)
                .appendLine("LOG NAME:    {}, {}", "LogName1", "LogName2")
                .appendLine("LOG EMAIL:   {}", "test2@test2.com")
                .appendLine("LOG NUMBER:  {}", "N1002")
                .appendLine("LOG ID:      {}", "ae1002")
                .appendPrettyLineSeparator('=', 25)
                .appendLineBreak()
                .logAndGetMessage();

        Assert.assertEquals(message2,
                "=========================\n" +
                        "LOGGING TEST 2\n" +
                        "=========================\n" +
                        "LOG NAME:    LogName1, LogName2\n" +
                        "LOG EMAIL:   test2@test2.com\n" +
                        "LOG NUMBER:  N1002\n" +
                        "LOG ID:      ae1002\n" +
                        "=========================\n" +
                        "\n",
                "Verify the logging output is correct when using explicit line separators."
        );
    }
}