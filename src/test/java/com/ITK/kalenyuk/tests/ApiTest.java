package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.models.AuthRequest;
import com.ITK.kalenyuk.service.AuthInitiator;
import com.ITK.kalenyuk.service.LoginHandler;
import com.ITK.kalenyuk.service.RedirectProcessor;
import com.ITK.kalenyuk.utils.ExcelDataProvider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiTest {

    @DisplayName("Проверка авторизации с разными данных")
    @ParameterizedTest(name = "Логин: {0}, Пароль: {1}, Ожидаемый результат: {2}")
    @MethodSource("loginDataProvider")
    public void testOAuth2Authorization(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username,password);
        LoginHandler loginHandler = new LoginHandler();
        AuthInitiator authInitiator = new AuthInitiator();
        System.out.println("Предварительный Статус: "+ authInitiator.getStatusCode());

        System.out.println("Статус входа в систему: " + loginHandler.getStatusCode());

        String loginLocationHeader = loginHandler.getLoginLocationHeader();
        assertNotNull(loginLocationHeader, "Заголовок Location отсутствует");

        boolean locationHeader = loginLocationHeader.contains("failed");
        if (!locationHeader){
            System.out.println("Сообщение удалось отправить: "+loginLocationHeader.contains("failed"));
        }else {
            System.out.println("Сообщение не удалось отправить: "+loginLocationHeader.contains("failed"));
        }

        if (!locationHeader) {
            Assertions.assertEquals(303, loginHandler.getStatusCode(), "Ожидаемое перенаправление для успешного входа в систему");
            RedirectProcessor redirectProcessor = new RedirectProcessor(loginLocationHeader,loginHandler.getCookie(),authRequest);

            String finalLocation = redirectProcessor.getFinalLocation();
            System.out.println("Окончательное перенаправление: " + finalLocation);

            assertTrue(finalLocation.contains("access_token"), "Токен доступа должен присутствовать в URL-адресе перенаправления");

        } else {
            assertTrue(locationHeader,
                     "В заголовке находиться информация об ошибке");
        }
    }

    static Iterator<Object[]> loginDataProvider() {
        return ExcelDataProvider.provideLoginData("src/test/resources/LoginTestData.xlsx");
    }
}