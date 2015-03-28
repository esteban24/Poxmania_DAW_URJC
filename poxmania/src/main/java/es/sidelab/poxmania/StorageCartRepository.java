package es.sidelab.poxmania;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface StorageCartRepository extends CrudRepository <StorageCart, Long> {
	
	//Devuelve un pedido de carrito de la compra introducido un id
	@Query("FROM StorageCart s where s.id = :id")
	StorageCart findById(@Param("id")boolean id);
	
	//Devuelve un pedido de carrito de la compra introducido el nombre
	@Query("FROM StorageCart s where s.name = :name")
	StorageCart findByName(@Param("name")String name);
	
	//Devuelve un pedido de carrito de la compra introducidos los apellidos
	@Query("FROM StorageCart s where s.lastName = :lastName")
	StorageCart findByLastName(@Param("lastName")String lastName);
	
	//Devuelve una lista de carritos de la compra según estén procesados o no (true, false)
	@Query("FROM StorageCart s where s.processed = :processed")
	List<StorageCart> findByProcessedStorageCart(@Param("processed")boolean processed);
	
	
	//Modifica el estado de un pedido pasándole el id del pedido que quieras modificar y el nuevo parámetro booleano
	@Modifying
	@Transactional(readOnly=false)
	@Query("update StorageCart s set s.processed = ?2 where s.id = ?1")
	Integer setAlreadyExistingStorageCart(Long idProduct, boolean processed);
}
