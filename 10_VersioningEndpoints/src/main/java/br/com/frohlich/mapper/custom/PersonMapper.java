package br.com.frohlich.mapper.custom;

import java.util.Date;

import org.springframework.stereotype.Service;

import br.com.frohlich.data.vo.v2.PersonVOV2;
import br.com.frohlich.model.Person;

@Service
public class PersonMapper {

	public PersonVOV2 convertEntityToVo(Person person) {

		PersonVOV2 vo = new PersonVOV2();
		vo.setId(person.getId());
		vo.setAddress(person.getAddress());
		vo.setBirthday(new Date());
		vo.setFirstName(person.getFirstName());
		vo.setLastName(person.getLastName());
		vo.setGender(person.getGender());

		return vo;
	}

	// converter personVo para entidade
	public Person convertVoToEntity(PersonVOV2 person) {

		Person entity = new Person();
		entity.setId(person.getId());
		entity.setAddress(person.getAddress());
		// entity.setBirthday(new Date());
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setGender(person.getGender());

		return entity;
	}

}
