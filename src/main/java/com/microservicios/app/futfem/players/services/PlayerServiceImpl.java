package com.microservicios.app.futfem.players.services;

import org.springframework.stereotype.Service;

import com.microservicios.app.common.services.CommonServiceImpl;
import com.microservicios.app.futfem.players.models.entity.Player;
import com.microservicios.app.futfem.players.models.repository.PlayerRepository;

@Service
public class PlayerServiceImpl  extends CommonServiceImpl<Player, PlayerRepository>  implements PlayerService {

}
