package br.com.frohlich.services;

import br.com.frohlich.controllers.PersonController;
import br.com.frohlich.data.vo.v1.PersonVO;
import br.com.frohlich.exceptions.RequiredObjectIsNullException;
import br.com.frohlich.exceptions.ResourceNotFoundException;
import br.com.frohlich.mapper.DozerMapper;
import br.com.frohlich.model.Person;
import br.com.frohlich.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {

    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    public List<PersonVO> findAll() {
        logger.info("Finding all people!");
        var people = DozerMapper.parseListObjects(repository.findAll(),
                PersonVO.class);
        people.stream().forEach(p -> {
            try {
                p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return people;
    }

    public PersonVO findById(Long id) throws Exception {
        logger.info("Finding one person!");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        //link hateoas:
        //methodON qual class e qual o nome do metodo
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person) throws Exception {

        if (person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");
        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) throws Exception {

        if (person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one person!");
        var entity = repository.findById(person.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id) throws Exception {
        logger.info("Disabling one person!");

        repository.disablePerson(id);

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting one person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

}
