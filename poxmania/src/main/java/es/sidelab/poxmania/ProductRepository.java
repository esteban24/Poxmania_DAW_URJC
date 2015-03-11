package es.sidelab.poxmania;

import org.springframework.data.repository.CrudRepository;

import es.sidelab.poxmania.Product;

public interface ProductRepository extends CrudRepository<Product, Long>  {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)

}
