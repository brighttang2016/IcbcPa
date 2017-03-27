// JavaScript Document
var height;
var updFormPanel;
var tableStore;
var win;
var checkForm;
var role_id = 0;
var pageSize = 10;
var leafTag;
var pId;
var pName;
var menuId;
var menuName;
var menuType;
var recordNow;
var orgId;

/*方法二：后台推送进度条信息*/
function showMessage(msg){
//	var json = Ext.JSON.decode(msg);
//	msgAlert(json.percent);
	var respJson  = Ext.JSON.decode(msg);
	var percent = respJson.percent;
	var errorCode = respJson.errorCode;
	var errorMsg = respJson.errorMsg;
	var progress = respJson.progress;
	if(errorCode == "1"){//上传成功
//		clearTimeout(act);
		Ext.MessageBox.updateProgress(progress,errorMsg,'正在上传');
		setTimeout(function(){
			Ext.MessageBox.hide();
		},3000);
	}else if(errorCode == "2"){//上传失败
//		clearTimeout(act);
		Ext.MessageBox.updateProgress(progress,errorMsg,'正在上传');
	}else{
		Ext.MessageBox.updateProgress(progress,percent,'正在上传');
	}
}

Ext.Loader.setConfig({
	enable:true,
	paths:{
		'App.js':'pages/js'
	}
});

Ext.application({
	name:'roleInfo',
	launch:function(){
		Ext.Loader.require("js.comm");
		MessagePush.onPageLoad(userId);
		dwr.engine.setActiveReverseAjax(true);
		dwr.engine.setNotifyServerOnPageUnload(true);
		
		upPanel = Ext.create('Ext.form.Panel', {
		    title: '文件上传',
//		    width: 400,
		    bodyPadding: 10,
		    frame: true,
		    id:'upPanel',
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
		    buttons: [{
		        text: '上传',
		        handler: function() {
		            var form = this.up('form').getForm();
		            if(form.isValid()){
//		            	Ext.MessageBox.progress('请等待','正在上传');
		            	Ext.MessageBox.show({
		            		title:'请等待',
		            		msg:'正在上传',
		            		width:240,
		            		progress:true,
		            		closable:true
		            	});
		                form.submit({
		                    url: 'fileUploadTest.html',
		                    //waitMsg: '文件上传中...',
		                    success: function(fp, o) {
		                    	setTimeout(function(){
		                    		//Ext.Msg.alert('Success', '文件 "' + o.result.file + '" 上传成功');
		                    	},10);
		                    }
		                });
		                Ext.MessageBox.updateProgress(0,'正在上传');
		                //setTimeout(getPercentage,1000);
		            }
		        }
		    }]
		});
		
		/*方法一：通过ajax请求调取进度条信息*/
		var act =  null;
		var i=1;
		function getPercentage(){
			Ext.Ajax.request({
				url:'getPercentTest.html',
				type:'ajax',
				success:function(resp,opts){
					act = setTimeout(getPercentage,1000);
					var respJson  = Ext.JSON.decode(resp.responseText);
					var percent = respJson.percent;
					var errorCode = respJson.errorCode;
					var errorMsg = respJson.errorMsg;
					var progress = respJson.progress;
					if(errorCode == "1"){//上传成功
						clearTimeout(act);
						Ext.MessageBox.updateProgress(progress,errorMsg,'正在上传');
						setTimeout(function(){
							Ext.MessageBox.hide();
						},3000);
					}else if(errorCode == "2"){//上传失败
						clearTimeout(act);
						Ext.MessageBox.updateProgress(progress,errorMsg,'正在上传');
					}else{
						Ext.MessageBox.updateProgress(progress,percent,'正在上传');
					}
				},
				failure:function(resp,opts){
				}
			});
		}
		
		var tableViewport = Ext.create('Ext.container.Viewport',{
			layout:'border',
			renderTo:Ext.getBody(),
			frame:false,
			defaults:{
				frame:false,//viewport的frame配置项，必须在defaults中配置才会生效(就是这里引起中间部分出现双层边框)
				border:true
			},
			items:[upPanel]
		});
	}	
});

