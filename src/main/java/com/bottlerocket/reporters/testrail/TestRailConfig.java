package com.bottlerocket.reporters.testrail;

final public class TestRailConfig {
    public final String baseUrl;
    public final int testSuiteId;
    public final String projectId;
    public final String userName;
    public final String apiKey;

    public TestRailConfig(String baseUrl, int testSuiteId, String projectId, String userName, String apiKey) {
        this.baseUrl = baseUrl;
        this.testSuiteId = testSuiteId;
        this.projectId = projectId;
        this.userName = userName;
        this.apiKey = apiKey;
    }

    public static TestRailConfig createFromEnvironment() {
        return new TestRailConfig(
                System.getenv("TEST_RAIL_API_URL"),
                Integer.parseInt(System.getenv("TEST_RAIL_SUITE_ID")),
                System.getenv("TEST_RAIL_PROJECT_ID"),
                System.getenv("TEST_RAIL_USER"),
                System.getenv("TEST_RAIL_API_KEY")
        );
    }
}
