package com.bottlerocket.reporters.testrail.api;

public class CreateTestRun {
    public int suite_id;
    public String name;
    public String description;


    public CreateTestRun(int suite_id, String name, String description) {
        this.suite_id = suite_id;
        this.name = name;
        this.description = description;
    }
}
