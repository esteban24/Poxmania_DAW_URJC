package es.sidelab.poxmania;

import org.springframework.data.repository.CrudRepository;

import es.sidelab.poxmania.VideoGames;

public interface VideoGamesRepository extends CrudRepository<VideoGames, Long>  {
	//TODO emartin: Hacer las consultas a BBDD (h2 y MySql)

}
