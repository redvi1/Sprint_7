package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import classes.Courier;
import classes.CourierLogin;

import static io.restassured.RestAssured.given;

public class CourierApi {

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {
        return given()
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierLogin courierLogin) {
        return given()
                .body(courierLogin)
                .post("/api/v1/courier/login");
    }

    @Step("Удалить курьера по id")
    public Response deleteCourier(int courierId) {
        return given()
                .delete("/api/v1/courier/" + courierId);
    }
}
