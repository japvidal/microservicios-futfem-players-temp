package com.microservicios.app.futfem.players.services;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
	public Optional<Player> findByNameSurnameAndBirthdate(String name, String surname, String nickname, String birthdate) {
		List<String> requestTerms = buildRequestTerms(name, surname, nickname);
		if (requestTerms.isEmpty()) {
			return Optional.empty();
		}

		Optional<LocalDate> requestedBirthdate = parseBirthdate(birthdate);

		return StreamSupport.stream(repository.findAll().spliterator(), false)
				.map(player -> rankPlayer(player, requestTerms, requestedBirthdate))
				.filter(PlayerMatch::isCandidate)
				.max(Comparator.comparing(PlayerMatch::birthdateMatched)
						.thenComparingInt(PlayerMatch::score)
						.thenComparingLong(match -> match.player().getId()))
				.map(PlayerMatch::player);
	}

	private List<String> buildRequestTerms(String name, String surname, String nickname) {
		log.info("Init method PlayerServiceImpl.buildRequestTerms");
		List<String> terms = new ArrayList<>();
		addIfPresent(terms, normalize(name));
		addIfPresent(terms, normalize(surname));
		addIfPresent(terms, normalize(nickname));
		return terms;
	}

	private void addIfPresent(List<String> terms, String value) {
		log.info("Init method PlayerServiceImpl.addIfPresent");
		if (value != null && !terms.contains(value)) {
			terms.add(value);
		}
	}

	private PlayerMatch rankPlayer(Player player, List<String> requestTerms, Optional<LocalDate> requestedBirthdate) {
		log.info("Init method PlayerServiceImpl.rankPlayer");
		List<String> playerTerms = buildPlayerTerms(player);
		if (playerTerms.isEmpty()) {
			return new PlayerMatch(player, false, false, 0);
		}

		boolean candidate = requestTerms.stream().anyMatch(request ->
				playerTerms.stream().anyMatch(playerTerm -> hasThreeLetterPrefixMatch(request, playerTerm)));

		if (!candidate) {
			return new PlayerMatch(player, false, false, 0);
		}

		int score = requestTerms.stream()
				.mapToInt(request -> playerTerms.stream()
						.mapToInt(playerTerm -> scoreTerms(request, playerTerm))
						.max()
						.orElse(0))
				.sum();

		boolean birthdateMatched = requestedBirthdate
				.map(date -> matchesBirthdate(player, date))
				.orElse(false);

		return new PlayerMatch(player, true, birthdateMatched, score);
	}

	private List<String> buildPlayerTerms(Player player) {
		log.info("Init method PlayerServiceImpl.buildPlayerTerms");
		List<String> terms = new ArrayList<>();
		addIfPresent(terms, normalize(player.getName()));
		addIfPresent(terms, normalize(player.getSurname()));
		addIfPresent(terms, normalize(player.getNickname()));
		return terms;
	}

	private boolean hasThreeLetterPrefixMatch(String left, String right) {
		log.info("Init method PlayerServiceImpl.hasThreeLetterPrefixMatch");
		return commonPrefixLength(left, right) >= 3;
	}

	private int scoreTerms(String request, String candidate) {
		log.info("Init method PlayerServiceImpl.scoreTerms");
		if (request.equals(candidate)) {
			return 100;
		}
		if (request.startsWith(candidate) || candidate.startsWith(request)) {
			return 75;
		}
		int commonPrefix = commonPrefixLength(request, candidate);
		if (commonPrefix >= 3) {
			return 10 + commonPrefix;
		}
		return 0;
	}

	private int commonPrefixLength(String left, String right) {
		log.info("Init method PlayerServiceImpl.commonPrefixLength");
		int max = Math.min(left.length(), right.length());
		int count = 0;
		for (int i = 0; i < max; i++) {
			if (left.charAt(i) != right.charAt(i)) {
				break;
			}
			count++;
		}
		return count;
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

	private record PlayerMatch(Player player, boolean candidate, boolean birthdateMatched, int score) {
		private boolean isCandidate() {
			return candidate;
		}
	}
}
