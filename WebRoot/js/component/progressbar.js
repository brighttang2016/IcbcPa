/**
 * 进度条进度控制
 */

var pgsMsgArr = new Array();
var pgsMsg = "正在处理...";
Ext.MessageBox.on('close',function(){
	pgsMsgArr = new Array();
	pgsMsg = "正在处理...";
});
function showMessage(msg){
//	alert("msg");
	var respJson  = Ext.JSON.decode(msg);
	var errorCode = respJson.errorCode;
	var errorMsg = respJson.errorMsg;
	var progress = respJson.progress;
	var zq  = respJson.data;
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
//	msgAlert(errorCode);
	Ext.MessageBox.updateProgress(progress,errorMsg,pgsMsg);//progress：进度条百分比,errorMsg：进度条显示信息,pgsMsg：进度条上方提示信息
	if(errorCode == "10"){//处理完毕
		setTimeout(function(){
			//Ext.MessageBox.hide();
			Ext.data.StoreManager.lookup("gridStoreId").reload();
		},2000);
	}
}