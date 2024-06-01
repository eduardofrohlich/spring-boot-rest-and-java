package br.com.frohlich.integrationtests.repositories;

import br.com.frohlich.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.frohlich.model.Person;
import br.com.frohlich.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    public PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup() {
        person = new Person();
    }

    @Test
    @Order(0)
    public void testFindByName() throws IOException {

        Pageable pageable = PageRequest.of(0, 6,
                Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPeopleByName("ana", pageable).getContent().getFirst();

        assertNotNull(person.getId());
        assertNotNull(person.getAddress());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getGender());
        assertFalse(person.getEnabled());

        assertEquals(602, person.getId());
        assertEquals("Alanah", person.getFirstName());
        assertEquals("Hulke", person.getLastName());
        assertEquals("9 Melody Road", person.getAddress());
        assertEquals("Female", person.getGender());
    }

    @Test
    @Order(1)
    public void testDisablePersonById() throws IOException {

        repository.disablePerson(person.getId());
        Pageable pageable = PageRequest.of(0, 6,
                Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPeopleByName("ana", pageable).getContent().getFirst();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertFalse(person.getEnabled());

        assertEquals(602, person.getId());
        assertEquals("Alanah", person.getFirstName());
        assertEquals("Hulke", person.getLastName());
        assertEquals("9 Melody Road", person.getAddress());
        assertEquals("Female", person.getGender());
    }


}
