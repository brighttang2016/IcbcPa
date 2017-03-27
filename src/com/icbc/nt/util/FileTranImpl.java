package com.icbc.nt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.test.DownLoad;

public class FileTranImpl implements FileTran {
	@Value("${uploadLen}")
	int uploadLen;
	@Value("${uploadPath}")
	String uploadPath;
	Logger logger = IcbcUtil.getLogger();
	/*@Autowired
	PushThread pushThread;*/
	public void startPush(String receiver,TransactionMapData tmd){
		tmd.put("percent", "0");
		tmd.put("errorCode", "0");
		tmd.put("errorMsg", "");
		tmd.put("finish", "0");
		tmd.put("progress", "0");
		MessagePushHelper mph = new MessagePushHelper();
		TransactionMapData tmdTemp = new TransactionMapData();
//		tmdTemp = tmd;
		new PushThread(receiver,mph,tmd.clone()).start();
//		new PushThread(receiver,mph,tmd).start();
		/*pushThread.setReceiver(receiver);
//		pushThread.setTmd(tmd);
		pushThread.setMph(mph);
		pushThread.start();*/
//		new PushThread(receiver,mph).start();
	}
	@Override
	public void fileUpload(MultipartFile file,TransactionMapData tmd) {
		String path = tmd.get("path").toString();
		IcbcUtil iu = new IcbcUtil();
		path = path+uploadPath;
		tmd.put("path", path);
		logger.info("path:"+path);
		tmd.put("percent", "0");
		tmd.put("errorCode", "0");
		tmd.put("errorMsg", "");
		tmd.put("finish", "0");
		tmd.put("progress", "0");
//		HashMap hashMap = new HashMap();
//		hashMap.put("name", "tang");
		String fileName = tmd.get("userId").toString()+iu.getTime()+"-"+file.getOriginalFilename();
		tmd.put("fileName", fileName);
		long fileLen = file.getSize();//文件长度
		long fileWrite = 0;//写入长度
		String percent = "";//百分比
		logger.info("准备推送:"+tmd.get("userId").toString());
//		this.startPush(tmd.get("userId").toString(),session,tmd,hashMap);
		this.startPush(tmd.get("userId").toString(),tmd);
		if(fileLen > 1024*1024*uploadLen){
			tmd.put("errorCode", "2");
			tmd.put("errorMsg", "上传失败,文件超过100兆");
			tmd.put("finish", "1");
			logger.error("上传失败,文件"+fileName+"超过100兆");
		}else{
			try {
				int len = 0;
				byte[] buf = new byte[1024];
				InputStream is = file.getInputStream();
				FileOutputStream fos = new FileOutputStream(new File(path+"\\"+fileName));
				logger.info("**********准备开始写入文件:"+fileName+"****************");
//				System.out.println("FileUploadTest.java**********准备开始写入文件:"+fileName+"****************");
				while((len = is.read(buf)) > 0){
					fos.write(buf, 0, len);
					fileWrite += len;
//					System.out.println("tttttttt:"+fileWrite/fileLen+"|fileLen:"+fileLen+"|fileWrite:"+fileWrite);
					percent = iu.getPercentage((double)fileWrite/fileLen, "####.####");
//					tmd.put("percent", percent);
					tmd.put("errorCode", "0");
//					tmd.put("errorMsg", "正在上传");
					tmd.put("errorMsg",percent);
					
					tmd.put("progress", (double)fileWrite/fileLen);
					tmd.put("finish", "0");
				}
				tmd.put("errorCode", "1");
				tmd.put("errorMsg", "文件上传成功");
//				tmd.put("finish", "1");
				logger.debug(file.isEmpty()+"file.getSize():"+file.getSize());
				logger.info("**********文件:"+fileName+"****上传成功************");
//				System.out.println("FileUploadTest.java**********文件:"+fileName+"****上传成功************");
				fos.close();
			} catch (Exception e) {
				tmd.put("errorCode", "2");
				tmd.put("errorMsg", "文件上传失败");
				tmd.put("finish", "1");
				logger.error("**********文件:"+fileName+"****上传失败************");
				iu.error(logger, e);
			}
		}
	}
	@Override
	public void fileDownLoad(HttpServletResponse response,String fileName, String filePath){
		logger.info("文件下载");
		File file = new File(filePath+"\\"+fileName);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
//			response.setContentType("application/vnd.ms-excel");//返回类型为excel
			response.setContentType("application/octet-stream");//返回类型为二进制流
			response.addHeader("Content-Disposition","attachment;filename="+URLEncoder.encode(fileName,"UTF-8"));
			response.addHeader("Content-Length", file.length()+"");
			byte[] buf = new byte[1024];
			int len = 0;
			OutputStream out = response.getOutputStream();
			while((len = fis.read(buf)) > 0){
				out.write(buf, 0, buf.length);
				System.out.println("输出len："+len);
			}
			out.flush();
			out.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
