
var udpFormPanelAdd;
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
		
		/*********显示开始**********/
		//列
		var myColumns = [ 
		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'left'}),//行号
			{header:'考核结果',dataIndex:'assptName',width:100,sortable:true,align:'left'},
			{header:'积分',dataIndex:'point',width:100,sortable:false,align:'left'},
			{header:'考核条件',dataIndex:'cond',sortable:false,align:'left'},
			{header:'说明',dataIndex:'note',sortable:false,align:'left',width:400},
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
			}/*,{header:'删除',width:80,align:'center',
				xtype:'actioncolumn',
				items:[{
					width:40,
					icon:'images/ext/delete.gif',
					handler:function(var1,var2,var3){//var1:当前Ext.grid.Panel对象，var2:当前操作行，var3:当前操作列
						var rec = var1.getStore().getAt(var2);
						handleDel(rec,"ssManage.html","gridStoreId");
					}
				}]
			}*/
		];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
//		var myGrid = new Ext.grid.GridPanel({
			id:'myGridPanel',
			title:'考核积分对照表',
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
					/*{
						text:'添加',
						cls:'x-btn-text-icon',
						icon:'images/ext/add.gif',
						handler:function(){
							showAdd("udpFormPanelAdd");
						}
					}*/
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
		var assCombo =Ext.create('assCombo',{
			name:'assptName',
			id:'assptName'
		}) ;
		/*new assCombo({
			name:'assptName',
			id:'assptName'
		});*/
		udpFormPanelAdd = new updFormPanelPrt({
			id:'udpFormPanelAdd',
			items:[{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[tran_code,{
					xtype:'hidden',
					name:'assptId',
					id:'assptId',
					fieldLabel:'考核结果id'
				}]
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
//				frame:true,
				items:[new queryTextField({
					columnWidth:.5,
					items:[/*new inputTextField({
						name:'assptName',
						fieldLabel:'考核结果',
						readOnly:false
					})*/assCombo]
				}),new queryTextField({
					columnWidth:.5,
					items:[new textFieldNum({
						name:'point',
						fieldLabel:'积分'
					})]
				})]
			},{
				layout:'column',
				baseCls:'my-panel-no-border',
				items:[new queryTextField({
					columnWidth:.5,
					items:[new inputTextField({
						name:'note',
						fieldLabel:'说明',
						maxLength:'100'
					})]
				}),new queryTextField({
					columnWidth:.5,
					items:[new textFieldNum({
						name:'cond',
						fieldLabel:'考核条件',
						maxLength:'100'
					})]
				})]
			}],
			buttons:[{
				text:'保存',
				handler:function(){
					comm_dir("assptManage.html","gridStoreId","udpFormPanelAdd");
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
		/*assCombo.on('select',function(combo){
			alert(combo.getValue());
		});*/
	}	
});

