package br.com.frohlich.integrationtests.controller.withjson;

import br.com.frohlich.configs.TestConfigs;
import br.com.frohlich.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.frohlich.integrationtests.vo.AccountCredentialsVO;
import br.com.frohlich.integrationtests.vo.BookVO;
import br.com.frohlich.integrationtests.vo.TokenVO;
import br.com.frohlich.integrationtests.vo.wrappers.WrapperBookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonMappingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookVO book;


    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        book = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
        //access token
        var token = given().basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenVO.class)
                .getToken();
        specification.header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token);
    }

    @Test
    @Order(1)
    public void testCreate() throws IOException, ParseException {
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        book = objectMapper.readValue(content, BookVO.class);

        assertNotNull(book);
        assertNotNull(book.getKey());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getTitle());
        assertNotNull(book.getPrice());

        assertTrue(book.getKey() > 0);

        assertEquals("Michael C. Feathers", book.getAuthor());
        assertEquals("Working effectively with legacy code", book.getTitle());
        assertEquals(49.00, book.getPrice());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(dateFormat.parse("2017-11-28"), book.getLaunchDate());
    }


    @Test
    @Order(2)
    public void testUpdate() throws IOException {
        book.setTitle("Working effectively with legacy code - Updated");

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(book)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO bookUpdated = objectMapper.readValue(content, BookVO.class);

        assertNotNull(bookUpdated);
        assertNotNull(bookUpdated.getKey());
        assertNotNull(bookUpdated.getAuthor());
        assertNotNull(bookUpdated.getLaunchDate());
        assertNotNull(bookUpdated.getTitle());
        assertNotNull(bookUpdated.getPrice());

        assertEquals(book.getKey(), bookUpdated.getKey());

        assertEquals("Working effectively with legacy code - Updated", bookUpdated.getTitle());
        assertEquals("Michael C. Feathers", bookUpdated.getAuthor());
        assertEquals(49.0, bookUpdated.getPrice());

    }


    @Test
    @Order(3)
    public void testFindById() throws IOException {
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", book.getKey())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        BookVO foundBookOne = objectMapper.readValue(content, BookVO.class);

        assertNotNull(foundBookOne);
        assertNotNull(foundBookOne.getKey());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getPrice());

        assertEquals(book.getKey(), foundBookOne.getKey());

        assertEquals("Working effectively with legacy code - Updated", foundBookOne.getTitle());
        assertEquals("Michael C. Feathers", foundBookOne.getAuthor());
        assertEquals(49.00, foundBookOne.getPrice());
    }

    @Test
    @Order(4)
    public void testDelete() throws IOException {
        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", book.getKey())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    public void testFindAll() throws IOException, ParseException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .queryParam("direction", "desc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);

        assertNotNull(wrapper);
        assertNotNull(wrapper.getEmbedded());

        var books = wrapper.getEmbedded().getbooks();
        assertNotNull(books);
        assertFalse(books.isEmpty());

        BookVO foundBookOne = books.getFirst();
        book = foundBookOne;

        assertNotNull(foundBookOne.getKey());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getLaunchDate());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getPrice());

        assertEquals(2, foundBookOne.getKey());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(dateFormat.parse("2017-11-29"), foundBookOne.getLaunchDate());
        assertEquals("Design Patterns", foundBookOne.getTitle());
        assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", foundBookOne.getAuthor());
        assertEquals(45.0, foundBookOne.getPrice());

        BookVO foundBookThree = books.get(2);
        book = foundBookThree;

        assertNotNull(foundBookThree.getKey());
        assertNotNull(foundBookThree.getAuthor());
        assertNotNull(foundBookThree.getLaunchDate());
        assertNotNull(foundBookThree.getTitle());
        assertNotNull(foundBookThree.getPrice());

        assertEquals(3, foundBookThree.getKey());

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(dateFormat.parse("2009-01-10"), foundBookThree.getLaunchDate());
        assertEquals("Clean Code", foundBookThree.getTitle());
        assertEquals("Robert C. Martin", foundBookThree.getAuthor());
        assertEquals(77.0, foundBookThree.getPrice());

    }

    @Test
    @Order(6)
    public void testFindAllWithoutToken() throws IOException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
        given().spec(specificationWithoutToken)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();
    }

    @Test
    @Order(7)
    public void testHATEOAS() throws IOException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .queryParam("direction", "desc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        System.out.println("Response content: " + content);

        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/12\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/2\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/5\"}}}"));

        assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":14,\"totalPages\":2,\"number\":1}}"));
        assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/book/v1?page=1&size=10&direction=asc\"}"));
        assertTrue(content.contains("\"first\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=10&sort=title,desc\"}"));
        assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=10&sort=title,desc\"}}"));
        assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=10&sort=title,desc\"}"));
    }


    private void mockBook() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = dateFormat.parse("2017-11-29");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        book.setKey(1L);
        book.setPrice(49.00);
        book.setLaunchDate(date);
        book.setAuthor("Michael C. Feathers");
        book.setTitle("Working effectively with legacy code");
    }

}



