package br.com.frohlich.mapper;

import java.util.ArrayList;
import java.util.List;

import br.com.frohlich.data.vo.v1.BookVO;
import br.com.frohlich.data.vo.v1.PersonVO;
import br.com.frohlich.model.Book;
import br.com.frohlich.model.Person;
import org.modelmapper.ModelMapper;

//import com.github.dozermapper.core.DozerBeanMapperBuilder;
//import com.github.dozermapper.core.Mapper;

public class DozerMapper {

    //private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    private static ModelMapper mapper = new ModelMapper();

    static {
        mapper.createTypeMap(Person.class, PersonVO.class).addMapping(Person::
                getId, PersonVO::setKey);

        mapper.createTypeMap(PersonVO.class, Person.class).
                addMapping(PersonVO::
                        getKey, Person::setId);

        mapper.createTypeMap(Book.class, BookVO.class).addMapping(Book::
                getId, BookVO::setKey);

        mapper.createTypeMap(BookVO.class, Book.class).
                addMapping(BookVO::
                        getKey, Book::setId);
    }

    //conversao
    //Origem, destino, retornando destino

    public static <O, D> D parseObject(O origin, Class<D> destination) {
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination) {
        List<D> destinationObjects = new ArrayList<D>();

        //converte item por item em objeto de destino
        for (O o : origin) {
            destinationObjects.add(mapper.map(o, destination));
        }
        return destinationObjects;
    }

}
