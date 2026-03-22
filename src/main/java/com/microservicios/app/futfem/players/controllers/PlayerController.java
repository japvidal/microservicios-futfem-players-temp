package com.microservicios.app.futfem.players.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.app.common.controllers.CommonController;
import com.microservicios.app.futfem.players.models.entity.Player;
import com.microservicios.app.futfem.players.services.PlayerService;

@RestController
public class PlayerController extends CommonController<Player, PlayerService>{
	
	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@RequestBody Player player, @PathVariable Long id){
		Optional<Player> o = service.findById(id);
		
		if (!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Player playerDb = o.get();
		playerDb.setName(player.getName());
		playerDb.setSurname(player.getSurname());
		playerDb.setNickname(player.getNickname());
		playerDb.setCountry(player.getCountry());
		playerDb.setPosition(player.getPosition());
		playerDb.setBirthdate(player.getBirthdate());
		playerDb.setUrlpic(player.getUrlpic());
		playerDb.setVelocidad(player.getVelocidad());
		playerDb.setResistencia(player.getResistencia());
		playerDb.setDefaerea(player.getDefaerea());
		playerDb.setDefagresividad(player.getDefagresividad());
		playerDb.setDefanticipacion(player.getDefanticipacion());
		playerDb.setDefestrategia(player.getDefestrategia());
		playerDb.setVisionjuego(player.getVisionjuego());
		playerDb.setAtaqueaereo(player.getAtaqueaereo());
		playerDb.setAtaquefaltas(player.getAtaquefaltas());
		playerDb.setAtaquepases(player.getAtaquepases());
		playerDb.setAtaquepenaltys(player.getAtaquepenaltys());
		playerDb.setAtaqueregate(player.getAtaqueregate());
		playerDb.setAtaqueremate(player.getAtaqueremate());
		playerDb.setPortero(player.getPortero());
		
		// service.save(alumnoDb) permite persistir el alumno con los datos editados
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(playerDb));  // HTTPStatus 201
	}
	
}
