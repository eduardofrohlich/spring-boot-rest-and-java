package br.com.frohlich.integrationtests.vo.wrappers;

import br.com.frohlich.integrationtests.vo.PersonVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class PersonEmbeddedVO {

    @JsonProperty("personVOList")
    private List<PersonVO> people;

    public PersonEmbeddedVO() {
    }

    public List<PersonVO> getPeople() {
        return people;
    }

    public void setPeople(List<PersonVO> people) {
        this.people = people;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEmbeddedVO that = (PersonEmbeddedVO) o;
        return Objects.equals(people, that.people);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(people);
    }
}
