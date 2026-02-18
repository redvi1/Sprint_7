import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.Before;

public class BaseTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .setRelaxedHTTPSValidation()
                .build();
    }
}