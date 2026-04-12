package com.microservicios.app.futfem.players.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.app.common.controllers.CommonController;
import com.microservicios.app.futfem.players.controllers.dto.PlayerLookupRequest;
import com.microservicios.app.futfem.players.models.entity.Player;
import com.microservicios.app.futfem.players.services.PlayerService;
import com.microservicios.app.futfem.players.services.dto.PlayerSearchRequest;

@RestController
public class PlayerController extends CommonController<Player, PlayerService>{

	@PostMapping("/getIdByName")
	public ResponseEntity<?> getIdByName(@RequestBody PlayerLookupRequest request) {
		Optional<Player> player = service.findByNameSurnameAndBirthdate(
			request.getName(),
			request.getSurname(),
			request.getNickname(),
			request.getBirthdate()
		);

		if (player.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(player.get().getId());
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) PlayerSearchRequest request) {
		return ResponseEntity.ok(service.searchPlayers(request == null ? new PlayerSearchRequest() : request));
	}

	@PostMapping("/")
	public ResponseEntity<?> crear(@RequestBody Player player) {
		Optional<Player> existing = service.findByNameSurnameAndBirthdate(
			player.getName(),
			player.getSurname(),
			player.getNickname(),
			null
		);

		if (existing.isPresent()) {
			Player playerDb = existing.get();
			boolean sameBirthdate = player.getBirthdate() == null
					|| playerDb.getBirthdate() == null
					|| playerDb.getBirthdate().equals(player.getBirthdate());
			if (sameBirthdate) {
				boolean updated = mergeImportedPlayerData(playerDb, player);
				return ResponseEntity.ok(updated ? service.save(playerDb) : playerDb);
			}
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(player));
	}
	
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

	private boolean mergeImportedPlayerData(Player target, Player source) {
		boolean updated = false;

		if (!isBlank(source.getCountry()) && !sameValue(target.getCountry(), source.getCountry())) {
			target.setCountry(source.getCountry());
			updated = true;
		}
		if (isBlank(target.getNickname()) && !isBlank(source.getNickname())) {
			target.setNickname(source.getNickname());
			updated = true;
		}
		if (!isBlank(source.getPosition()) && !sameValue(target.getPosition(), source.getPosition())) {
			target.setPosition(source.getPosition());
			updated = true;
		}
		if (target.getBirthdate() == null && source.getBirthdate() != null) {
			target.setBirthdate(source.getBirthdate());
			updated = true;
		}

		return updated;
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private boolean sameValue(String left, String right) {
		return !isBlank(left) && !isBlank(right) && left.equalsIgnoreCase(right);
	}
	
}
