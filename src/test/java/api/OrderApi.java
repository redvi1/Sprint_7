package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderApi {

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return given()
                .body(order)
                .post("/api/v1/orders");
    }

    @Step("Получить список заказов")
    public Response getOrdersList() {
        return given()
                .get("/api/v1/orders");
    }

    @Step("Отменить заказ по track")
    public Response cancelOrder(int track) {
        return given()
                .queryParam("track", track)
                .put("/api/v1/orders/cancel");
    }
}
