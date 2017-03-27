/**
 * 下载测试servlet
 */
package com.icbc.nt.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icbc.nt.util.IcbcUtil;

public class DownLoad extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		IcbcUtil iu = new IcbcUtil();
		String fileName = iu.getProperty("fileDownTest");
		OutputStream out = response.getOutputStream();
		fileName = URLEncoder.encode(fileName, "UTF-8");
		System.out.println(DownLoad.class.getClassLoader().getResource("/"));
		System.out.println("111:"+request.getServletPath());
		System.out.println("222:"+request.getRequestURI());
		System.out.println("333:"+request.getSession().getServletContext().getRealPath(request.getRequestURI()));
		String path = request.getSession().getServletContext().getRealPath("/download/exceltpl");
		
		File file = new File(path+"/"+iu.getProperty("fileDownTest"));
		FileInputStream fis = new FileInputStream(file);
		
//		response.setContentType("application/vnd.ms-excel");
		response.setContentType("application/octet-stream");
		
		response.addHeader("Content-Disposition","attachment;filename="+fileName);
		response.addHeader("Content-Length", file.length()+"");
		
		byte[] buf = new byte[1024];
		int len = 0;
		while((len = fis.read(buf)) > 0){
			out.write(buf, 0, buf.length);
			System.out.println("输出len："+len);
		}
		System.out.println("path:"+path);
		
		out.flush();
		out.close();
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

}
