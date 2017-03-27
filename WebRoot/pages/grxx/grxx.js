// JavaScript Document
var height;
var updFormPanel;
//var tableStore;
var win;
var checkForm;
var winUpd;
var winAdd;
var checkForm;
var role_id = 0;

var leafTag;
var pId;
var pName;
var menuId;
var menuName;
var menuType;
var recordNow;
var orgId;
var udpFormPanelAdd;
//var orgInfoUpd;//机构部门下拉框（用户更新）
Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'js/ext');
Ext.require([
 	'Ext.form.Panel',
	'Ext.ux.form.MultiSelect',
	'Ext.ux.form.ItemSelector',
	'Ext.tip.QuickTipManager',
	'Ext.ux.ajax.JsonSimlet',
	'Ext.ux.ajax.SimManager',
	'Ext.ux.ajax.DataSimlet',
	'Ext.ux.ajax.SimXhr',
	'Ext.ux.ajax.Simlet'
]);
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
		var sendParamCol = Ext.create('Ext.util.MixedCollection');
		sendParamCol.add('txCode','20021');
		Ext.Ajax.request({
			type:'ajax',
			url:'userInfoQuery.html',
			params:{
//				sendJson:Ext.encode(sendParamCol.map)
				txCode:'20021'
			},
			success:function(response){
				var json = Ext.decode(response.responseText);
				var rowData = json.rows[0];
				var userId = rowData.userid;
				var baseInfo = userBasicInfo(rowData);//用户基本信息
				var pointInfo = userPointHisInfo(userId);//用户积分历史信息
				var wglHisInfo = userWglHisInfo(userId);//工资等级档次历史记录
				var jbHisInfo = userJbHisInfo(userId);//岗位层级历史变更记录
				var xfInfo = userxfInfo(userId);
				var ctInfo = userCtInfo(userId);
				/*userInfoPanel = Ext.create('Ext.form.Panel',{
					title:'个人信息',
					layout:'vbox',
					items:[baseInfo,pointInfo,wglHisInfo,jbHisInfo]
				});*/
				
				userInfoPanel = Ext.create('Ext.tab.Panel', {
					id:'userInfoPanelId',
				   // width: 400,
				    //height: 400,
				    title:'个人信息',
				    //renderTo: document.body,
				   defaults:{
				    	layout:{
				        	type:'hbox',
				        	pack:'center'/*,
				        	align:'center'*/
				        }
				    },
				    items: [{
				        title: '个人基本信息',
				        items:[baseInfo]
				    },{
				        title: '用户积分历史信息',
				        items:[pointInfo]
				    },{
				        title: '工资等级档次历史记录',
				        items:[wglHisInfo]
				    },{
				        title: '岗位层级历史变更记录',
				        items:[jbHisInfo]
				    } ,{
				        title: '学习积分',
				        items:[xfInfo]
				    },{
				        title: '资格信息',
				        items:[ctInfo]
				    }]
				});
				
				var tableViewport = Ext.create('Ext.container.Viewport',{
					layout:'border',
					renderTo:Ext.getBody(),
//					resizable:true,
					defaults:{
//						frame:true,//viewport的frame配置项，必须在defaults中配置才会生效(就是这里引起中间部分出现双层边框)
//						border:true
					},
					items:[{
						region:'center',
						collapsible:false,
						layout:'fit',
						autoScroll:true,
						border:false,
						items:[userInfoPanel],
						margins:'0 0 4 0'
					}]
				});
			},
			failure:function(){
			}
		});
	}	
});

