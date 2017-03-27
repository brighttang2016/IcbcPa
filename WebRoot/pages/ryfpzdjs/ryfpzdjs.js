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
	
	var respJson  = Ext.JSON.decode(msg);
	var errorCode = respJson.errorCode;
	var errorMsg = respJson.errorMsg;
	var progress = respJson.progress;
	var zq  = respJson.data;
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
		var orgCmp = new orgCmponent();//机构组件对象
		var tree = orgCmp.getTreeQuery();//机构树对象
		var orgInfoQuery = orgCmp.getOrgInfoQuery(tree);;//机构下拉框对象
		checkForm = Ext.create('Ext.form.Panel',{
			buttonAlign:'center',
			id:'checkForm',
			title:'查询区',	
			frame:true,
			border:false,
			height:80,
			defaults:{
				labelWidth:50,
				labelAlign:'right',
				msgTarget:'side'
			},
			items:[],
			bbar:[{
				text:'绩效计算',
				//overCls:'jxCalcClass',
				iconCls:'Pageattach',
				handler:function(){
					Ext.MessageBox.show({
						title:'提示',
						msg:'正在处理...',
						width:300,
						progress:true,
						closable:true
					});
					Ext.MessageBox.updateProgress('0','准备计算','正在处理...');
					Ext.Ajax.request({
						type:'ajax',
						url:'jxCalcControler.html?tx_code=10000070',
//						params:params,
						success:function(resp,opts){
						},
						failure:function(resp,opts){
						}
					});
				}
			}]
		});
		/*********查询结束**********/
		
		/*********显示开始**********/
		//列
		var myColumns = [ 
//		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
            {header:'机构名称',dataIndex:'orgname',width:150,locked:true,align:'left'},
            {header:'部门名称',dataIndex:'depname',width:130,editor:{},align:'left',locked:true},
            {header:'人力资源编码',dataIndex:'userid',width:110,editor:{},align:'left',locked:true},
            {header:'姓名',dataIndex:'name',width:80,editor:{},align:'left',locked:true},
            {header:'考核周期',dataIndex:'zq',width:80,editor:{},align:'left',locked:true},
            {text:'支行考核绩效',
            	columns:[{header:'业务量',dataIndex:'jx_ywl_zh',editor:{},align:'left'},
            	         {header:'标准产品',dataIndex:'jx_bzcp_zh',editor:{},align:'left'},
            	         {header:'特色业务',dataIndex:'jx_tscp_zh',editor:{},align:'left'},
            	         {header:'定性',dataIndex:'jx_dx_zh',editor:{},align:'left'}]},
	         {text:'网点考核绩效',
	            	columns:[{header:'业务量',dataIndex:'jx_ywl_wd',editor:{},align:'left'},
	            	         {header:'标准产品',dataIndex:'jx_bzcp_wd',editor:{},align:'left'},
	            	         {header:'特色业务',dataIndex:'jx_tscp_wd',editor:{},align:'left'},
	            	         {header:'定性',dataIndex:'jx_dx_wd',editor:{},align:'left'}]},
	         {text:'支行考核折算分',
            	columns:[{header:'业务量',dataIndex:'zsf_ywl_wd',editor:{},align:'left'},
            	         {header:'标准产品',dataIndex:'zsf_bzcp_wd',editor:{},align:'left'},
            	         {header:'特色业务',dataIndex:'zsf_tscp_wd',editor:{},align:'left'},
            	         {header:'定性',dataIndex:'zsf_dx_wd',editor:{},align:'left'}]},
	         {text:'网点考核折算分',
            	columns:[{header:'业务量',dataIndex:'zsf_ywl_wd',editor:{},align:'left'},
            	         {header:'标准产品',dataIndex:'zsf_bzcp_wd',editor:{},align:'left'},
            	         {header:'特色业务',dataIndex:'zsf_tscp_wd',editor:{},align:'left'},
            	         {header:'定性',dataIndex:'zsf_dx_wd',editor:{},align:'left'}]}]
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
						fileDownload("jxDownload.html?tx_code=10000072");
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
		setTimeout(function(){tableStore.reload();},100);//tableStore属性autoLoad:true，可能存在加载不成功的情况，故采用延时加载解决该问题。2015-11-10
	}	
});

