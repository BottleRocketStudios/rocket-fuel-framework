package com.bottlerocket.reporters.testrail;

import com.bottlerocket.reporters.testrail.api.*;
import com.bottlerocket.reporters.testrail.model.TestCase;
import com.bottlerocket.reporters.testrail.model.TestCaseReporter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TestRailReporter extends TestCaseReporter {

    private TestRailConfig config = TestRailConfig.createFromEnvironment();

    private TestRailApi testRailApi = TestRailApiFactory.createTestRailApi(config);

    private TestRailResult.ResultStatus fromTestResult(TestCase.TestResultStatus status) {
        switch (status) {
            case Success:
                return TestRailResult.ResultStatus.Passed;
            case Failure:
                return TestRailResult.ResultStatus.Failed;
            default:
                return TestRailResult.ResultStatus.Untested;
        }
    }

    private TestRailResult toTestRailResult(TestCase testCase) {
        return new TestRailResult(
                testCase.getId(),
                fromTestResult(testCase.getStatus()),
                "",
                testCase.getLength().toString()
        );
    }

    @Override
    protected void reportResults(List<TestCase> results) {
        try {
            var call = testRailApi.createTestRun(config.projectId, new CreateTestRun(config.testSuiteId, String.format("Automation Run - %s", LocalDateTime.now().toString()), ""))
                    .execute();

            var run = call.body()
                    .id;

            var testRailResults = results.stream()
                    .map(this::toTestRailResult)
                    .collect(Collectors.toList());

            testRailApi.bulkUpdateTestCases(run, new UpdateTestCasesBody(testRailResults)).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
