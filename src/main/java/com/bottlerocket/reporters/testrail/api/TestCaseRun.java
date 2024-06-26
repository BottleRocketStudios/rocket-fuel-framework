package com.bottlerocket.reporters.testrail.api;

import com.fasterxml.jackson.annotation.JsonCreator;

public class TestCaseRun {
    public int id;

    public TestCaseRun() {

    }

    @JsonCreator
    public TestCaseRun(int id) {
        this.id = id;
    }
}
