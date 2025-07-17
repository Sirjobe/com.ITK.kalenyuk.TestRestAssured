package com.ITK.kalenyuk.service;

import com.ITK.kalenyuk.models.AuthResponse;
import com.ITK.kalenyuk.utils.ConfigLoader;
import io.restassured.response.Response;
import lombok.Data;

import static io.restassured.RestAssured.given;

@Data
public class AuthInitiator {
    private int statusCode;
    private AuthResponse authResponse;

    public AuthInitiator(String token) {
        Response response = startAuthFlow(token);
        this.statusCode = response.getStatusCode();
        if (response.getStatusCode() == 200) {
            this.authResponse = response.as(AuthResponse.class);
        }
    }

    public Response startAuthFlow(String token) {
        String fullUrl = ConfigLoader.getProperty("base_url") + ConfigLoader.getProperty("auth_endpoint");

        return given()
                .redirects().follow(false)
                .auth().oauth2(token)
                .when()
                .get(fullUrl);
    }
}