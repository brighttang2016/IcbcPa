package com.icbc.nt.bean;

public class RoleBean extends BeanParent{
	private String role_id;
	private String role_name;
	private String role_desc;
	private String role_auth;
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getRole_desc() {
		return role_desc;
	}
	public void setRole_desc(String role_desc) {
		this.role_desc = role_desc;
	}
	public String getRole_auth() {
		return role_auth;
	}
	public void setRole_auth(String role_auth) {
		this.role_auth = role_auth;
	}
	
}
