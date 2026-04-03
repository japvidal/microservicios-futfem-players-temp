package com.microservicios.app.futfem.players.services;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.microservicios.app.common.services.CommonServiceImpl;
import com.microservicios.app.futfem.players.models.entity.Player;
import com.microservicios.app.futfem.players.models.repository.PlayerRepository;

@Service
public class PlayerServiceImpl extends CommonServiceImpl<Player, PlayerRepository> implements PlayerService {

	private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
	private static final DateTimeFormatter BIRTHDATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public Optional<Player> findByNameSurnameAndBirthdate(String name, String surname, String birthdate) {
		String normalizedName = normalize(name);
		String normalizedSurname = normalize(surname);
		String normalizedBirthdate = normalize(birthdate);

		if (normalizedName == null) {
			return Optional.empty();
		}

		if (normalizedBirthdate == null) {
			return repository.findByNameSurnameAndBirthdateIsNull(normalizedName, normalizedSurname);
		}

		try {
			LocalDate parsedBirthdate = LocalDate.parse(normalizedBirthdate, BIRTHDATE_FORMATTER);
			return repository.findByNameSurnameAndBirthdate(normalizedName, normalizedSurname, Date.valueOf(parsedBirthdate));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}

	private String normalize(String value) {
		log.info("Init method PlayerServiceImpl.normalize");
		if (value == null) {
			return null;
		}
		String normalized = value.trim().replaceAll("\\s+", " ");
		return normalized.isEmpty() ? null : normalized;
	}
}
