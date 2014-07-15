package com.foreach.across.modules.user.business;

import javax.persistence.Column;
import javax.persistence.Id;

//@Entity
public class UserApplicationRole
{
	@Id
	@Column( name = "user_id", nullable = false)
	private User user;

	@Id
	@Column( name = "user_application_id", nullable = false)
	private UserApplication userApplication;

	@Id
	@Column( name = "role_id", nullable = false)
	private Role role;

	public User getUser() {
		return user;
	}

	public void setUser( User user ) {
		this.user = user;
	}

	public UserApplication getUserApplication() {
		return userApplication;
	}

	public void setUserApplication( UserApplication userApplication ) {
		this.userApplication = userApplication;
	}

	public Role getRole() {
		return role;
	}

	public void setRole( Role role ) {
		this.role = role;
	}
}
