package com.icbc.nt.bean;

public class SsBean extends BeanParent{
	private String ssId;
	private String yearStart;
	private String yearEnd;
	private String subsity;
	private String note;
	
	public String getSsId() {
		return ssId;
	}
	public void setSsId(String ssId) {
		this.ssId = ssId;
	}
	public String getYearStart() {
		return yearStart;
	}
	public void setYearStart(String yearStart) {
		this.yearStart = yearStart;
	}
	public String getYearEnd() {
		return yearEnd;
	}
	public void setYearEnd(String yearEnd) {
		this.yearEnd = yearEnd;
	}
	public String getSubsity() {
		return subsity;
	}
	public void setSubsity(String subsity) {
		this.subsity = subsity;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}
