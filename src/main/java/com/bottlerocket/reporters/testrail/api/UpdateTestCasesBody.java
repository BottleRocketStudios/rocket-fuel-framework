package com.bottlerocket.reporters.testrail.api;

import java.util.List;

public class UpdateTestCasesBody {
    public List<TestRailResult> results;

    public UpdateTestCasesBody() {}

    public UpdateTestCasesBody(List<TestRailResult> results) {
        this.results = results;
    }
}
