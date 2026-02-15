import org.junit.Test;

import io.restassured.response.Response;

import api.CourierApi;
import classes.Courier;
import classes.CourierLogin;

import java.util.UUID;


import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest extends BaseTest {

    private final CourierApi courierApi = new CourierApi();

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

        int courierId = courierApi.loginCourier(new CourierLogin(login, password))
                .then().statusCode(200).body("id", notNullValue())
                .extract().path("id");

        courierApi.deleteCourier(courierId).then().statusCode(200).body("ok", equalTo(true));
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

        int courierId = courierApi.loginCourier(new CourierLogin(login, password))
                .then().statusCode(200)
                .extract().path("id");

        courierApi.deleteCourier(courierId).then().statusCode(200);
    }

    // тут были степы, я их перенесла в классы в папке api
}
