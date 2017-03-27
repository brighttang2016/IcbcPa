// JavaScript Document
var height;
var updFormPanel;

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
		'App.js':'pages/js',
		'App.ux':'lib'
	}
});
Ext.Loader.setPath('com.nt.cmp','js/component');

Ext.application({
	name:'roleInfo',
	launch:function(){
		var userId = top.varpool.userId;
		//alert(top.varpool.userId);
		Ext.Loader.require("js.comm");
		Ext.Loader.setPath('com.nantian.common','js/public');
		MessagePush.onPageLoad(userId);//注册监听
		var orgCmp = new orgCmponent();//机构组件对象
//		var treeUpd = orgCmp.getTreeUpd();//更新树panel
		var treeQuery = orgCmp.getTreeQuery();//查询树panel
		var orgInfoQuery = orgCmp.getOrgInfoQuery(treeQuery);//查询树panel
		var tableStore;
		/*********查询开始**********/
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
					items:[orgInfoQuery]
				}),new queryTextField({
					columnWidth:.5,
					items:[new queryTextInput({
						fieldLabel:'用户号',
						name:'userId'
					})]
				})]
			},{
				layout:'column',
				frame:true,
				baseCls:'my-panel-no-border',
				items:[{
					frame:true,
//					border:false,
					baseCls:'my-panel-no-border',
					columnWidth:.5,
					layout:{
						type:'hbox',
						pack:'center'
					},
					items:[{
						xtype:'label',
						text:'【机构部门:】',
						id:'queryStrId'
					}]
				}]
			}],
			buttons:[{
				text:'查询',
				handler:function(){
					comm_query('gridStoreId','checkForm');
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
		
		var myColumns = [ 
 		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
 		    {header:'机构名称',dataIndex:'orgName',width:150,sortable:false,align:'left',flex:1},
 			{header:'考核岗位',dataIndex:'jbjx_pid',width:100,sortable:false,align:'left',
 				renderer:function(value){
 					return value=='1'?"柜员":"客户经理";
 				}
 			},
 			{header:'考核范围',dataIndex:'khfw',width:100,sortable:false,align:'left',
 				renderer:function(value){
 					return value=='1'?"支行考核":"网点考核";
 				}
 			},
 			{text:'业务量',
 				columns:[{header:'下限',dataIndex:'ywl_xx',width:80,sortable:false,align:'center'},
 			         	{header:'上限',dataIndex:'ywl_sx',width:80,sortable:false,align:'center'}]},
 			{text:'标准产品',
 			    columns:[{header:'下限',dataIndex:'bzcp_xx',width:80,sortable:false,align:'center'},
 			 			{header:'上限',dataIndex:'bzcp_sx',width:80,sortable:false,align:'center'}]},
 		    {text:'支行特色',
 			    columns:[{header:'下限',dataIndex:'zhts_xx',width:80,sortable:false,align:'center'},
 			 			{header:'上限',dataIndex:'zhts_sx',width:80,sortable:false,align:'center'}]},
 		    {text:'定性',
 			    columns:[{header:'下限',dataIndex:'dx_xx',width:80,sortable:false,align:'center'},
 			 			{header:'上限',dataIndex:'dx_sx',width:80,sortable:false,align:'center'}]}
 			
 		];
 		var sizeCombo = new sizeComboPrt();
 		var tableStore = Ext.data.StoreManager.lookup("gridStoreId");
 		var myGrid = Ext.create('Ext.grid.GridPanel',{
// 			var myGrid = new Ext.grid.GridPanel({
 				id:'myGridPanel',
 				title:'权重区间信息',
 				rowLines:true,
 				columnLines:false,
 				scroll:true,
 				border:true,
// 				layout:'fit',
 				width:'100%',
 				height:'100%',
 				viewConfig:{
// 					forceFit:true,
// 					stripeRows:true
 				},
 				store:tableStore,
 				features:[{
 					ftype:'summary'
 				},{
 					ftype:'summary'
 				}],
 				columns:myColumns,
 				selModel:Ext.create('Ext.selection.CheckboxModel',{
 					checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
 				}),
 				tbar:[
 				      	{	
 							text:'上传批量文件',
 							cls:'x-btn-text-icon',
 							iconCls:'Folderup',
 							handler:function(){
 								fileUpload("10000073","权重区间批量");
 							}
 						},{	
 							text:'下载批量模版文件',
 							cls:'x-btn-text-icon',
 							iconCls:'Packagedown',
 							handler:function(){
 								fileDownload("downLoad.html?tx_code=10000073");
 							}
 						},{
 							text:'删除',
 							cls:'x-btn-text-icon',
 							iconCls:'Delete',
 							handler:function(){
 								new comm().handleDelBat(myGrid,"param_qzqj.html","10000075","gridStoreId");
 							}
 						}],
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
		treeQuery.on('itemclick',function(view,record,item,index,e,obj){
			orgInfoQuery.setValue(record.get("menuId"));
			orgInfoQuery.setRawValue(record.get("menuName"));
			//alert(getNode("treeStoreId",record.get("menuId")));
			orgId = getLatestOrgId(getNode("treeStoreId",record.get("menuId")),"treeStoreId");//获取当前操作节点最接近机构id
//			alert("orgId:"+orgId);
			orgInfoQuery.collapse();
			checkForm.getForm().findField("orgIdQuery").setValue(orgId);
			checkForm.getForm().findField("deptIdQuery").setValue(record.get("menuId"));
			
			var orgArr = new Array();
			node = getNode("treeStoreId",record.get("menuId"));//初始节点
			getUpOrgAll(node,orgArr);
			var queryStr = getQueryStr(orgArr);
			Ext.getCmp("queryStrId").setText(queryStr);
		});
	}	
});

