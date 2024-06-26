package com.bottlerocket.reporters.testrail.model;


/**
 * Test case represents a single test that has some reported status
 */
public class TestCase {

    private final String id;
    private final Long length;
    private final TestResultStatus status;

    /**
     * @param id The ID of the test case
     * @param status The status of the test case
     * @param length The duration of the test (how long did it take to run?)
     */
    public TestCase(String id, Long length, TestResultStatus status) {
        this.id = id;
        this.length = length;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Long getLength() {
        return length;
    }

    public TestResultStatus getStatus() {
        return status;
    }

    public enum TestResultStatus {
        Success,
        Failure
    }

    public static TestResultStatus resultStatusFromBoolean(Boolean result) {
        if (result) return TestResultStatus.Success;
        else return TestResultStatus.Failure;
    }
}

