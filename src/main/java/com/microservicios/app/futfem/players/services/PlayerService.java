package com.microservicios.app.futfem.players.services;

import java.util.Optional;

import com.microservicios.app.common.services.CommonService;
import com.microservicios.app.futfem.players.models.entity.Player;

public interface PlayerService extends CommonService<Player> {

	Optional<Player> findByNameSurnameAndBirthdate(String name, String surname, String birthdate);
}
