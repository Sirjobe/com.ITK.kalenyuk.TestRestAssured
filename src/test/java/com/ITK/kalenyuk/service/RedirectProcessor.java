package com.ITK.kalenyuk.service;

import com.ITK.kalenyuk.models.AuthRequest;
import com.ITK.kalenyuk.utils.ConfigLoader;
import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RedirectProcessor {
    @NonNull
    private String redirect;
    @NonNull
    private Cookies cookie;
    private String finalLocation;
    @NonNull
    private AuthRequest user;



    private void extractTokenFromRedirect(){
        String oauthURL = redirect.split("\\?")[0];
        String stateOAuth = redirect.split("state=")[1].split("&")[0];

        Response redirectResponse = RestAssured.given()
                .redirects().follow(false)
                .cookies(cookie)
                .queryParam("response_type", ConfigLoader.getProperty("response_type"))
                .queryParam("client_id", ConfigLoader.getProperty("client_id"))
                .queryParam("redirect_uri", ConfigLoader.getProperty("oauth_redirect_url"))
                .queryParam("scope", ConfigLoader.getProperty("scope"))
                .queryParam("state", stateOAuth)
                .get(oauthURL);

        this.finalLocation = redirectResponse.getHeader("Location");

        if (finalLocation != null && finalLocation.contains("#access_token")){
            String hashPart = finalLocation.split("#")[1];
            user.setToken(hashPart.split("access_token=")[1].split("&")[0]);
            System.out.println("Access Token: " + user.getToken());
        }
    }
}
