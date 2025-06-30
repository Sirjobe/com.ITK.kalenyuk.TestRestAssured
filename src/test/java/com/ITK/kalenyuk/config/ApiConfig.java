package com.ITK.kalenyuk.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

public class ApiConfig {
    protected static final String BASE_URL = "http://193.233.193.42:9091/hub";
    protected static final String AUTH_ENDPOINT = "/auth/login";
    protected static final String INTERACTIVE_LOGIN_ENDPOINT = "/api/rest/oauth2/interactive/login";
    protected static final String OAUTH_REDIRECT_URL = "http://193.233.193.42:9091/oauth";

    // OAuth parameters
    protected static final String CLIENT_ID = "cf6f74d5-c1b8-457f-9d4b-2348fe19440f";
    protected static final String RESPONSE_TYPE = "token";
    protected static final String SCOPE = "cf6f74d5-c1b8-457f-9d4b-2348fe19440f Upsource TeamCity YouTrack%20Slack%20Integration 0-0-0-0-0";
    protected static final String DEFAULT_STATE = "759c16e5-be94-44a8-80a4-db4d04e2afe7";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.URLENC)
                .build();
    }
}