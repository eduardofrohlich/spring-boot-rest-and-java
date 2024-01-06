package br.com.frohlich.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.frohlich.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
