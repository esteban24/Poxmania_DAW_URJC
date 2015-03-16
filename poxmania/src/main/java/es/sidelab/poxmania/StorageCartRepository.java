package es.sidelab.poxmania;

import org.springframework.data.repository.CrudRepository;

public interface StorageCartRepository extends CrudRepository <StorageCart, Long> {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)
}
