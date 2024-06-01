package br.com.frohlich.integrationtests.swagger;

import br.com.frohlich.configs.TestConfigs;
import br.com.frohlich.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.RestAssured;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shouldDisplaySwaggerUiPage() {

       var content = RestAssured.given().basePath("/swagger-ui/index.html")
                .port(TestConfigs.SERVER_PORT)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertTrue(content.contains("Swagger UI"));

    }

}

