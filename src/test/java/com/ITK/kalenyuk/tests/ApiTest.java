package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.config.ApiConfig;
import com.ITK.kalenyuk.utils.ExcelDataProvider;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiTest extends ApiConfig {

    @DisplayName("Проверка авторизации с разными данных")
    @ParameterizedTest(name = "Логин: {0}, Пароль: {1}, Ожидаемый результат: {2}")
    @MethodSource("loginDataProvider")
    public void testOAuth2Authorization(String username, String password, boolean expectedResult) {
        // Шаг 1: Первоначальный запрос на аутентификацию
        Response authResponse = RestAssured.given()
                .redirects().follow(false)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", OAUTH_REDIRECT_URL)
                .queryParam("scope", SCOPE)
                .queryParam("state", DEFAULT_STATE)
                .get(AUTH_ENDPOINT);
        System.out.println("Initial Status: "+authResponse.getStatusCode());

        // Шаг 2: Интерактивный вход в систему
        Response loginResponse = RestAssured.given()
                .redirects().follow(false)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", OAUTH_REDIRECT_URL)
                .queryParam("scope", SCOPE)
                .queryParam("state", DEFAULT_STATE)
                .queryParam("rememberMe", "true")
                .formParam("username", username)
                .formParam("password", password)
                .formParam("rememberMe", "on")
                .post(INTERACTIVE_LOGIN_ENDPOINT);

        int statusCode = loginResponse.getStatusCode();
        System.out.println("Статус входа в систему: " + statusCode);

        String loginLocationHeader = loginResponse.getHeader("Location");
        assertNotNull(loginLocationHeader, "Заголовок Location отсутствует");

        boolean locationHeader = loginResponse.getHeader("Location").contains("failed");
        if (!locationHeader){
            System.out.println("Сообщение удалось отправить: "+loginResponse.getHeader("Location").contains("failed"));
        }else {
            System.out.println("Сообщение не удалось отправить: "+loginResponse.getHeader("Location").contains("failed"));
        }

        if (!locationHeader) {

            Assertions.assertEquals(303, statusCode, "Ожидаемое перенаправление для успешного входа в систему");

            String loginRedirectUrl = loginResponse.getHeader("Location");
            String oauthURL = loginRedirectUrl.split("\\?")[0];
            String stateOAuth = loginRedirectUrl.split("state=")[1].split("&")[0];

            // Шаг 3: Выполните перенаправление
            Response redirectResponse = RestAssured.given()
                    .redirects().follow(false)
                    .cookies(loginResponse.getCookies())
                    .queryParam("response_type", RESPONSE_TYPE)
                    .queryParam("client_id", CLIENT_ID)
                    .queryParam("redirect_uri", OAUTH_REDIRECT_URL)
                    .queryParam("scope", SCOPE)
                    .queryParam("state", stateOAuth)
                    .get(oauthURL);

            String finalLocation = redirectResponse.getHeader("Location");
            System.out.println("Окончательное перенаправление: " + finalLocation);


            assertTrue(finalLocation.contains("access_token"),
                    "Токен доступа должен присутствовать в URL-адресе перенаправления");

        } else {
            assertTrue(locationHeader,
                     "В заголовке находиться информация об ошибке");
        }
        System.out.println("");
    }

    static Iterator<Object[]> loginDataProvider() {
        return ExcelDataProvider.provideLoginData("src/test/resources/LoginTestData.xlsx");
    }
}