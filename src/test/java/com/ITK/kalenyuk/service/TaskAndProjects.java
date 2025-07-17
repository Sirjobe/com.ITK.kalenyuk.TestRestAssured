package com.ITK.kalenyuk.service;

import com.ITK.kalenyuk.models.Issue;
import com.ITK.kalenyuk.models.Project;
import com.ITK.kalenyuk.utils.ConfigLoader;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import lombok.Data;

import java.util.List;

import static io.restassured.RestAssured.given;

@Data
public class TaskAndProjects {
    private int statusCode;
    private List<Project> projects;
    private String projectId;
    private Issue createdIssue;
    private int statusCodeIssue;

    public TaskAndProjects(String token) {
        Response response = getListProjects(token);
        this.statusCode = response.getStatusCode();
        if (response.getStatusCode() == 200) {
            this.projects = response.as(new TypeRef<List<Project>>() {});
            this.projectId = projects.stream()
                    .filter(project -> "QA".equals(project.getName()))
                    .findFirst()
                    .map(Project::getId)
                    .orElse(null);
        }
    }

    public Response getListProjects(String token) {
        String fullUrl = ConfigLoader.getProperty("base_url") + ConfigLoader.getProperty("project_endpoint");

        return given().log().all()
                .header("Authorization", "Bearer " + token)
                .queryParam("fields", "id,name,shortName")
                .when()
                .get(fullUrl);
    }

    public Response createTask(String token, String projectId, String summary, String description) {
        String fullUrl = ConfigLoader.getProperty("base_url") +
                ConfigLoader.getProperty("project_endpoint") + "/" +
                projectId +
                ConfigLoader.getProperty("issue_endpoint");

        String requestBody = String.format(
                "{ \"project\": {\"id\": \"%s\"}, \"summary\": \"%s\", \"description\": \"%s\" }",
                projectId, summary, description
        );

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post(fullUrl);

        this.statusCodeIssue = response.getStatusCode();
        if (response.getStatusCode() == 200) {
            this.createdIssue = response.as(Issue.class);
        }
        return response;
    }

    public Response deleteTask(String token, String issueId) {
        String fullUrl = ConfigLoader.getProperty("base_url") +
                ConfigLoader.getProperty("delete_endpoint") + "/" +
                issueId;

        return given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(fullUrl);
    }

    public Response getTaskById(String token, String projectId, String taskId) {
        String fullUrl = ConfigLoader.getProperty("base_url") +
                ConfigLoader.getProperty("project_endpoint") + "/" +
                projectId +
                ConfigLoader.getProperty("issue_endpoint") + "/" +
                taskId;

        return given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(fullUrl);
    }
}