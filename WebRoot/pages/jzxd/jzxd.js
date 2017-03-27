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

/*function showMessage(msg){
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
}*/

Ext.Loader.setConfig({
	enable:true,
	paths:{
		'App.js':'pages/js'
	}
});
var jsonData = {'items':[]};
function showMessage2(msg){
	alert(msg);
	
	/*jsonData = {'items':[
		         		        { 'name': 'Lisa',  "email":"lisa@simpsons.com",  "phone":"555-111-1224"  },
		        		        { 'name': 'Bart',  "email":"bart@simpsons.com",  "phone":"555-222-1234" },
		        		        { 'name': 'Homer', "email":"home@simpsons.com",  "phone":"555-222-1244"  },
		        		        { 'name': 'Marge', "email":"marge@simpsons.com", "phone":"555-222-1254"  }
		        		    ]};*/
	jsonData = Ext.JSON.decode(msg);
	var tableStore = Ext.data.StoreManager.lookup('simpsonsStore');
	//tableStore.data = jsonData;
	//tableStore.reload();
//	tableStore.loadData(jsonData,true);
	tableStore.loadRawData(jsonData,false);
	//alert("showMessage2:"+msg);
}

Ext.application({
	name:'roleInfo',
	launch:function(){
		/*Ext.Loader.require("js.component.msgpush");*/
	/*	Ext.Loader.require("js.component.fileupload");*/
		MessagePush.onPageLoad(userId);//注册监听
		//fileUpload("30011","工资基准薪点初始化");
		
		var act =  null;
		var i=1;
		function getPercentage(){
			Ext.Ajax.request({
				url:'getPercent.html',
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
		
		
		
		
		Ext.create('Ext.data.Store', {
		    storeId:'simpsonsStore',
		    fields:['name', 'email', 'phone'],
		    data:jsonData,
		    proxy: {
		        type: 'memory',
		        reader: {
		            type: 'json',
		            root: 'items'
		        }
		    }
		});

		var showPanel = Ext.create('Ext.grid.Panel', {
		    title: 'Simpsons',
		    store: Ext.data.StoreManager.lookup('simpsonsStore'),
		    columns: [
		        { text: 'Name',  dataIndex: 'name' },
		        { text: 'Email', dataIndex: 'email', flex: 1 },
		        { text: 'Phone', dataIndex: 'phone' }
		    ],
		    height: 200,
		    width: 400,
		    renderTo: Ext.getBody()
		});
		
		var tableViewport = Ext.create('Ext.container.Viewport',{
			layout:'border',
			renderTo:Ext.getBody(),
			frame:false,
			defaults:{
				frame:false,//viewport的frame配置项，必须在defaults中配置才会生效(就是这里引起中间部分出现双层边框)
				border:true
			},
			
			items:[{
				region:'north',
				collapsible:false,
				split:true,
//				xtype:'panel',
				layout:'fit',
//				height:100,
				border:false,
				items:[new fileUploadCmp().getUploadPanel(30011)],
				margins:'0 0 4 0'
			},{
				region:'center',
				collapsible:false,
				layout:'fit',
				border:false,
				items:[showPanel],
				margins:'0 0 4 0'
			}]
		});
	}	
});

