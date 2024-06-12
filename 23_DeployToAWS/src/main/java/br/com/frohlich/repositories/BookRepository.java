package br.com.frohlich.repositories;

import br.com.frohlich.model.Book;
import br.com.frohlich.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
