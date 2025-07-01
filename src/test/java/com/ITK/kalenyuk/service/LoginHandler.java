package com.ITK.kalenyuk.service;

import com.ITK.kalenyuk.models.AuthRequest;
import com.ITK.kalenyuk.utils.ConfigLoader;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import lombok.Data;

@Data
public class LoginHandler {
    private AuthRequest  authRequest;
    private String loginLocationHeader;
    private int statusCode;
    private Cookies cookie;


    public LoginHandler(){
        this.loginLocationHeader = responseLogin().getHeader("Location");
        this.statusCode = responseLogin().getStatusCode();
        this.cookie = responseLogin().getDetailedCookies();
    }

    private Response responseLogin(){
        return RestAssured.given()
                .redirects().follow(false)
                .queryParam("response_type", ConfigLoader.getProperty("response_type"))
                .queryParam("client_id", ConfigLoader.getProperty("client_id"))
                .queryParam("redirect_uri", ConfigLoader.getProperty("oauth_redirect_url"))
                .queryParam("scope", ConfigLoader.getProperty("scope"))
                .queryParam("state", ConfigLoader.getProperty("default_state"))
                .queryParam("rememberMe", "true")
                .formParam("username", authRequest.getUsername())
                .formParam("password", authRequest.getPassword())
                .formParam("rememberMe", "on")
                .post(ConfigLoader.getProperty("interactive_login_endpoint"));
    }


}