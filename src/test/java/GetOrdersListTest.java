import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void ordersListReturnOrdersArray() {
        Response response = getOrdersList(10, 0);

        checkStatusCode(response, 200);
        checkOrdersIsPresent(response);
        checkOrdersNotEmpty(response);
    }

    @Step("Отправить запрос на получение списка заказов (limit={0}, page={1})")
    public Response getOrdersList(int limit, int page) {
        return given()
                .queryParam("limit", limit)
                .queryParam("page", page)
                .get("/api/v1/orders");
    }

    @Step("Проверить код ответа: {1}")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Step("Проверить, что в ответе есть поле orders")
    public void checkOrdersIsPresent(Response response) {
        response.then().body("orders", notNullValue());
    }

    @Step("Проверить, что список orders не пустой")
    public void checkOrdersNotEmpty(Response response) {
        response.then().body("orders.size()", greaterThan(0));
    }
}
