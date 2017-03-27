
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
		MessagePush.onPageLoad(userId);
		Ext.Loader.require("js.comm");
		Ext.require(['Ext.data.*', 'Ext.grid.*']);
		/*********查询开始**********/
		//机构部门下拉树（查询面板）
		
		var yearCombo = Ext.create('yearCombo',{
			fieldLabel:'请选择年份',
			labelAlign:"right",
//			width:380,
			frame:true,
			border:true,
			name:'rpTimeQuery'
		});
		var checkForm = Ext.create('Ext.form.Panel',{
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
				items:[new queryTextField({
					columnWidth:.5,
					items:[yearCombo],
					border:true
				}),new queryTextField({
					columnWidth:.5,
					items:[new queryTextInput({
						fieldLabel:'用户号',
						name:'userIdQuery'
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
		    Ext.create('Ext.grid.RowNumberer',{text:'行号',width:40,align:'left'}),//行号
			{header:'员工编码',dataIndex:'userId',width:100,sortable:true,align:'left'},
			{header:'姓名',dataIndex:'userName',width:100,sortable:false,align:'left'},
			{header:'年份',dataIndex:'rpTime',sortable:false,align:'left',width:100},
			{header:'奖励积分',dataIndex:'rpPoint',sortable:false,align:'left',width:100},
			{header:'积分奖励原因',dataIndex:'rpMsg',sortable:false,align:'left',width:200}
		];
		var sizeCombo = new sizeComboPrt();
		var myGrid = Ext.create('Ext.grid.GridPanel',{
			id:'myGridPanel',
			title:'员工奖励信息',
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
			tbar:[{
						text:'添加',
						cls:'x-btn-text-icon',
						icon:'images/ext/add.gif',
						handler:function(){
//							showAdd();
							zhxf();
						}
			},{
				text:'删除',
				cls:'x-btn-text-icon',
				icon:'images/ext/delete.gif',
				handler:function(){
//					handleDel(myGrid);
//					handleDelBat(myGrid,"rpManage.html","30053","gridStoreId");
					new comm().handleDelBat(myGrid,"rpManage.html","30052","gridStoreId");
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
				layout:'fit',
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
	}	
});

