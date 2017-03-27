// JavaScript Document

dwr.engine.setActiveReverseAjax(true);
dwr.engine.setNotifyServerOnPageUnload(true);

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

function showMessage(msg){
//	alert("msg");
	var respJson  = Ext.JSON.decode(msg);
	var errorCode = respJson.errorCode;
	var errorMsg = respJson.errorMsg;
	var progress = respJson.progress;
	var zq  = respJson.data;
//	msgAlert(errorCode);
	if(errorCode == "10"){
		Ext.MessageBox.updateProgress(progress,errorMsg,'处理完毕');
		setTimeout(function(){
			Ext.MessageBox.hide();
			Ext.data.StoreManager.lookup("gridStoreId").reload();
		},2000);
		/*Ext.MessageBox.updateProgress(progress,errorMsg,'准备下载');
		if(zq!= null && zq!='' && zq!=undefined){
			Ext.data.StoreManager.lookup("gridStoreId").reload();
			setTimeout(function(){
				Ext.MessageBox.hide();
//				fileDownload("jxDownload.html?zq="+zq+"&tx_code=10000072");
			},2000);
		}*/
	}else if(errorCode == "11"){
		Ext.MessageBox.updateProgress(progress,errorMsg,'处理完毕');
	}else if(errorCode == "12"){
		Ext.MessageBox.updateProgress(progress,errorMsg,'正在处理...');
	}
}

Ext.application({
	name:'roleInfo',
	launch:function(){
		Ext.Loader.require("js.comm");
		var userId = top.varpool.userId;
		MessagePush.onPageLoad(userId);//注册监听
		/*********查询开始**********/
		var orgCmp =  new orgCmponent();
		/*var orgTreePanel = orgCmp.getTreeQuery();
		var orgInfoQuery = orgCmp.getOrgInfoQuery(orgTreePanel);*/
		var tpQuery = orgCmp.treePickerQuery();//查询区panel机构下拉树
		//考核周期
		var zqCmp = new zqComponent();
		var zqCombo = zqCmp.getZqCombo();
		checkForm = Ext.create('Ext.form.Panel',{
			buttonAlign:'center',
			id:'checkForm',
			title:'查询区',
			frame:true,
			border:false,
			defaults:{
				labelWidth:50,
				labelAlign:'right',
				msgTarget:'side'
			},
			items:[{
				layout:'column',
				baseCls : 'my-panel-no-border',
				frame:true,
				border:false,
				items:[{
					xtype:'hidden',
					name:'orgId',
					id:'orgIdQuery'
				},{
					xtype:'hidden',
					name:'deptId',
					id:'deptIdQuery'
				},new queryTextField({
					columnWidth:.5,
					items:[tpQuery]
				}),new queryTextField({
					columnWidth:.5,
					items:[zqCombo]
				})]
			},{
				layout:'column',
				frame:true,
				baseCls:'my-panel-no-border',
				items:[{
					frame:true,
					border:true,
					baseCls:'my-panel-no-border',
					columnWidth:.5,
					layout:{
						type:'hbox',
						pack:'center'
					},
					items:[{
						xtype:'label',
						text:'【机构部门:】',
						id:'queryStrId',
					}]
				}]
			}],
			buttons:[{
				text:'查询',
				handler:function(){
					comm_query('gridStoreId','checkForm','20000011');
				}
			},{
				text:'清空',
				handler:function(){
//					checkForm.getForm().reset();
					Ext.getCmp('checkForm').getForm().reset();
					Ext.getCmp('queryStrId').setText('【机构部门:】');
				}
			}]
		});
		/*********查询结束**********/
		
		/*********显示开始**********/
//		'userid','name','orgid','depid','orgname','depname','zq',
//        'jx_gwlz','jx_wdzykh','jx_qyyxjl','jx_wdsdfp','jx_sum'
		//列
		var myColumns = [ 
//		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
            {header:'机构名称',dataIndex:'orgname',width:150,align:'left',locked:true},
            {header:'部门名称',dataIndex:'depname',width:130,align:'left',locked:true},
            {header:'人力资源编码',dataIndex:'userid',width:100,align:'left',locked:true},
            {header:'姓名',dataIndex:'name',width:80,align:'left',locked:true},
            {header:'岗位(绩效考核)',dataIndex:'jbjx_pname',width:100,align:'left',locked:true},
            {header:'考核周期',dataIndex:'zq',width:80,editor:{},align:'left',locked:true},
            {text:'绩效',columns:[{header:'岗位履职考核',dataIndex:'jx_gwlz',width:110,editor:{},align:'left',locked:true},
                                {header:'网点自由考核',dataIndex:'jx_wdzykh',width:110,editor:{},align:'left',locked:true},
                                {header:'全员营销奖励',dataIndex:'jx_qyyxjl',width:110,editor:{},align:'left',locked:true},
                                {header:'网点手动分配',dataIndex:'jx_wdsdfp',width:110,editor:{},align:'left',locked:true}]},
            {header:'总绩效',dataIndex:'jx_sum',width:100,editor:{},align:'left'}];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			title:'当期绩效计算结果',
			scroll:true,
			border:true,
//			layout:'fit',
			width:'100%',
			height:'100%',
			viewConfig:{
//				forceFit:true,
//				stripeRows:true
			},
			store:tableStore,
			features:[{
				ftype:'summary'
			},{
				ftype:'summary'
			}],
			columns:myColumns,
//			selModel:Ext.create('Ext.selection.CheckboxModel',{
//				checkOnly:false//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
//			}),
			tbar:[{
					text:'数据导出',
					iconCls:'Pageexcel',
					handler:function(){
						var data = Ext.encode(getParam("checkForm"));//form表单数据
//						console.log(data);
						fileDownload("downLoad.html?tx_code=20000012&tranData="+data);
//						Ext.getDom('downloadIframe').src = "jxDownload.html?tx_code=10000072";
					}
				}
			],
			bbar:[{//分页（dockedItems显示在表格顶端）
				id:'pagingBar',
				xtype:'pagingtoolbar',
				store:tableStore,
				displayInfo:true,
				border:false,
				items:['每页显示',sizeCombo,'条']
			}]
		});
		
		/*********显示结束**********/
		
		var tableViewport = Ext.create('Ext.container.Viewport',{
			layout:'border',
			renderTo:Ext.getBody(),
//			resizable:true,
			defaults:{
//				frame:true,//viewport的frame配置项，必须在defaults中配置才会生效(就是这里引起中间部分出现双层边框)
//				border:true
			},
			items:[{
				region:'north',
				collapsible:false,
				split:true,
//				xtype:'panel',
				layout:'fit',
//				height:100,
				border:false,
				items:[checkForm],
				margins:'0 0 4 0'
			},{
				region:'center',
				collapsible:false,
				layout:'fit',
				border:false,
				items:[myGrid],
				margins:'0 0 4 0'
			}]
		});
		
		sizeCombo.on('select',function(combo){
			var pageSize = parseInt(combo.getValue());
			var pagingToolBar = Ext.getCmp('pagingBar');
			pagingToolBar.pageSize = pageSize;
			tableStore.pageSize = pageSize;
			tableStore.loadPage(1);
		});
		
		//机构点击事件执行函数
//		orgTreeClick(orgTreePanel,orgInfoQuery,checkForm);
		orgCmp.tpOnSelect(tpQuery,checkForm);//treePicker下拉树select事件
		setTimeout(function(){tableStore.reload();},100);//tableStore属性autoLoad:true，可能存在加载不成功的情况，故采用延时加载解决该问题。2015-11-10
	}	
});

