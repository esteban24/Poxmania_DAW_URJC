package es.sidelab.poxmania;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface StorageCartRepository extends CrudRepository <StorageCart, Long> {
	
	/**
	 * Returns a StorageCart introducing its id 
	 * @param id
	 * @return
	 */
	@Query("FROM StorageCart s where s.id = :id")
	StorageCart findById(@Param("id")boolean id);
	
	/**
	 * Returns a StorageCart introducing its name
	 * @param name
	 * @return
	 */
	@Query("FROM StorageCart s where s.name = :name")
	StorageCart findByName(@Param("name")String name);
	
	/**
	 * Returns a StorageCart introducing its lastName
	 * @param lastName
	 * @return
	 */
	@Query("FROM StorageCart s where s.lastName = :lastName")
	StorageCart findByLastName(@Param("lastName")String lastName);
	
	/**
	 * Returns a StorageCart list processed or not. processed=(true/false)
	 * @param processed
	 * @return
	 */
	@Query("FROM StorageCart s where s.processed = :processed")
	List<StorageCart> findByProcessedStorageCart(@Param("processed")boolean processed);
	
	
	/**
	 * Modifies the state of processed or not processed from the id of a StorageCart passed as parameter
	 * @param idProduct
	 * @param processed
	 * @return
	 */
	@Modifying
	@Transactional(readOnly=false)
	@Query("update StorageCart s set s.processed = ?2 where s.id = ?1")
	Integer setAlreadyExistingStorageCart(Long idProduct, boolean processed);
}
