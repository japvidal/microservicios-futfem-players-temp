package com.microservicios.app.futfem.players.services.dto;

import java.util.List;

import com.microservicios.app.futfem.players.models.entity.Player;

public record PlayerPageResponse(
		List<Player> content,
		long totalElements,
		int totalPages,
		int page,
		int size) {
}
