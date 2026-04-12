package com.microservicios.app.futfem.players.services;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.microservicios.app.common.services.CommonServiceImpl;
import com.microservicios.app.futfem.players.models.entity.Player;
import com.microservicios.app.futfem.players.models.repository.PlayerRepository;
import com.microservicios.app.futfem.players.services.dto.PlayerPageResponse;
import com.microservicios.app.futfem.players.services.dto.PlayerSearchRequest;

@Service
public class PlayerServiceImpl extends CommonServiceImpl<Player, PlayerRepository> implements PlayerService {

	private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
	private static final DateTimeFormatter BIRTHDATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final int DEFAULT_PAGE = 0;
	private static final int DEFAULT_SIZE = 25;

	@Override
	public Optional<Player> findByNameSurnameAndBirthdate(String name, String surname, String nickname, String birthdate) {
		String requestName = normalizeRaw(name);
		String requestSurname = normalizeRaw(surname);
		String requestNickname = normalizeRaw(nickname);
		if (requestName == null && requestSurname == null && requestNickname == null) {
			return Optional.empty();
		}

		Optional<LocalDate> requestedBirthdate = parseBirthdate(birthdate);

		return StreamSupport.stream(repository.findAll().spliterator(), false)
				.map(player -> rankPlayer(player, requestName, requestSurname, requestNickname, requestedBirthdate))
				.filter(PlayerMatch::isCandidate)
				.max(Comparator.comparing(PlayerMatch::birthdateMatched)
						.thenComparingInt(PlayerMatch::score)
						.thenComparingLong(match -> match.player().getId()))
				.map(PlayerMatch::player);
	}

	@Override
	public PlayerPageResponse searchPlayers(PlayerSearchRequest request) {
		log.info("Init method PlayerServiceImpl.searchPlayers");
		PlayerSearchRequest safeRequest = request == null ? new PlayerSearchRequest() : request;
		int page = safeRequest.getPage() == null || safeRequest.getPage() < 0 ? DEFAULT_PAGE : safeRequest.getPage();
		int size = safeRequest.getSize() == null || safeRequest.getSize() <= 0 ? DEFAULT_SIZE : safeRequest.getSize();

		Pageable pageable = PageRequest.of(page, size,
				Sort.by("surname").ascending()
						.and(Sort.by("name").ascending())
						.and(Sort.by("nickname").ascending()));
		Page<Player> result = repository.searchPlayers(
				normalizeSearchTerm(safeRequest.getSearch()),
				normalizeFilterValue(safeRequest.getCountry()),
				normalizeFilterValue(safeRequest.getPosition()),
				pageable);

		return new PlayerPageResponse(
				result.getContent(),
				result.getTotalElements(),
				result.getTotalPages(),
				result.getNumber(),
				result.getSize());
	}

	private PlayerMatch rankPlayer(Player player, String requestName, String requestSurname, String requestNickname,
			Optional<LocalDate> requestedBirthdate) {
		log.info("Init method PlayerServiceImpl.rankPlayer");
		String normalizedPlayerName = normalize(player.getName());
		String normalizedPlayerSurname = normalize(player.getSurname());
		String normalizedPlayerNickname = normalize(player.getNickname());
		if (normalizedPlayerName == null && normalizedPlayerSurname == null && normalizedPlayerNickname == null) {
			return new PlayerMatch(player, false, false, 0);
		}

		int officialNameScore = scoreOfficialIdentity(requestName, requestSurname, normalizedPlayerName, normalizedPlayerSurname);
		int nicknameScore = scoreNickname(requestName, requestSurname, requestNickname, normalizedPlayerNickname);
		boolean candidate = officialNameScore > 0 || nicknameScore > 0;

		if (!candidate) {
			return new PlayerMatch(player, false, false, 0);
		}

		int score = officialNameScore + nicknameScore;

		boolean birthdateMatched = requestedBirthdate
				.map(date -> matchesBirthdate(player, date))
				.orElse(false);

		return new PlayerMatch(player, true, birthdateMatched, score);
	}

	private int scoreOfficialIdentity(String requestName, String requestSurname, String normalizedPlayerName,
			String normalizedPlayerSurname) {
		log.info("Init method PlayerServiceImpl.scoreOfficialIdentity");
		if (!matchesExact(requestName, normalizedPlayerName) || !matchesExact(requestSurname, normalizedPlayerSurname)) {
			return 0;
		}

		return 250;
	}

	private int scoreNickname(String requestName, String requestSurname, String requestNickname, String normalizedPlayerNickname) {
		log.info("Init method PlayerServiceImpl.scoreNickname");
		if (normalizedPlayerNickname == null) {
			return 0;
		}

		if (matchesExact(requestSurname, normalizedPlayerNickname)) {
			return 200;
		}
		if (matchesExact(requestName, normalizedPlayerNickname)) {
			return 180;
		}
		if (matchesExact(requestNickname, normalizedPlayerNickname)) {
			return 220;
		}
		return 0;
	}

	private boolean matchesExact(String requestValue, String normalizedPlayerValue) {
		log.info("Init method PlayerServiceImpl.matchesExact");
		String normalizedRequestValue = normalize(requestValue);
		return normalizedRequestValue != null
				&& normalizedPlayerValue != null
				&& normalizedRequestValue.equals(normalizedPlayerValue);
	}

	private Optional<LocalDate> parseBirthdate(String birthdate) {
		log.info("Init method PlayerServiceImpl.parseBirthdate");
		String normalizedBirthdate = normalizeBirthdate(birthdate);
		if (normalizedBirthdate == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(LocalDate.parse(normalizedBirthdate, BIRTHDATE_FORMATTER));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}

	private String normalizeBirthdate(String value) {
		log.info("Init method PlayerServiceImpl.normalizeBirthdate");
		String normalized = normalizeRaw(value);
		return normalized == null ? null : normalized.replaceAll("\\s+", "");
	}

	private boolean matchesBirthdate(Player player, LocalDate requestedBirthdate) {
		log.info("Init method PlayerServiceImpl.matchesBirthdate");
		if (player.getBirthdate() == null) {
			return false;
		}
		LocalDate playerBirthdate = player.getBirthdate().toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		return playerBirthdate.equals(requestedBirthdate);
	}

	private String normalize(String value) {
		log.info("Init method PlayerServiceImpl.normalize: {}", value);
		String normalized = normalizeRaw(value);
		if (normalized == null) {
			return null;
		}
		String sanitized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.replaceAll("[^\\p{Alnum}]", "")
				.toLowerCase();
		return sanitized.isEmpty() ? null : sanitized;
	}

	private String normalizeRaw(String value) {
		log.info("Init method PlayerServiceImpl.normalizeRaw");
		if (value == null) {
			return null;
		}
		String normalized = value.trim().replaceAll("\\s+", " ");
		return normalized.isEmpty() ? null : normalized;
	}

	private String normalizeSearchTerm(String value) {
		log.info("Init method PlayerServiceImpl.normalizeSearchTerm");
		return normalizeRaw(value);
	}

	private String normalizeFilterValue(String value) {
		log.info("Init method PlayerServiceImpl.normalizeFilterValue");
		String normalized = normalizeRaw(value);
		return normalized == null ? null : normalized.toUpperCase();
	}

	private record PlayerMatch(Player player, boolean candidate, boolean birthdateMatched, int score) {
		private boolean isCandidate() {
			return candidate;
		}
	}
}
