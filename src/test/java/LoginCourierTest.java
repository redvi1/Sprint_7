import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginCourierTest {

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

        String createBody = "{ \"login\": \"" + login + "\", " + "\"password\": \"" + password + "\", " + "\"firstName\": \"" + firstName + "\" }";
        sendCreateCourier(createBody);

        courierId = getCourierId(login, password);
    }

    @Test
    public void courierCanLogin() {
        String body = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }";

        Response response = sendLoginCourier(body);

        checkStatusCode(response, 200);
        checkIdNotNull(response);
    }

    @Test
    public void loginNoPassword() {
        String body = "{ \"login\": \"" + login + "\" }";

        Response response = sendLoginCourier(body);

        checkStatusCode(response, 400);
        checkMessage(response, "Недостаточно данных для входа");
    }

    @Test
    public void loginNoLogin() {
        String body = "{ \"password\": \"" + password + "\" }";

        Response response = sendLoginCourier(body);

        checkStatusCode(response, 400);
        checkMessage(response, "Недостаточно данных для входа");
    }

    @Test
    public void loginWithWrongPassword() {
        String body = "{ \"login\": \"" + login + "\", \"password\": \"nothing\" }";

        Response response = sendLoginCourier(body);

        checkStatusCode(response, 404);
        checkMessage(response, "Учетная запись не найдена");
    }

    @Test
    public void loginNonExistingCourier() {
        String body = "{ \"login\": \"nothing\", \"password\": \"" + password + "\" }";

        Response response = sendLoginCourier(body);

        checkStatusCode(response, 404);
        checkMessage(response, "Учетная запись не найдена");
    }

    @Step("Создать курьера")
    public void sendCreateCourier(String body) {
        given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier");
    }

    @Step("Отправить запрос на логин курьера")
    public Response sendLoginCourier(String body) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/courier/login");
    }

    @Step("Получить id курьера")
    public Integer getCourierId(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
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

    @Step("Проверить, что в ответе есть id")
    public void checkIdNotNull(Response response) {
        response.then().body("id", notNullValue());
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
