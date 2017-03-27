/**
 * 文件上传公用组件2015-10-16 brighttang
 */
dwr.engine.setActiveReverseAjax(true);
dwr.engine.setNotifyServerOnPageUnload(true);
dwr.engine.setErrorHandler(function(message){}); //屏蔽dwr毫无作用的后台错误提示


/**
 * 文件下载iframe初始化 2016-01-25
 */
function dwnIframeInit(){
	var downloadIframe = document.createElement("iframe");
	downloadIframe.setAttribute("id","downloadIframe");
	downloadIframe.style.display = "none";
	window.document.getElementsByTagName("body")[0].appendChild(downloadIframe);
}

//页面刷新(2016-02-25改为pageRefresh)
function showMessage2(msg){
	alert("showMessage2");
	setTimeout(function(){
		tableStore.reload();
	},5000);
};

//页面刷新
function pageRefresh(msg){
	var respJson  = Ext.JSON.decode(msg);
	var errorCode = respJson.errorCode;
	var errorMsg = respJson.errorMsg;
	if(errorCode == 10){//文件上传处理成功，刷新前端页面
		setTimeout(function(){
			tableStore.reload();
		},2000);
	}
	
};

/**
 * 文件上传
 * @param txCode 交易码
 * @param name 窗口名称
 */
var upWin;
function fileUpload(txCode,name){
	var fileUpCmp = new fileUploadCmp();
	var upPanel = fileUpCmp.getUploadPanel(txCode);
	upWin = fileUpCmp.getUploadWin(name,upPanel);
	upWin.show();
}

/*function showMessage2(msg){
	alert(msg);
}*/
/**
 * 2015-10-14 brighttang
 * 文件上传模块中，显示后台推送信息（动态更新进度条当前进度）
 */
var pgsMsgArr = new Array();
var pgsMsg = "正在处理...";
Ext.MessageBox.on('close',function(){
	pgsMsgArr = new Array();
	pgsMsg = "正在处理...";
});
function showMessage(msg){
//	upWin.hide();
	try{
		upWin.close();
	}catch(e){}
//	var json = Ext.JSON.decode(msg);
//	msgAlert(json.percent);
	var respJson  = Ext.JSON.decode(msg);
	var percent = respJson.percent;
	var errorCode = respJson.errorCode;
	var errorMsg = respJson.errorMsg;
	var progress = respJson.progress;
	
	var isExist = false;
	for(var i = 0;i < pgsMsgArr.length;i ++){
		if(pgsMsgArr[i] == errorMsg){
			isExist = true;
		}
	}
	if(!isExist && errorMsg != undefined && errorMsg != ""){
		pgsMsgArr.push(errorMsg);
		pgsMsg += "<br/>"+errorMsg;
	}
	
	if(errorCode == "10"){//上传成功
//		clearTimeout(act);
//		Ext.MessageBox.updateProgress(progress,errorMsg,'处理完毕');
		Ext.MessageBox.updateProgress(progress,errorMsg,pgsMsg);//progress：进度条百分比,errorMsg：进度条显示信息,pgsMsg：进度条上方提示信息
		setTimeout(function(){
//			Ext.MessageBox.hide();
			//Ext.MessageBox.close();
		},3000);
	}else if(errorCode == "11"){//上传失败
//		clearTimeout(act);
//		Ext.MessageBox.updateProgress(progress,errorMsg,'处理完毕');
		Ext.MessageBox.updateProgress(progress,errorMsg,pgsMsg);//progress：进度条百分比,errorMsg：进度条显示信息,pgsMsg：进度条上方提示信息
		/*setTimeout(function(){
			Ext.MessageBox.hide();
		},5000);*/
	}else{
//		Ext.MessageBox.updateProgress(progress,percent,'正在上传');
//		Ext.MessageBox.updateProgress(progress,errorMsg,'正在处理...');
		Ext.MessageBox.updateProgress(progress,errorMsg,pgsMsg);//progress：进度条百分比,errorMsg：进度条显示信息,pgsMsg：进度条上方提示信息
	}
}

/**
 * 文件上传组件
 */
function fileUploadCmp(){
	this.getUploadWin = function(title,item){
		 var uploadWin = Ext.create('Ext.window.Window',{
				constrain:true,
				layout:'fit',
				title:title,
				maximizable:true,
				width:400,
				height:150,
				modal:true,
				items:[item],
				closeAction:'destroy'/*,
				animateTarget:Ext.getBody()//弹出动画(会出现偶发性卡顿现象)
*/			});
		 return uploadWin;
	};
	this.getUploadPanel = function(txCode){
		upPanel = Ext.create('Ext.form.Panel', {
//		    title: '文件上传',
//		    width: 400,
		    bodyPadding: 10,
		    frame: true,
		    id:'upPanel',
		    style:'text-align:center',
		    baseCls:'my-panel-no-border',
//		    renderTo: Ext.getBody(),
		    items: [{
		        xtype: 'filefield',
		        name: 'file',
		        fieldLabel: '选择文件',
		        labelWidth: 100,
		        msgTarget: 'side',
		        allowBlank: false,
		        anchor: '100%',
		        buttonText: '请选择上传文件'
		    }],
		    dockedItems:[{
		    	xtype:'toolbar',
		    	dock:'bottom',
		    	ui:'footer',
		    	layout:{
		    		pack:'center'
		    	},
		    	items:[{
		    		text:'上传',
		    		minWidth:80,
		    		margin:'0 0 10 0',
		    		handler:function(){
		    			var form = this.up('form').getForm();
			            if(form.isValid()){
//			            	Ext.MessageBox.progress('请等待','正在上传');
			            	
			            	Ext.MessageBox.show({
			            		title:'请等待',
			            		msg:'正在上传',
			            		border: 5,
			            		style: {
			            		    borderColor: 'red',
			            		    borderStyle: 'solid'
			            		},
			            		width:380,
			            		progress:true,
			            		closable:true
			            	});
			            	setMsgBoxScroll();
			                form.submit({
			                    url: 'fileUpload.html?tx_code='+txCode,
			                    //waitMsg: '文件上传中...',
			                    success: function(fp, o) {
			                    	setTimeout(function(){
			                    		//Ext.Msg.alert('Success', '文件 "' + o.result.file + '" 上传成功');
			                    	},10);
			                    }
			                });
			                Ext.MessageBox.updateProgress(0,'正在处理...');
			                //setTimeout(getPercentage,1000);
			                
			            }
		    		}
		    	}]
		    }]
		});
		return upPanel;
	};
	
	//关闭弹窗时，清空弹窗显示信息滚动条
	 Ext.MessageBox.on('close',function(){
		 clearMsgBoxScroll();
	 });
}