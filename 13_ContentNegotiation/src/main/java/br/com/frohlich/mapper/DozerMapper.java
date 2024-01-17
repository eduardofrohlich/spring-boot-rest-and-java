package br.com.frohlich.mapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

//import com.github.dozermapper.core.DozerBeanMapperBuilder;
//import com.github.dozermapper.core.Mapper;

public class DozerMapper {
	
	//private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	private static ModelMapper mapper = new ModelMapper();
	
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
