package com.icbc.nt.bean;

public class AssptBean extends BeanParent{
	private String assptId;
	private String assptName;
	private String point;
	private String note;
	private String cond;
	
	public String getCond() {
		return cond;
	}
	public void setCond(String cond) {
		this.cond = cond;
	}
	public String getAssptId() {
		return assptId;
	}
	public void setAssptId(String assptId) {
		this.assptId = assptId;
	}
	public String getAssptName() {
		return assptName;
	}
	public void setAssptName(String assptName) {
		this.assptName = assptName;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}	
