package com.microservicios.app.futfem.players.models.repository;

import java.sql.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.microservicios.app.futfem.players.models.entity.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {

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
}
