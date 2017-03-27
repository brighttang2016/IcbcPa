var progressBar;
var runner;
var task;
/**
 * 清空messageBox提示信息滚动条
 */
function clearMsgBoxScroll(){
	var updScrollPanel = document.getElementById("messagebox-1001-displayfield-inputEl");
	updScrollPanel.style.height = "50px";
	updScrollPanel.style.overflowY = "hidden";
	updScrollPanel.style.border = "0px solid #BCCADC";
}
/**
 * 设置messageBox提示信息滚动条
 */
function setMsgBoxScroll(){
	var updScrollPanel = document.getElementById("messagebox-1001-displayfield-inputEl");
	updScrollPanel.style.overflowY = "scroll";
	updScrollPanel.style.height = "100px";
	updScrollPanel.style.width = "360px";
	updScrollPanel.style.border = "1px solid #BCCADC";
}
/*文件下载*/
function fileDownload(url){
	var downloadIframe = document.createElement("iframe");
	downloadIframe.setAttribute("style","display:none");
	document.getElementsByTagName("body")[0].appendChild(downloadIframe);
	downloadIframe.src = url;
}
function getQueryStr(orgArr){
	var queryStr = "";
	for (var i = orgArr.length -1; i > 0 ; i--) {
		queryStr += orgArr[i]+"->";
	}
	queryStr += orgArr[0];
	return "【机构部门:"+queryStr+"】";
}

//所有上级机构记录
function getUpOrgAll(node,orgArr){
	orgArr.push(node.get("menuName"));
	var pnode;
	/*if(!pnode.get("menuName") == null)
		alert(pnode.get("menuName"));*/
	try{
		pnode = node.parentNode;
		if(pnode.get("id") != -1)
			getUpOrgAll(pnode,orgArr);
	}catch(e){}
}
//获取当前操作最近节点所属机构号
function getLatestOrgId(node){
	var menuIdRet;
	if(node.get("menuType") == 1){//menutype 1：机构 2：部门
		menuIdRet = node.get("menuId");
	}else{
		var parentNode = node.parentNode;
		menuIdRet = getLatestOrgId(parentNode);
	}
	return  menuIdRet;
}

function getNode(storeId,id){
//	alert(id);
//	alert(Ext.data.StoreManager.lookup(storeId).getNodeById("023"));
	storeTemp = Ext.data.StoreManager.lookup(storeId);
	node = storeTemp.getNodeById(id);
	return node;
}

var FormUtil={
	disableForm:function (formId)
	{
		var form=Ext.getCmp(formId);
		form.getEl().mask();
		form.items.each(function(item){
			if(item.getXTypes().indexOf('field')!==-1)
				item.disable();
			return true;
		});
		var arrBtn=form.buttons;
		/*for(var i=0;i<arrBtn.length;i++)
		{
			arrBtn[i].disable();
		}*/
	},
	disableFormShowCtrl:function (formId,showArr,hideArr)
	{
		var form=Ext.getCmp(formId);
		form.getEl().mask();
		form.items.each(function(item){
			if(item.getXTypes().indexOf('field')!==-1)
				item.disable();
			return true;
		});
		var arrBtn=form.buttons;
		/*for(var i=0;i<arrBtn.length;i++)
		{
			arrBtn[i].disable();
		}*/
		for (var i = 0; i < showArr.length; i++) {
			Ext.getCmp(showArr[i]).removeCls("hide");
		};
		for (var i = 0; i < hideArr.length; i++) {
			Ext.getCmp(hideArr[i]).addCls("hide");
		}
	},
	enableForm:function(formId,arr,hideArr,showArr)//arr是一个数组用于说明不被enable的item
	{
		if(!arr)
			arr=[];
		var form=Ext.getCmp(formId);
		form.getEl().unmask();
		form.items.each(function(item){
			var equalFlag=false;
			for(var i=0;i<arr.length;i++)
			{
//				alert("arr[i]:"+arr[i]+"|item.getItemId():"+item.getItemId());
				if(arr[i]==item.getId())
				{
					equalFlag=true;
					
					break;
				}
			}
			for (var i = 0; i < hideArr.length; i++) {
				Ext.getCmp(hideArr[i]).addCls("hide");
			}
			for (var i = 0; i < showArr.length; i++) {
				Ext.getCmp(showArr[i]).removeCls("hide");
			}
			if(!equalFlag)
				item.enable();
			return true;
		});
		/*var arrBtn=form.buttons;
		for(var i=0;i<arrBtn.length;i++)
		{
			arrBtn[i].enable();
		}*/
	}
};

//金额合计
function consbSumRender(value,metadata,tdCls){
	return tdCls.get('consb_price')*tdCls.get('num');
}

//打印参数设置
function printCfg(){
var cfgState = 0;
 var hkey_root,hkey_path,hkey_key;
 hkey_root="HKEY_CURRENT_USER";
 hkey_path="\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";
 try{
	 var RegWsh = new ActiveXObject("WScript.Shell");
	 cfgState = 1
	 hkey_key  = "header" ;
	 RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"中国工商银行物品申领单");
	 hkey_key = "footer";
	 RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"&b第 &p 页/共 &P 页&b");
	 hkey_key = "Print_Background";
	 RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"yes");
	 //RegWsh.RegWrite("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3\\1201","0","REG_DWORD");//0：启用 1：提示 3：jin
  }catch(e){
	 alert("提示：浏览器安全级别过高,无法打印，请提前修改后再打印！\n修改方法：进入：设置(快捷键:alt+x)->Internet选项->安全->自定义级别->ActiveX控件和插件->对未标记为可安全执行脚本的ActiveX控件初始化并执行,勾选为【提示】,点击【确定】【是】即可");
	 } 
	 return cfgState;
}
	//打印
function applyPrint(){
	if(navigator.userAgent.indexOf('MSIE') >= 0 || navigator.userAgent.indexOf('.NET') >= 0){
		if(printCfg()){
			document.getElementById("print").style.display = "none";
	 		window.print();
	 		window.close();
	 		//document.all.WebBrowser.ExecWB(7,1);
		}
	}else{
		document.getElementById("print").style.display = "none";
		window.print();
 		window.close();
	}
}

/**
 * 截取小数位(四舍五入) 2015-04-02 ，支持小数位数长度有numScal数组长度而定
 * num:资源数据，
 * length：保留小数位数
 */
function numDecMath(num,length){
	var numRet;
	var numScal = new Array(0,10,100,1000,10000,100000,1000000);//支持0-6位小数
	if(numScal[length] == 0){
		numRet = Math.round(num);//整数
	}else{
		numRet = Math.round(num*numScal[length])/numScal[length];//小数
	}
	return numRet;
}

/**
截取小数位(非四舍五入) ，支持任意长度小数位
 * 2015-04-02 
 * num:资源数据，
 * length：保留小数位数 
 */
function numDecRegx(num,length){
	var reg = "/^([0-9]+.[0-9]{"+length+"})[0-9]*$/";
	return num.replace(eval(reg),"$1");
}
//列金额总计records:所有记录，text:总计说明，dataIndex1：参与计算字段1，dataIndex：参与计算字段2
function getFeeSum(records,text,dataIndex1,dataIndex2){
	var total = 0;
	for (var i = 0; i < records.length; ++i) {
		record = records[i];
		total += record.get(dataIndex1) * record.get(dataIndex2);
	}
	return text+":"+numDecMath(total,2);
}
//列总计records:所有记录，text：列总计说明，dataIndex：参数总计类dataIndex
function getSum(records,text,dataIndex){
	var total = 0;
	for(var i = 0;i < records.length;i++){
		record = records[i];
		total +=parseFloat(record.get(dataIndex));
	}
	return text+":"+numDecMath(total,2);
}
//页面切切换
function pageChange(url){
	 if ((navigator.userAgent.indexOf('MSIE') >= 0 || (navigator.userAgent.indexOf('.NET')) >= 0)&& (navigator.userAgent.indexOf('Opera') < 0)) {//ie,navigator.userAgent.indexOf('.NET')ie11判断
		   document.location.href = '../../'+url;
	   }else{
		   document.location.href = url;//也可
	   }
}
//隐藏处理进度条
function progressBarHide(){
//	Ext.MessageBox.hide();
	progressBar.hide();
	runner.stop(task);
}
//显示处理进度条
function progressBarShow(){
	
	var count = 0;
	var bartext = "正在处理,请稍候...";
	var curnum = 0;//当前进度条百分比
//	Ext.MessageBox.wait('正在处理,请稍候...','提示');
	progressBar = Ext.MessageBox.show({
		title:'提示',
//		msg:'正在处理,请稍候...',
		progress:true,
		closable:false//,
//		icon:Ext.MessageBox.INFO
	});
	progressBar.updateProgress(curnum,bartext);
	task = {
			run:function(){
				count++;
				if(count > 10){
//					progressBar.hide();
					count = 0;
				}else{
					curnum = count/10;
					bartext=curnum*100+"%";
					bartext="正在处理,请稍候...";
					progressBar.updateProgress(curnum,bartext);
				}
			},
			interval:40
	};
	runner = new Ext.util.TaskRunner();
	runner.start(task);
}

//登录失效
function sessionValidate(msg){
	setTimeout(function(){
		Ext.MessageBox.alert("提示",msg,function(btn){
			//setTimeout('',2000);
			logout();
		});
	},100);
	//如下方式没用setTimeout,用户管理页面超时“Ext.MessageBox.alert”抛dom异常
	/*
	Ext.MessageBox.alert("提示",msg,function(btn){
		setTimeout('logout()',2000);
	});*/
	return;
}

function msgAlert(msg){
	Ext.Msg.show({
		title:'提示',
		width:300,
		msg:msg,
		icon:Ext.MessageBox.INFO,
		buttons:Ext.Msg.YES,buttonText:{yes:'确定'}
	});
}

/**
 * 2015-02-03
 * @param ckFormId 查询条件表单
 * @param expFormId 报表请求提交表单
 * @param action 提交地址
 */
function reportExp(ckFormId,action,tranCode,userId){
	params = "tx_code="+tranCode+"&"+'user_id='+userId+"&";//查询条件
	try{
		cdJson = Ext.getCmp(ckFormId).getForm().getValues(false);
		cdJsonSize = 0;//json对象长度
		index = 0;
		for(key in cdJson)
			cdJsonSize++;
		for(key in cdJson){
			if(index < cdJsonSize-1)
				params += key+"="+cdJson[key]+"&";
			else
				params += key+"="+cdJson[key];
			index++;
		}
	}catch(e){}
	reportForm = document.getElementById('reportFormId');
	reportForm.action = action+"?"+params;
//	alert(params);
	reportForm.submit();
}

/**time格式化
 * time:2014010101010101
 * return 2014-01-01 01:01:01
 */
function timeParse(time){
//	alert("timeParse:"+time);
	timeRet = "";
	try{
		if(time != "0" && time != "")
			timeRet = time.substr(0,4)+"-"+time.substr(4,2)+"-"+time.substr(6,2)+" "+time.substr(8,2)+":"+time.substr(10,2)+":"+time.substr(12,2);
	}catch(e){
		timeRet = null;
	}
	return timeRet;
}
/**time清除格式化
 * time:2014-01-01 01:01:01
 * return 2014010101010101
 */
function timeUnParse(time){
//	alert("timeUnParse:"+time);
	timeRet = "";
	try{
		if(time != "0" && time != "")
			timeRet = time.substr(0,4)+time.substr(5,2)+time.substr(8,2)+time.substr(11,2)+time.substr(14,2)+time.substr(17,2);
	}catch(e){
		timeRet = null;
	}
	return timeRet;
}




//*******************ctrl+q组合键开始******************************
var keyCodeArr = new Array();//当前按下按键数组
var keyCodeInit = new Array(17,81);//预设组合键ctrl+q
var keyExist = new Array();//判断16、17 81三个按键是否被同时按下
var index = 0;
document.onkeyup = keyup;
document.onkeydown = keydown;	
//按键弹起
function keyup(e){
	//keyCodeArr = new Array();
	var keyCodeTemp = new Array();
	j = 0;
	//将当前弹起按键移除按下按键数组
	for(i = 0;i < keyCodeArr.length;i++){
		if(event.keyCode != keyCodeArr[i]){
			keyCodeTemp[j++] = keyCodeArr[i];
		}
	}
	keyCodeArr = keyCodeTemp;
}
//按键按下
function keydown(e){
	var keyAdd = true;
	for(j = 0;j < keyCodeArr.length;j++){
		if(keyCodeArr[j] == event.keyCode){
			keyAdd = false;
		}
	}
	//添加当前按键（前提：该按键未被添加）
	if(keyAdd){
		keyCodeArr[index++] = event.keyCode;
	}
	//初始化按键按下状态，true：按下 false：弹起
	for(j = 0;j < keyCodeInit.length;j++){
		keyExist[j] = false;
		for(i = 0; i < keyCodeArr.length;i++){
			if(keyCodeInit[j] == keyCodeArr[i]){
				keyExist[j] = true;
			}
		}
	}
	var keyCom = true;//ctrl+shift+q组合键按下状态，true：同时按下，false:未同时按下
	for(var i = 0;i < keyExist.length;i++){
		if(!keyExist[i]){
			keyCom = false;
		}	
	}
	if(keyCom)
		logout(userId);
}
//*******************ctr+q组合键结束******************************




/**
 * session失效跳转控制器
 */
Ext.Ajax.on('requestcomplete', function(conn,response,options){
	var jsonstr = response.responseText;
	//var respJson = JSON.parse(jsonstr);//JSON.parse（）仅支持ie8及以上浏览器
	var respJson = Ext.JSON.decode(jsonstr);//支持ie8以下浏览器
	var jsonArray = eval('('+jsonstr+')');
	try{
		/*if(respJson.errorCode == '01'){
			setTimeout(function(){
				if(response.getResponseHeader("sessionStatus")){//session超时：sessionStatus:timeOut
					if(jsonArray.length > 0){
						sessionValidate(jsonArray[0].errorMsg);//导航条session超时（导航条后台返回的是json数组）
					}else{
						sessionValidate(respJson.errorMsg);//一般session超时(后台直接返回json串)
					}
				}else{//无访问权限
					msgAlert(respJson.errorMsg);
				}
			},50);
		}*/
		switch(parseInt(respJson.errorCode)){
		case 1://执行更新操作结果(弹出提示，不关闭)
			msgAlert(respJson.errorMsg);
			setTimeout(function(){Ext.MessageBox.hide();},3000);
			break;
		case 2://session失效
			setTimeout(function(){
				sessionValidate(respJson.errorMsg);//一般session超时(后台直接返回json串)
			},5);
			break;
		case 3://权限不足,无访问权限
			msgAlert(respJson.errorMsg);
			break;
		}
	}catch(e){}
});