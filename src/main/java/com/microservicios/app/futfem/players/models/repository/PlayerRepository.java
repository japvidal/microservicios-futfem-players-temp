package com.microservicios.app.futfem.players.models.repository;

import org.springframework.data.repository.CrudRepository;

import com.microservicios.app.futfem.players.models.entity.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {

	
	
}
