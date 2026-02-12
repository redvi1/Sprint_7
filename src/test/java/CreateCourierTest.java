import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private String login;
    private String password;
    private String firstName;
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        login = "ninja" + new Random().nextInt(1000000);
        password = "1234";
        firstName = "saske";
    }

    @Test
    public void createNewCourier() {

        String body = "{ \"login\": \"" + login + "\", " + "\"password\": \"" + password + "\", " + "\"firstName\": \"" + firstName + "\" }";

        Response response = sendCreateCourier(body);

        checkStatusCode(response, 201);
        checkOkTrue(response);

        courierId = loginCourier(login, password);
    }

    @Test
    public void createCourierWithoutLogin() {

        String body = "{ \"password\": \"" + password + "\", " +
                "\"firstName\": \"" + firstName + "\" }";

        Response response = sendCreateCourier(body);

        checkStatusCode(response, 400);
        checkMessage(response, "Недостаточно данных для создания учетной записи");
    }

    @Test
    public void cannotCreateTwoSameCouriers() {

        String body = "{ \"login\": \"" + login + "\", " +
                "\"password\": \"" + password + "\", " +
                "\"firstName\": \"" + firstName + "\" }";

        Response firstResponse = sendCreateCourier(body);
        checkStatusCode(firstResponse, 201);

        courierId = loginCourier(login, password);

        Response secondResponse = sendCreateCourier(body);
        checkStatusCode(secondResponse, 409);
        checkMessage(secondResponse, "Этот логин уже используется");
    }



    @Step("Отправить запрос на создание курьера")
    public Response sendCreateCourier(String body) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier");
    }

    @Step("Авторизоваться данными курьера")
    public Integer loginCourier(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .path("id");
    }

    @Step("Удалить курьера")
    public void deleteCourier(Integer id) {
        given()
                .delete("/api/v1/courier/" + id);
    }

    @Step("Проверить код ответа")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Step("Проверить поле ok")
    public void checkOkTrue(Response response) {
        response.then().body("ok", equalTo(true));
    }

    @Step("Проверить сообщение ошибки")
    public void checkMessage(Response response, String message) {
        response.then().body("message", equalTo(message));
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
            courierId = null;
        }
    }
}
