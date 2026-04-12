package com.microservicios.app.futfem.players.models.repository;

import java.sql.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.microservicios.app.futfem.players.models.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	@Query(value = """
			select * from players p
			where lower(trim(p.name)) = lower(trim(?1))
			  and lower(trim(coalesce(p.surname, ''))) = lower(trim(coalesce(?2, '')))
			  and date(p.birthdate) = ?3
			limit 1
			""", nativeQuery = true)
	Optional<Player> findByNameSurnameAndBirthdate(String name, String surname, Date birthdate);

	@Query(value = """
			select * from players p
			where lower(trim(p.name)) = lower(trim(?1))
			  and lower(trim(coalesce(p.surname, ''))) = lower(trim(coalesce(?2, '')))
			  and p.birthdate is null
			limit 1
			""", nativeQuery = true)
	Optional<Player> findByNameSurnameAndBirthdateIsNull(String name, String surname);

	@Query("""
			select p from Player p
			where (:search is null
					or lower(coalesce(p.name, '')) like lower(concat('%', :search, '%'))
					or lower(coalesce(p.surname, '')) like lower(concat('%', :search, '%'))
					or lower(coalesce(p.nickname, '')) like lower(concat('%', :search, '%')))
			  and (:country is null or upper(coalesce(p.country, '')) = upper(:country))
			  and (:position is null or upper(coalesce(p.position, '')) = upper(:position))
			""")
	Page<Player> searchPlayers(
			@Param("search") String search,
			@Param("country") String country,
			@Param("position") String position,
			Pageable pageable);
}
