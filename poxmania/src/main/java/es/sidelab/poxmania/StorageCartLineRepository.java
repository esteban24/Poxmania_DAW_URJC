package es.sidelab.poxmania;

import org.springframework.data.repository.CrudRepository;

import es.sidelab.poxmania.StorageCartLine;

public interface StorageCartLineRepository extends CrudRepository<StorageCartLine, Long>  {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)

}
