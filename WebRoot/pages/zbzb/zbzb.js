// JavaScript Document
var height;
var updFormPanel;

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'js/ext');
Ext.require([
 	'Ext.form.Panel',
 	'Ext.ux.form.MultiSelect',//不支持ie6
	'Ext.ux.form.ItemSelector',//不支持ie6
	'Ext.tip.QuickTipManager',
	'Ext.ux.ajax.JsonSimlet',//不支持ie6
	'Ext.ux.ajax.SimManager',//不支持ie6
	'Ext.ux.ajax.DataSimlet',//不支持ie6
	'Ext.ux.ajax.SimXhr',//不支持ie6
	'Ext.ux.ajax.Simlet'//不支持ie6
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
		//alert(top.varpool.userId);
		//var userId = top.varpool.userId;
		Ext.Loader.require("js.comm");
		Ext.Loader.setPath('com.nantian.common','js/public');
		/*********显示开始**********/
		var myColumns = [ 
		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'center'}),//行号
		    {header:'考核内容',dataIndex:'nr_name',width:150,sortable:false,align:'left'},
			{header:'比例下限',dataIndex:'min',width:100,sortable:false,align:'left'},
			{header:'比例上限',dataIndex:'max',width:100,sortable:false,align:'left'}
		];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
			id:'myGridPanel',
			title:'总包占比信息',
			rowLines:true,
			columnLines:false,
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
				checkOnly:false//,//checkOnly只能通过点击复选框选择（默认点击行也可选择）
			}),
			tbar:[/*{
				text:'添加',
				cls:'x-btn-text-icon',
				icon:'images/ext/add.gif',
				handler:function(){
					showAdd();
				}
			},{
				text:'删除',
				cls:'x-btn-text-icon',
				iconCls:'Delete',
				handler:function(){
					var records = myGrid.getSelectionModel().getSelection();				
					var selectValid = true;//已选记录合法性校验
					for (var i = 0; i < records.length; i++) {
						if(records[i].data.used == '1')
							selectValid = false;
					}
					if(selectValid)
						new comm().handleDelBat(myGrid,"param_khrw.html","10000075","gridStoreId");
					else
						msgAlert("不能删除【往期考核】任务!");
				}
			}*/{
				text:'编辑',
//				cls:'x-btn-text-icon',
				iconCls:'Tableedit',
				handler:function(){
//					recordUpd();
					showAdd(myGrid);
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
				region:'center',
				collapsible:false,
				layout:'fit',
				border:false,
				items:[myGrid],
				margin:'0 0 4 0'
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
		setTimeout(function(){
			tableStore.reload();
		},1000);
	}	
});

