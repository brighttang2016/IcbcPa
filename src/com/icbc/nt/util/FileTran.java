/**
 * 文件传输接口
 */
package com.icbc.nt.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

public interface FileTran {
	/**
	 * 开始推送文件上传处理进度
	 * @param receiver 接收消息监听者
	 * @param tmd 变量池
	 */
	public void startPush(String receiver,TransactionMapData tmd);
	/**
	 * 文件上传
	 * @param file
	 * @param tmd 变量池
	 */
	public void fileUpload(MultipartFile file,TransactionMapData tmd);
	/**
	 * 文件下载
	 * @param response
	 * @param fileName 文件名
	 * @param filePath 下载文件所在目录绝对路径
	 */
	public void fileDownLoad(HttpServletResponse response,String fileName,String filePath);
}
