package es.sidelab.poxmania;

import org.springframework.data.repository.CrudRepository;

import es.sidelab.poxmania.Television;

public interface TelevisionRepository extends CrudRepository<Television, Long>  {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)

}