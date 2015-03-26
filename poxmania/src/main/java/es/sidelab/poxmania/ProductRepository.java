package es.sidelab.poxmania;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.sidelab.poxmania.Product;

@Transactional(readOnly=true)
public interface ProductRepository extends CrudRepository<Product, Long>  {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)
	
	@Query("FROM Product p where p.id = :idProduct")
	Product findById(@Param("idProduct")Long idProduct);
	
	@Query("FROM Product p where p.name = :name")
	List<Product> findByName(@Param("name")String name);
	
	@Query("FROM Product p where p.category = :category")
	List<Product> findByCategory(@Param("category")String category);
	
	@Query("FROM Product p where p.prize between :prizeMin and :prizeMax")
	List<Product> findByPrize(@Param("prizeMin")double prizeMin, @Param("prizeMax") double prizeMax);
	
	@Modifying
	@Transactional(readOnly=false)
	@Query("update Product p set p.name = ?2, p.category = ?3, p.image = ?4, p.description = ?5, p.prize = ?6 where p.id = ?1")
	Integer setAlreadyExistingProduct(Long idProduct, String name, String category, String image, String description, double prize);

}
