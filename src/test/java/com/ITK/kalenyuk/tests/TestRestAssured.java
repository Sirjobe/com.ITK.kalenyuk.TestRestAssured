package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.utils.ExcelDataProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Iterator;


public class TestRestAssured {

    @Test
    public void testOAuth2Authorization(){
        String authURL = "http://193.233.193.42:9091/hub/auth/login";
        String loginURL = "http://193.233.193.42:9091/hub/api/rest/oauth2/interactive/login";
        String loginRedirectUrl;
        String clientId = "cf6f74d5-c1b8-457f-9d4b-2348fe19440f";
        String responseType = "token" ; //тип ответа
        String redirectUri = "http://193.233.193.42:9091/oauth";
        String scope = "cf6f74d5-c1b8-457f-9d4b-2348fe19440f Upsource TeamCity YouTrack%20Slack%20Integration 0-0-0-0-0";
        String state = "759c16e5-be94-44a8-80a4-db4d04e2afe7";
        String accessToken;
        String loginOAuthUrl = null;
        //----------------------------------------------------------------------
        String username = "QA1";
        String password = "TestPassword";
        String rememberMe = "on";

        // Эмулируем GET-запрос (должен вернуть 200 на страницу логина)
        Response authResponse = RestAssured.given()
                .redirects().follow(false)
                .queryParam("response_type",responseType)
                .queryParam("client_id",clientId)
                .queryParam("redirect_uri",redirectUri)
                .queryParam("scope",scope)
                .queryParam("state",state)
                .get(authURL);
        System.out.println(authResponse.getStatusCode());

        // Отправка POST-запрос (должен вернуть 303 на страницу логина)
        Response loginResponse = RestAssured.given()
                .redirects().follow(false)
                .queryParam("response_type",responseType)
                .queryParam("client_id",clientId)
                .queryParam("redirect_uri",redirectUri)
                .queryParam("scope",scope)
                .queryParam("state",state)
                .queryParam("rememberMe","true")
                .formParam("username",username)
                .formParam("password",password)
                .formParam("rememberMe",rememberMe)
                .when()
                .post(loginURL);


        loginRedirectUrl = loginResponse.getHeader("Location");
        System.out.println(loginResponse.getStatusCode());
        System.out.println(loginRedirectUrl);
        String oauthURL = loginRedirectUrl.split("\\?")[0];
        String stateOAuth = loginRedirectUrl.split("state=")[1];


        // Отправка GET запроса для дальнейшей аутентификацией (должен вернуть 302 на страницу логина)
        if(loginResponse.getStatusCode()==303){
            Response loginRedirectResponse = RestAssured.given()
                    .redirects().follow(false)
                    .cookies(loginResponse.getCookies())
                    .queryParam("response_type",responseType)
                    .queryParam("client_id",clientId)
                    .queryParam("redirect_uri",redirectUri)
                    .queryParam("scope",scope)
                    .queryParam("state",stateOAuth)
                    .get(oauthURL);
            System.out.println(loginRedirectResponse.getStatusCode());
            loginOAuthUrl = loginRedirectResponse.getHeader("Location");
            System.out.println(loginOAuthUrl);
        }

        // Извлечение токена
        if (loginOAuthUrl != null && loginOAuthUrl.contains("#access_token")){
            String hashPart = loginOAuthUrl.split("#")[1];
            accessToken = hashPart.split("access_token=")[1].split("&")[0];
            System.out.println("Access Token: " + accessToken);

        }
    }

    static Iterator<Object[]> loginDataProvider() {
        return ExcelDataProvider.provideLoginData("src/test/resources/LoginTestData.xlsx");
    }
}
