package es.sidelab.poxmania;

import org.springframework.data.repository.CrudRepository;

import es.sidelab.poxmania.Informatic;

public interface InformaticRepository extends CrudRepository<Informatic, Long>  {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)

}
