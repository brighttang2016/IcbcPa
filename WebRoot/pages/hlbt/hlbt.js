// JavaScript Document
//var height;
//var updFormPanel;
//var tableStore;
//var win;
//var checkForm;
//var winUpd;
//var winAdd;
//var checkForm;
//var role_id = 0;

//var leafTag;
//var pId;
//var pName;
//var menuId;
//var menuName;
//var menuType;
//var recordNow;
//var orgId;
var udpFormPanelAdd;
//var orgInfoUpd;//机构部门下拉框（用户更新）
/*Ext.Loader.setConfig({enabled: true});
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
]);*/
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
//		Ext.QuickTips.init();
		/*********显示开始**********/
		//列
		var myColumns = [ 
		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
			{header:'开始年限',dataIndex:'yearStart',width:100,sortable:true,align:'center'},
			{header:'结束年限',dataIndex:'yearEnd',width:100,sortable:false,align:'center'},
			{header:'行龄补贴',dataIndex:'subsity',width:130,sortable:false,align:'center'},
			{header:'说明',dataIndex:'note',sortable:false,align:'center',flex:1},
			{header:'修改',width:80,align:'center',
				xtype:'actioncolumn',
				items:[{
					width:40,
					icon:'images/ext/edit.png',
					handler:function(grid,rowIndex,colIndex){
						var rec = grid.getStore().getAt(rowIndex);
						handleUpdate(rec,"gridStoreId","udpFormPanelAdd");
					}
				}]
			},{header:'删除',width:80,align:'center',
				xtype:'actioncolumn',
				items:[{
					width:40,
					icon:'images/ext/delete.gif',
					handler:function(var1,var2,var3){//var1:当前Ext.grid.Panel对象，var2:当前操作行，var3:当前操作列
						var rec = var1.getStore().getAt(var2);
						handleDel(rec,"ssManage.html","gridStoreId");
					}
				}]
			}
		];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			title:'行龄补贴信息',
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
			selModel:Ext.create('Ext.selection.CheckboxModel',{
				checkOnly:true//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),
			tbar:[
					{
						text:'添加',
						qtip:'test',
						cls:'x-btn-text-icon',
						icon:'images/ext/add.gif',
						handler:function(){
							showAdd("udpFormPanelAdd");
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
				region:'center',
				collapsible:false,
				layout:'fit',
				border:false,
				items:[myGrid],
				margins:'0 0 4 0'
			}]
		});
		
		/************更新开始************/
		try{
			udpFormPanelAdd.removeAll();
		}catch(e){}
		udpFormPanelAdd = new updFormPanelPrt({
			id:'udpFormPanelAdd',
			items:[{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[tran_code,{
					xtype:'hidden',
					name:'ssId',
					id:'ssId',
					fieldLabel:'行龄补贴id'
				}]
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
//				frame:true,
				items:[new queryTextField({
					columnWidth:.5,
					items:[new textFieldInt({
						name:'yearStart',
						fieldLabel:'起始年限',
						readOnly:false
					})]
				}),new queryTextField({
					columnWidth:.5,
					items:[new textFieldInt({
						name:'yearEnd',
						fieldLabel:'终止年限'
					})]
				})]
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[new queryTextField({
					columnWidth:.5,
					items:[new textFieldInt({
						name:'subsity',
						fieldLabel:'行龄补贴'
					})]
				}),new queryTextField({
					columnWidth:.5,
					items:[new inputTextField({
						name:'note',
						fieldLabel:'说明'
					})]
				})]
			}],
			buttons:[{
				text:'保存',
				handler:function(){
					comm_dir("ssManage.html","gridStoreId","udpFormPanelAdd");
				}
			},{
				text:'取消',
				handler:handlerCancle
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
		
	}	
});

