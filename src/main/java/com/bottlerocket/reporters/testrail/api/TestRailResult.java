package com.bottlerocket.reporters.testrail.api;

import com.fasterxml.jackson.annotation.JsonValue;

public class TestRailResult {
    public String case_id;
    public ResultStatus status_id;
    public String comment;
    public String elapsed;

    public TestRailResult() {}

    public TestRailResult(String case_id, ResultStatus status_id, String comment, String elapsed) {
        this.case_id = case_id;
        this.status_id = status_id;
        this.comment = comment;
        this.elapsed = elapsed;
    }

    public enum ResultStatus {
        Passed(1),
        Blocked(2),
        Untested(3),
        Retested(4),
        Failed(5);

        private int value;

        ResultStatus(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }
    }
}
