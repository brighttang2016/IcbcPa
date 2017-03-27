package com.icbc.nt.bean;

public class BeanParent {
	String tx_code;
	String start;
	String limit;
	
	public String getLimit() {
		return limit;
	}
	
	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}
	
	public String getTx_code() {
		return tx_code;
	}

	public void setTx_code(String tx_code) {
		this.tx_code = tx_code;
	}
}
