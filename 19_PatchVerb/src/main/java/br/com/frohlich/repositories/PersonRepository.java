package br.com.frohlich.repositories;

import br.com.frohlich.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Modifying
    @Query("UPDATE Person p p.enabled = false WHERE p.id =:id")
    void disablePerson(@Param("id") Long id);
}
