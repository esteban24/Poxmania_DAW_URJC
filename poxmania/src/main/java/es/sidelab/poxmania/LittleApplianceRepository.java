package es.sidelab.poxmania;

import org.springframework.data.repository.CrudRepository;

import es.sidelab.poxmania.LittleAppliance;

public interface LittleApplianceRepository extends CrudRepository<LittleAppliance, Long>  {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)

}