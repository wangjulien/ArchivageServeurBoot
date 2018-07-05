package com.telino.avp.entity.context;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "login")
@NamedNativeQueries({
		@NamedNativeQuery(name = "User.updatePassword", query = "update login set userpassword = crypt(:encryptedPassword, gen_salt('bf')) where userid = :userId") })
public class User {

	@Id
	private String userId;

	private String userMail;
	private String userPassword;
	private String nom;
	private String prenom;

	@OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private Set<ParRight> parRights = new HashSet<>();

	public User() {
		super();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public Set<ParRight> getParRights() {
		return parRights;
	}

	public void setParRights(Set<ParRight> parRights) {
		this.parRights = parRights;
	}

	public void addParRight(ParRight parRight) {
		this.parRights.add(parRight);
		parRight.setUser(this);
	}
}
