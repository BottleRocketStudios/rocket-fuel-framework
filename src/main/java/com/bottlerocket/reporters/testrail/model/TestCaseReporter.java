package com.bottlerocket.reporters.testrail.model;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TestCaseReporter implements ITestListener {

    protected abstract void reportResults(List<TestCase> results);

    @Nullable
    protected TestCaseId getTestCaseId(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestCaseId.class);
    }

    @Override
    public void onFinish(ITestContext context) {
        // If context is null, just vomit out of here. Something has gone horribly wrong.
        assert(context != null);

        var passedResults = context.getPassedTests().getAllResults();
        var failedResults = context.getFailedTests().getAllResults();

        var allResults = new ArrayList<ITestResult>(passedResults);
        allResults.addAll(failedResults);

        var testCases = allResults.stream()
                .map(result -> {
                    var id = getTestCaseId(result);

                    if (id != null) {
                        return new TestCase(
                                id.id(),
                                (result.getEndMillis() - result.getStartMillis()),
                                TestCase.resultStatusFromBoolean(result.isSuccess())
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        reportResults(testCases);
    }

    @Override
    public void onTestStart(ITestResult result) {}

    @Override
    public void onTestSuccess(ITestResult result) {}

    @Override
    public void onTestFailure(ITestResult result) {}

    @Override
    public void onTestSkipped(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

}
