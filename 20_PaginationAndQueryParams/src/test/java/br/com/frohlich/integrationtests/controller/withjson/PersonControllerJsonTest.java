package br.com.frohlich.integrationtests.controller.withjson;

import br.com.frohlich.configs.TestConfigs;
import br.com.frohlich.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.frohlich.integrationtests.vo.AccountCredentialsVO;
import br.com.frohlich.integrationtests.vo.PersonVO;
import br.com.frohlich.integrationtests.vo.TokenVO;
import br.com.frohlich.integrationtests.vo.wrappers.WrapperPersonVO;
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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static PersonVO person;


    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        person = new PersonVO();
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
    public void testCreate() throws IOException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO foundPersonOne = objectMapper.readValue(content, PersonVO.class);
        person = foundPersonOne;

        assertNotNull(foundPersonOne);
        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getGender());
        assertTrue(foundPersonOne.getEnabled());

        assertTrue(foundPersonOne.getId() > 0);

        assertEquals("Richard", foundPersonOne.getFirstName());
        assertEquals("Stallman", foundPersonOne.getLastName());
        assertEquals("New York City, New York, US", foundPersonOne.getAddress());
        assertEquals("Male", foundPersonOne.getGender());

    }


    @Test
    @Order(2)
    public void testUpdate() throws IOException {
        person.setLastName("Torvalds");

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO foundPersonOne = objectMapper.readValue(content, PersonVO.class);
        person = foundPersonOne;

        assertNotNull(foundPersonOne);
        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getGender());
        assertTrue(foundPersonOne.getEnabled());

        assertEquals(person.getId(), foundPersonOne.getId());

        assertEquals("Richard", foundPersonOne.getFirstName());
        assertEquals("Torvalds", foundPersonOne.getLastName());
        assertEquals("New York City, New York, US", foundPersonOne.getAddress());
        assertEquals("Male", foundPersonOne.getGender());

    }

    @Test
    @Order(3)
    public void testDisablePersonById() throws IOException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO foundPersonOne = objectMapper.readValue(content, PersonVO.class);
        person = foundPersonOne;

        assertNotNull(foundPersonOne);

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());
        assertFalse(foundPersonOne.getEnabled());

        assertEquals(person.getId(), foundPersonOne.getId());

        assertEquals("Richard", foundPersonOne.getFirstName());
        assertEquals("Torvalds", foundPersonOne.getLastName());
        assertEquals("New York City, New York, US", foundPersonOne.getAddress());
        assertEquals("Male", foundPersonOne.getGender());
    }


    @Test
    @Order(4)
    public void testFindById() throws IOException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO foundPersonOne = objectMapper.readValue(content, PersonVO.class);
        person = foundPersonOne;

        assertNotNull(foundPersonOne);

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());
        assertFalse(foundPersonOne.getEnabled());

        assertEquals(person.getId(), foundPersonOne.getId());

        assertEquals("Richard", foundPersonOne.getFirstName());
        assertEquals("Torvalds", foundPersonOne.getLastName());
        assertEquals("New York City, New York, US", foundPersonOne.getAddress());
        assertEquals("Male", foundPersonOne.getGender());
    }

    @Test
    @Order(5)
    public void testDelete() throws IOException {
        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testFindByName() throws IOException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .accept(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("firstName", "ana")
                .queryParam("page", 0, "size", 6, "direction", "ASC")
                .when()
                .get("findPeopleByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
        var people = wrapper.getEmbedded().getPeople();

        PersonVO foundPersonOne = people.getFirst();
        person = foundPersonOne;

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getGender());
        assertFalse(foundPersonOne.getEnabled());


        System.out.println(foundPersonOne.getFirstName());
        assertEquals(602, foundPersonOne.getId());

        assertEquals("Alanah", foundPersonOne.getFirstName());
        assertEquals("Hulke", foundPersonOne.getLastName());
        assertEquals("9 Melody Road", foundPersonOne.getAddress());
        assertEquals("Female", foundPersonOne.getGender());
    }

    @Test
    @Order(7)
    public void testFindAll() throws IOException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .queryParam("page", 3, "size", 10, "direction", "ASC")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
        var people = wrapper.getEmbedded().getPeople();


        PersonVO foundPersonOne = people.getFirst();
        person = foundPersonOne;

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getGender());
        assertFalse(foundPersonOne.getEnabled());

        assertEquals(272, foundPersonOne.getId());

        assertEquals("Alysa", foundPersonOne.getFirstName());
        assertEquals("Furley", foundPersonOne.getLastName());
        assertEquals("92 Thackeray Crossing", foundPersonOne.getAddress());
        assertEquals("Female", foundPersonOne.getGender());


        PersonVO foundPersonThree = people.get(2);
        person = foundPersonThree;

        assertNotNull(foundPersonThree.getId());
        assertNotNull(foundPersonThree.getAddress());
        assertNotNull(foundPersonThree.getFirstName());
        assertNotNull(foundPersonThree.getLastName());
        assertNotNull(foundPersonThree.getGender());
        assertFalse(foundPersonThree.getEnabled());

        assertEquals(152, foundPersonThree.getId());

        assertEquals("Anabelle", foundPersonThree.getFirstName());
        assertEquals("Souten", foundPersonThree.getLastName());
        assertEquals("45163 Fairview Plaza", foundPersonThree.getAddress());
        assertEquals("Female", foundPersonThree.getGender());

    }

    @Test
    @Order(8)
    public void testFindAllWithoutToken() throws IOException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
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


    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
        person.setEnabled(true);
    }
}



