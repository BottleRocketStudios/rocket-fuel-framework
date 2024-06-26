package com.bottlerocket.reporters.testrail.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface TestRailApi {

    @POST("add_run/{project_id}")
    Call<TestCaseRun> createTestRun(@Path("project_id") String projectId, @Body CreateTestRun run);

    @POST("add_results_for_cases/{run_id}")
    Call<List<TestCaseRun>> bulkUpdateTestCases(@Path("run_id") int runId, @Body UpdateTestCasesBody cases);

}
