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
		MessagePush.onPageLoad(userId);//注册监听
		/*********查询开始**********/
		//机构部门下拉树（查询面板）
		var orgCmp = new orgCmponent();//机构组件对象
		var tree = orgCmp.getTreeQuery();//机构树对象
		orgInfoQuery = orgCmp.getOrgInfoQuery(tree);//机构下拉框对象
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
				}
			}]
		});
		/*********查询结束**********/
		
		/*********显示开始**********/
		//列
		var myColumns = [ 
//		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
//			{header:'员工id',dataIndex:'userId',width:80,sortable:true,align:'center',locked:true},
			{header:'姓名',dataIndex:'name',width:80,sortable:false,align:'center',locked:true},
			{header:'机构名称',dataIndex:'orgName',width:130,sortable:false,align:'center',locked:true},
			{header:'部门名称',dataIndex:'depName',width:150,sortable:false,align:'center',locked:true},
//			{header:'性别',dataIndex:'sex',width:100,sortable:false,align:'center'},
			{header:'总行基础学分',dataIndex:'zhjf',width:100,sortable:true,align:'center'},
			
			{header:'市行基础学分',columns:[
			    {header:'参训参考得分',dataIndex:'cxck',width:100,sortable:true,align:'center'},
			    {header:'资格认证得分',dataIndex:'zgrz',width:100,sortable:true,align:'center'},
			    {header:'组织管理得分',dataIndex:'zzgl',width:100,sortable:true,align:'center'}
			]},
			{header:'附加学分',columns:[
  			    {header:'附加学分1',dataIndex:'fjxf1',width:100,sortable:true,align:'center'},
  			    {header:'附加学分2',dataIndex:'fjxf2',width:100,sortable:true,align:'center'},
  			    {header:'附加学分3',dataIndex:'fjxf3',width:100,sortable:true,align:'center'},
  			    {header:'附加学分4',dataIndex:'fjxf4',width:100,sortable:true,align:'center'},
			    {header:'附加学分5',dataIndex:'fjxf5',width:100,sortable:true,align:'center'},
			    {header:'附加学分6',dataIndex:'fjxf6',width:100,sortable:true,align:'center'},
			    {header:'附加学分7',dataIndex:'fjxf7',width:100,sortable:true,align:'center'},
  			    {header:'附加学分8',dataIndex:'fjxf8',width:100,sortable:true,align:'center'}
			]},
			{header:'手工调整学分',dataIndex:'sgtz',width:100,sortable:true,align:'center'},
			{header:'个人学分汇总',dataIndex:'all_sum',width:100,sortable:true,align:'center',
				renderer: function(value, metaData, record, rowIdx, colIdx, store, view) {
					var keyArr = new Array();//带求和列名数组
					var varRet = 0;//行个人学分汇总
					keyArr.push("zhjf");
					keyArr.push("cxck");
					keyArr.push('zgrz');
					keyArr.push('zzgl');
					keyArr.push('sgtz');
					keyArr.push('fjxf1');
					keyArr.push('fjxf2');
					keyArr.push('fjxf3');
					keyArr.push('fjxf4');
					keyArr.push('fjxf5');
					keyArr.push('fjxf6');
					keyArr.push('fjxf7');
					keyArr.push('fjxf8');
					for(var i = 0;i < keyArr.length;i++){
						var tempVal = parseInt(record.get(keyArr[i]));
						if(!isNaN(tempVal)){
							varRet += tempVal;
						}
					}
					return varRet;
	            }
			}
		];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			columnLines: true,
			title:'学分信息',
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
			/*features:[{
				ftype:'summary'
			},{
				ftype:'summary'
			}],*/
			columns:myColumns,
			/*selModel:Ext.create('Ext.selection.CheckboxModel',{
				checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),*/
			tbar:[{
						text:'学分维护',
						cls:'x-btn-text-icon',
						icon:'images/ext/xfwh.png',
						menu:[{
							text:'总行基础学分维护',
//							cls:'Worldnight',
//							icon:'images/ext/add.gif',
							iconCls:'zhxf',
							handler:function(){
								zhxf();
							}
						},{
							text:'市行基础学分维护',
//							icon:'images/ext/add.gif',
							iconCls:'shxf',
//							iconCls:'Note',
							handler:function(){
								shxf();
							}
						},{
							text:'附加学分维护',
//							icon:'images/ext/add.gif',
							iconCls:'fjxf',
							handler:function(){
								fjxf();
							}
						},{
							text:'学分手工调整',
//							icon:'images/ext/sgtz.png',
							iconCls: 'sgtz',
							handler:function(){
								sgtz();
							}
						}]
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
		
		tree.on('itemclick',function(view,record,item,index,e,obj){
			orgInfoQuery.setValue(record.get("menuId"));
			orgInfoQuery.setRawValue(record.get("menuName"));
			//alert(getNode("treeStoreId",record.get("menuId")));
			orgId = getLatestOrgId(getNode("treeStoreId",record.get("menuId")),"treeStoreId");//获取当前操作节点最接近机构id
//			alert("orgId:"+orgId);
			orgInfoQuery.collapse();
			checkForm.getForm().findField("orgIdQuery").setValue(orgId);
			checkForm.getForm().findField("deptIdQuery").setValue(record.get("menuId"));
		});
	}	
});

