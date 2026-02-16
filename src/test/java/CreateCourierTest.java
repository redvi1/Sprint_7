import org.junit.Test;
import org.junit.After;

import io.restassured.response.Response;

import api.CourierApi;
import model.Courier;
import model.CourierLogin;

import java.util.UUID;


import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest extends BaseTest {

    private final CourierApi courierApi = new CourierApi();
    private Integer courierId;

    private String uniqueLogin() {
        return "ninja_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void createNewCourier() {

        String login = uniqueLogin();
        String password = "1234";
        Courier courier = new Courier(login, password, "saske");

        Response create = courierApi.createCourier(courier);
        create.then().statusCode(201).body("ok", equalTo(true));

        courierId = courierApi.loginCourier(new CourierLogin(login, password))
                .then().statusCode(200).body("id", notNullValue())
                .extract().path("id");

    }

    @Test
    public void createCourierWithoutPassword() {

        Courier courier = new Courier(uniqueLogin(), null, "saske");

        courierApi.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void createCourierWithoutLogin() {

        Courier courier = new Courier(null, "1234", "saske");

        courierApi.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void cannotCreateTwoSameCouriers() {

        String login = uniqueLogin();
        String password = "1234";
        Courier courier = new Courier(login, password, "saske");

        courierApi.createCourier(courier).then().statusCode(201);

        courierApi.createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));

        courierId = courierApi.loginCourier(new CourierLogin(login, password))
                .then().statusCode(200)
                .extract().path("id");

    }

    // тут были степы, я их перенесла в классы в папке api

    @After
    public void tearDown() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId)
                    .then()
                    .statusCode(200);
        }
    }
}