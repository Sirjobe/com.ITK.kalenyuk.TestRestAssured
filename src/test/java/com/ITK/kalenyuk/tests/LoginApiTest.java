package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.config.ApiConfig;
import com.ITK.kalenyuk.models.AuthRequest;
import com.ITK.kalenyuk.models.AuthResponse;
import com.ITK.kalenyuk.utils.ExcelDataProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.*;




public class LoginApiTest extends ApiConfig {

    @DisplayName("Проверка авторизации с разными данных")
    @ParameterizedTest(name = "Логин: {0}, Пароль: {1}, Ожидаемый результат: {2}")
    @MethodSource("loginDataProvider")
    public void checkingConnection(String username, String password, boolean isSuccess){
        AuthRequest authRequest = new AuthRequest(username, password);

        Response response = RestAssured.given()
                .auth().basic(authRequest.getLogin(),authRequest.getPassword())
              //  .body(authRequest)
                .post("/hub/auth/login/");

        if(isSuccess){ //Если авторизация успешна
            assertEquals(200,response.getStatusCode());
            System.out.println(response.getStatusCode());
            AuthResponse authResponse = response.as(AuthResponse.class);
            System.out.println(authResponse.getToken());
            assertNotNull(authResponse.getToken());
        }else { // Если авторизация не успешная
            assertTrue(response.getStatusCode()==401||response.getStatusCode()==400);
            System.out.println(response.getStatusCode());
            AuthResponse authResponse = response.as(AuthResponse.class);
            System.out.println(authResponse.getMessage());
            assertNotNull(authResponse.getMessage());
        }

    }

    static Iterator<Object[]> loginDataProvider() {
        return ExcelDataProvider.provideLoginData("src/test/resources/LoginTestData.xlsx");
    }

}
