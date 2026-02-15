import org.junit.Test;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.notNullValue;

import api.OrderApi;

public class GetOrdersListTest extends BaseTest {

    private final OrderApi orderApi = new OrderApi();

    @Test
    public void ordersListReturnOrdersArray() {
        Response response = orderApi.getOrdersList();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    // тут были степы, я их перенесла в классы в папке api

}
