package com.ITK.kalenyuk.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

public class ApiConfig {
    private static String clientId;
    private static String redirectUri;
    private static String scope;

    @BeforeAll
    public static void setup(){
        RestAssured.baseURI= "http://193.233.193.42:9091";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();

    }

}
