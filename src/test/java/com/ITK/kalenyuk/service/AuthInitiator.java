package com.ITK.kalenyuk.service;

import com.ITK.kalenyuk.utils.ConfigLoader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;

@Data
public class AuthInitiator {
    private int statusCode;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = ConfigLoader.getProperty("base_url");
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.URLENC)
                .build();
    }

    public AuthInitiator(){
        this.statusCode = startAuthFlow().getStatusCode();
    }

    public Response startAuthFlow(){
        return RestAssured.given()
                .redirects().follow(false)
                .queryParam("response_type", ConfigLoader.getProperty("response_type"))
                .queryParam("client_id", ConfigLoader.getProperty("client_id"))
                .queryParam("redirect_uri", ConfigLoader.getProperty("oauth_redirect_url"))
                .queryParam("scope", ConfigLoader.getProperty("scope"))
                .queryParam("state", ConfigLoader.getProperty("default_state"))
                .get(ConfigLoader.getProperty("auth_endpoint"));
    }

}
