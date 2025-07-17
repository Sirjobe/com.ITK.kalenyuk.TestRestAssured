package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.models.Project;
import com.ITK.kalenyuk.service.AuthInitiator;
import com.ITK.kalenyuk.service.TaskAndProjects;
import com.ITK.kalenyuk.utils.ConfigLoader;
import com.ITK.kalenyuk.utils.TestDataProvider;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ApiTest {
    private static final String VALID_TOKEN = ConfigLoader.getProperty("token");
    private static final String PROJECT_NAME = "QA";

    static Stream<Arguments> testDataProvider() {
        return TestDataProvider.loadTestData("testdata.csv").stream()
                .map(data -> Arguments.of(
                        data[0], // scenario
                        data[1], // token
                        data[2], // project
                        data[3], // summary
                        data[4], // description
                        Integer.parseInt(data[5]) // expectedStatus
                ));
    }

    @DisplayName("Проверка авторизации с валидным токеном")
    @Test
    public void testValidOAuth2Authorization() {
        AuthInitiator auth = new AuthInitiator(VALID_TOKEN);

        assertEquals(200, auth.getStatusCode(), "Статус-код не 200");
        assertNotNull(auth.getAuthResponse(), "Ответ авторизации не получен");
        assertFalse(auth.getAuthResponse().isGuest(), "Авторизован как гость");
        assertEquals(ConfigLoader.getProperty("name"), auth.getAuthResponse().getName(), "Имя пользователя не соответствует");
    }

    @DisplayName("Негативные тесты авторизации")
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("testDataProvider")
    public void testAuthScenarios(String scenario, String token, String project, String summary, String description, int expectedStatus) {
        AuthInitiator auth = new AuthInitiator(token);
        if (auth.getStatusCode() == 200) {
            assumeTrue(false, "Пропуск: не удалось получить проекты");
        }
        assertEquals(expectedStatus, auth.getStatusCode(), "Неверный статус-код для сценария: " + scenario);

    }

    @DisplayName("Проверка получения списка проектов")
    @Test
    public void testGetProjects() {
        TaskAndProjects service = new TaskAndProjects(VALID_TOKEN);

        assertEquals(200, service.getStatusCode(), "Статус-код не 200");
        assertNotNull(service.getProjects(), "Список проектов не получен");
        assertTrue(service.getProjects().stream().anyMatch(p -> PROJECT_NAME.equals(p.getName())), "Проект 'QA' не найден");

        Project qaProject = service.getProjects().stream()
                .filter(p -> PROJECT_NAME.equals(p.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(qaProject, "Проект 'QA' не найден");
        assertNotNull(qaProject.getId(), "ID проекта 'QA' не найден");
        System.out.println("ID проекта 'QA': " + qaProject.getId());
    }

    @DisplayName("Тесты создания задач")
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("testDataProvider")
    public void testCreateTaskScenarios(String scenario, String token, String projectName, String summary, String description, int expectedStatus) {
        // Для сценариев без проектов пропускаем получение проектов
        if (!"invalid_project".equals(scenario)) {
            TaskAndProjects service = new TaskAndProjects(token);
            if (service.getStatusCode() != 200) {
                assumeTrue(false, "Пропуск: не удалось получить проекты");
            }

            String projectId = service.getProjects().stream()
                    .filter(p -> projectName.equals(p.getName()))
                    .findFirst()
                    .map(Project::getId)
                    .orElse(null);

            if (projectId == null) {
                assumeTrue(false, "Пропуск: проект '" + projectName + "' не найден");
            }

            Response response = service.createTask(token, projectId, summary, description);
            assertEquals(expectedStatus, response.getStatusCode(), "Неверный статус-код для сценария: " + scenario);

            if (response.getStatusCode() == 200) {
                assertNotNull(service.getCreatedIssue(), "Задача не создана");
                assertNotNull(service.getCreatedIssue().getId(), "ID задачи не получен");

                // Очистка
                Response deleteResponse = service.deleteTask(token, service.getCreatedIssue().getId());
                assertEquals(200, deleteResponse.getStatusCode(), "Ошибка удаления задачи");
            }
        } else {
            // Для сценария с невалидным проектом
            Response response = new TaskAndProjects(token).createTask(token, "invalid_project_id", summary, description);
            assertEquals(expectedStatus, response.getStatusCode(), "Неверный статус-код для сценария: " + scenario);
        }
    }

    @DisplayName("Полный жизненный цикл задачи")
    @Test
    public void testFullTaskLifecycle() {
        TaskAndProjects service = new TaskAndProjects(VALID_TOKEN);
        assumeTrue(service.getStatusCode() == 200, "Провал: не удалось получить проекты");
        assumeTrue(service.getProjectId() != null, "Провал: проект 'QA' не найден");

        // Создание задачи
        String summary = "Lifecycle Test " + System.currentTimeMillis();
        Response createResponse = service.createTask(VALID_TOKEN, service.getProjectId(), summary, "Test Description");
        assertEquals(200, createResponse.getStatusCode(), "Ошибка создания задачи");
        String taskId = service.getCreatedIssue().getId();

        // Проверка создания
        Response getResponse = service.getTaskById(VALID_TOKEN, service.getProjectId(), taskId);
        assertEquals(200, getResponse.getStatusCode(), "Задача не найдена после создания");

        // Удаление задачи
        Response deleteResponse = service.deleteTask(VALID_TOKEN, taskId);
        assertEquals(200, deleteResponse.getStatusCode(), "Ошибка удаления задачи");

        // Проверка удаления
        Response getAfterDelete = service.getTaskById(VALID_TOKEN, service.getProjectId(), taskId);
        assertNotEquals(200, getAfterDelete.getStatusCode(), "Задача все еще доступна после удаления");
    }
}