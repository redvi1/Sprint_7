import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;
import io.qameta.allure.Step;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final List<String> colors;

    public CreateOrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "colors={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { List.of("BLACK") },
                { List.of("GREY") },
                { List.of("BLACK", "GREY") },
                { null }
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void canCreateOrderWithDifferentColors() {
        Map<String, Object> body = buildOrderBody(colors);

        Response response = createOrder(body);

        checkStatusCode(response, 201);
        checkTrackNotNull(response);
    }

    @Step("Собрать тело запроса на создание заказа")
    public Map<String, Object> buildOrderBody(List<String> colors) {
        int uniq = new Random().nextInt(90) + 10;


        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Naruto" + uniq);
        body.put("lastName", "Uchiha");
        body.put("address", "Konoha, 142 apt.");
        body.put("metroStation", "4");
        body.put("phone", "+7 800 355 35 " + uniq);
        body.put("rentTime", 5);
        body.put("deliveryDate", "2020-06-06");
        body.put("comment", "Saske, come back to Konoha");

        if (colors != null) {
            body.put("color", colors);
        }

        return body;
    }

    @Step("Отправить запрос на создание заказа")
    public Response createOrder(Map<String, Object> body) {
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .post("/api/v1/orders");
    }

    @Step("Проверить код ответа")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Step("Проверить, что в ответе есть track")
    public void checkTrackNotNull(Response response) {
        response.then().body("track", notNullValue());
    }
}
