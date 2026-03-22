package com.microservicios.app.futfem.players.models.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="players_temp")
public class Player {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String surname;
	private String nickname;
	private String position;
	private String country;
	private String urlpic;
	
	private int velocidad;
	private int resistencia;
	private int defaerea;
	private int defestrategia;
	private int defagresividad;
	private int defanticipacion;
	private int visionjuego;
	private int ataquepases;
	private int ataqueremate;
	private int ataqueregate;
	private int ataqueaereo;
	private int ataquefaltas;
	private int ataquepenaltys;
	private int portero;
	
	@Column(name = "birthdate")
	private Date birthdate;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date createAt) {
		this.birthdate = createAt;
	}

	public String getUrlpic() {
		return urlpic;
	}

	public void setUrlpic(String urlpic) {
		this.urlpic = urlpic;
	}

	public int getVelocidad() {
		return velocidad;
	}

	public void setVelocidad(int velocidad) {
		this.velocidad = velocidad;
	}

	public int getResistencia() {
		return resistencia;
	}

	public void setResistencia(int resistencia) {
		this.resistencia = resistencia;
	}

	public int getDefaerea() {
		return defaerea;
	}

	public void setDefaerea(int defaerea) {
		this.defaerea = defaerea;
	}

	public int getDefestrategia() {
		return defestrategia;
	}

	public void setDefestrategia(int defestrategia) {
		this.defestrategia = defestrategia;
	}

	public int getDefagresividad() {
		return defagresividad;
	}

	public void setDefagresividad(int defagresividad) {
		this.defagresividad = defagresividad;
	}

	public int getDefanticipacion() {
		return defanticipacion;
	}

	public void setDefanticipacion(int defanticipacion) {
		this.defanticipacion = defanticipacion;
	}

	public int getVisionjuego() {
		return visionjuego;
	}

	public void setVisionjuego(int visionjuego) {
		this.visionjuego = visionjuego;
	}

	public int getAtaquepases() {
		return ataquepases;
	}

	public void setAtaquepases(int ataquepases) {
		this.ataquepases = ataquepases;
	}

	public int getAtaqueremate() {
		return ataqueremate;
	}

	public void setAtaqueremate(int ataqueremate) {
		this.ataqueremate = ataqueremate;
	}

	public int getAtaqueregate() {
		return ataqueregate;
	}

	public void setAtaqueregate(int ataqueregate) {
		this.ataqueregate = ataqueregate;
	}

	public int getAtaqueaereo() {
		return ataqueaereo;
	}

	public void setAtaqueaereo(int ataqueaereo) {
		this.ataqueaereo = ataqueaereo;
	}

	public int getAtaquefaltas() {
		return ataquefaltas;
	}

	public void setAtaquefaltas(int ataquefaltas) {
		this.ataquefaltas = ataquefaltas;
	}

	public int getAtaquepenaltys() {
		return ataquepenaltys;
	}

	public void setAtaquepenaltys(int ataquepenaltys) {
		this.ataquepenaltys = ataquepenaltys;
	}

	public int getPortero() {
		return portero;
	}

	public void setPortero(int portero) {
		this.portero = portero;
	}

	
	
}
