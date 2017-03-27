/**
 * 2015-10-14 brighttang
 * 文件上传事件类
 */
package com.icbc.nt.bus;

import com.icbc.nt.util.TransactionMapData;

public class FileUploadEvent {
	private Object obj;
	private TransactionMapData tmd;
	public FileUploadEvent(Object obj,TransactionMapData tmd){
		this.obj = obj;
		this.tmd = tmd;
	}
	public Object getObj() {
		return obj;
	}
	public TransactionMapData getTmd() {
		return tmd;
	}
	
	
}
