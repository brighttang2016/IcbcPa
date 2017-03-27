/**
 * 2015-10-14 brighttang
 * 文件上传监听者接口
 */
package com.icbc.nt.excel;

import com.icbc.nt.bus.FileUploadEvent;
import com.icbc.nt.util.TransactionMapData;
/*
public abstract class FileUploadListener {
	protected TransactionMapData tmd = null;
	*//**
	 * 文件上传后处理动作
	 * @param e
	 *//*
	public void actionToFileUpload(FileUploadEvent e){
		
	};
}*/
public interface FileUploadListener {
	/**
	 * 文件上传后处理动作
	 * @param e
	 */
	public void actionToFileUpload(FileUploadEvent e);
}
