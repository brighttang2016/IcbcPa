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
		'App.js':'pages/js',
		'App.ux':'lib'
	}
});
Ext.Loader.setPath('com.nt.cmp','js/component');

Ext.application({
	name:'roleInfo',
	launch:function(){
		Ext.Loader.require("js.comm");
		Ext.Loader.setPath('com.nantian.common','js/public');
		MessagePush.onPageLoad(userId);//注册监听
		
		var orgCmp = new orgCmponent();//机构组件对象
		var treeUpd = orgCmp.getTreeUpd();//更新树panel
		var orgInfoUpd = orgCmp.getOrgInfoUpd(treeUpd); //更新panel
		var treeQuery = orgCmp.getTreeQuery();//查询树panel
		var orgInfoQuery = orgCmp.getOrgInfoQuery(treeQuery);//查询树panel
		
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
				})/*,new queryTextField({
					columnWidth:.5,
					items:[new queryTextInput({
						fieldLabel:'用户号',
						name:'userId'
					})]
				})*/]
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
		
		/*********显示开始**********/
		//列'orgid','orgname','org_point','seq_num','zb_name','unit','point'
		var myColumns = [ 
		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
		    {header:'机构编号',dataIndex:'orgid',width:130,sortable:true,align:'center'},
			{header:'机构名称',dataIndex:'orgname',width:130,sortable:true,align:'center',flex:1},
			{ text: '指标序号',dataIndex:'seq_num',width:130,sortable:true,align:'center'},
			{ text: '指标名称',dataIndex:'zb_name',width:200,sortable:true,align:'center'},
			{ text: '机构设定分值',  dataIndex: 'org_point',width:130,sortable:true,align:'center'}
		];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			title:'支行特色业务指标范围信息表',
			scroll:true,
			border:true,
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
			selModel:Ext.create('Ext.selection.CheckboxModel',{
				checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),
			tbar:[{
						text:'添加',
						cls:'x-btn-text-icon',
						iconCls:'Add',
						handler:function(){
							zhxf();
						}
			},{
				text:'删除',
				cls:'x-btn-text-icon',
				iconCls:'Delete',
				handler:function(){
					new comm().handleDelBat(myGrid,"zbfwControler.html","30153","gridStoreId");
				}
			},{
				text:'编辑',
				cls:'x-btn-text-icon',
				iconCls:'Tableedit',
				handler:function(){
					showDetail(myGrid);
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
		
		/************更新结束************/
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

