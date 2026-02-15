import org.junit.Test;

import io.restassured.response.Response;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.hamcrest.Matchers.notNullValue;

import api.OrderApi;
import classes.Order;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest {

    private final List<String> colors;
    private final OrderApi orderApi = new OrderApi();

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

    @Test
    public void canCreateOrderWithDifferentColors() {
        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+78003553535",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );

        Response response = orderApi.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    // тут были степы, я их перенесла в классы в папке api

}
