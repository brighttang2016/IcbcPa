/**
 * excel解析公用业务处理接口
 * @author brighttang 2015-10-21
 */
package com.icbc.nt.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import com.icbc.message.push.MessagePushClient;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;
import com.icbc.nt.util.TransactionMapData;

public abstract class ExcelBus extends BusParent{
	public abstract void excelParseToDb(TransactionMapData tmd);
}
