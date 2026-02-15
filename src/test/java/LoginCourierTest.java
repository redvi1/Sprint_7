import org.junit.Test;

import io.restassured.response.Response;

import api.CourierApi;
import classes.Courier;
import classes.CourierLogin;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseTest {

    private final CourierApi courierApi = new CourierApi();

    private String uniqueLogin() {
        return "ninja_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    public void courierCanLogin() {
        String login = uniqueLogin();
        String password = "1234";

        courierApi.createCourier(new Courier(login, password, "saske"))
                .then().statusCode(201);

        int courierId = courierApi.loginCourier(new CourierLogin(login, password))
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().path("id");

        courierApi.deleteCourier(courierId).then().statusCode(200);
    }

    @Test
    public void loginNoPassword() {
        CourierLogin cred = new CourierLogin("some_login", null);

        courierApi.loginCourier(cred)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void loginNoLogin() {
        CourierLogin cred = new CourierLogin(null, "1234");

        courierApi.loginCourier(cred)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void loginWithWrongPassword() {
        String login = uniqueLogin();
        String password = "1234";

        courierApi.createCourier(new Courier(login, password, "saske"))
                .then().statusCode(201);

        Response loginResponse = courierApi.loginCourier(new CourierLogin(login, "wrong"));

        loginResponse.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));

        int courierId = courierApi.loginCourier(new CourierLogin(login, password))
                .then().statusCode(200)
                .extract().path("id");

        courierApi.deleteCourier(courierId).then().statusCode(200);
    }

    @Test
    public void loginNonExistingCourier() {
        CourierLogin cred = new CourierLogin("no_such_login", "no_such_password");

        courierApi.loginCourier(cred)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    // тут были степы, я их перенесла в классы в папке api

}
